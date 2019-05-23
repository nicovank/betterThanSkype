package Client.Sockets;

import Client.Events.*;
import Client.Main;
import crypto.AES;
import crypto.CryptoException;
import crypto.RSA;
import javafx.application.Platform;
import packets.*;
import utils.Address;
import utils.Constants;

import java.io.IOException;
import java.net.*;
import java.security.PublicKey;
import java.sql.Time;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * this class handles sending and receiving packets from the the server and clients.
 * this includes encryption and firing events.
 *
 * @author Jim Spagnola
 * @author Nick Esposito
 */
public class RoomSocket implements IRoomSocket, Runnable {
    private final InetAddress SERVER_ADDRESS;
    private final DatagramSocket SERVER_SOCKET;
    private final MulticastSocket CLIENT_SOCKET;
    private final RSA KEYS;
    private final PublicKey OUR_KEY;
    private final PublicKey SERVER_PUBLIC_KEY;
    private final ConcurrentLinkedQueue<DatagramPacket> IO_QUEUE;
    private final AtomicLong TIME_STAMP;
    private final ConcurrentLinkedQueue<ExpectedPacket> EXPECTED_PACKETS;
    private AES aes;
    private InetAddress currentMulticastAddress;
    private String username;

    public RoomSocket() throws IOException, CryptoException {
        //setup SERVER_SOCKET
        SERVER_SOCKET = new DatagramSocket(Constants.PORTS.SERVER);
        CLIENT_SOCKET = new MulticastSocket(Constants.PORTS.CLIENT);
        CLIENT_SOCKET.setLoopbackMode(true);
        SERVER_SOCKET.setSoTimeout(10000);
        SERVER_ADDRESS = InetAddress.getByName("pi.cs.oswego.edu");

        //generate our RSA security key.
        KEYS = new RSA();
        OUR_KEY = KEYS.getPublicKey();

        //inform our Server of our existence.
        PublicKeyPacket prPacket = new PublicKeyPacket(OUR_KEY);
        byte[] bytes = new byte[Constants.MAX_PACKET_SIZE];
        DatagramPacket sendPacket = prPacket.getDatagramPacket(SERVER_ADDRESS, Constants.PORTS.SERVER);
        SERVER_SOCKET.send(sendPacket);

        //Get ack and key from server
        boolean successfulServerAck = false;
        DatagramPacket receivePacket = new DatagramPacket(bytes, bytes.length);
        Packet p = null;

        while (!successfulServerAck) {
            SERVER_SOCKET.receive(receivePacket);
            try {
                p = Packet.parse(Arrays.copyOf(receivePacket.getData(), receivePacket.getLength()));
                successfulServerAck = true;
            } catch (InvalidPacketFormatException e) {
                e.printStackTrace();
            }
        }
        SERVER_PUBLIC_KEY = ((PublicKeyPacket) p).getPublicKey();

        //concurrency setup
        IO_QUEUE = new ConcurrentLinkedQueue<>();
        EXPECTED_PACKETS = new ConcurrentLinkedQueue<>();
        TIME_STAMP = new AtomicLong(0);
    }

    /**
     * This method is the entry point into the client, where actual sending and receiving happens.
     * Sending is
     */
    @Override
    public void run() {
        try {
            SERVER_SOCKET.setSoTimeout(100);
            CLIENT_SOCKET.setSoTimeout(100);
        } catch (SocketException e) {
            System.out.println("Thread Socket Error has occurred");
            return;
        }

        while (!Thread.interrupted()) {
            //always send before receiving

            //This section handles timeouts, if an expected packet is not received .5s after it is supposed to, the packet is resent
            for (ExpectedPacket ex : EXPECTED_PACKETS) {
                if ((System.nanoTime() - ex.getTime()) > 1000000000L) {
                    ex.setTime(System.nanoTime());
                    if (ex.getPacket().getOperationCode() < 8) {
                        IO_QUEUE.offer(ex.getOriginal().getDatagramPacket(SERVER_ADDRESS, Constants.PORTS.SERVER));
                    } else {
                        IO_QUEUE.offer(ex.getOriginal().getDatagramPacket(currentMulticastAddress, Constants.PORTS.CLIENT));
                    }
                }
            }
            while (!IO_QUEUE.isEmpty()) {
                DatagramPacket packet = IO_QUEUE.poll();
                try {
                    if (packet.getPort() == Constants.PORTS.SERVER) {
                        SERVER_SOCKET.send(packet);
                    } else if (packet.getPort() == Constants.PORTS.CLIENT) {
                        CLIENT_SOCKET.send(packet);
                        System.out.println("Sent to client opcode: " + packet.getData()[1]);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //handle Server packet if there is one.
            //Server requests are the priority
            try {
                byte[] bytes = new byte[Constants.MAX_PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                SERVER_SOCKET.receive(packet);
                Packet p = Packet.parse(Arrays.copyOf(packet.getData(), packet.getLength()), KEYS);
                Queue<ExpectedPacket> q = new LinkedList<>();
                for (ExpectedPacket ex : EXPECTED_PACKETS) {
                    if (p.equals(ex.getPacket())) {
                        q.add(ex);
                    }
                }
                q.forEach(EXPECTED_PACKETS::remove);
                handleServerPacket(p);
            } catch (SocketTimeoutException e) {

            } catch (IOException | InvalidPacketFormatException | CryptoException e) {
                e.printStackTrace();
            }

            //handle Client packet if there is one.
            try {
                byte[] bytes = new byte[Constants.MAX_PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(bytes, bytes.length);
                CLIENT_SOCKET.receive(packet);
                Packet p = Packet.parse(Arrays.copyOf(packet.getData(), packet.getLength()), aes);
                System.out.println("Received packet  " + p.getOperationCode());
                EXPECTED_PACKETS.removeIf(l -> l.getPacket().equals(p));
                System.out.println(EXPECTED_PACKETS.size());
                handleClientPacket(p);
            } catch (SocketTimeoutException e) {

            } catch (IOException | InvalidPacketFormatException | CryptoException e) {
                e.printStackTrace();
            }

        }
    }

    /**
     * handle all packets a client should be expected to receive from the server.
     *
     * @param packet The packet that has been received, determined to be from the server, based on the opcode
     */
    private void handleServerPacket(Packet packet) {

        switch (packet.getOperationCode()) {
            case Constants.OPCODE.MRCS:
                SuccessfulMulticastRoomCreationPacket s = (SuccessfulMulticastRoomCreationPacket) packet;
                //handle room stuff
                currentMulticastAddress = s.getIP();
                try {
                    CLIENT_SOCKET.joinGroup(currentMulticastAddress);
                } catch (IOException e) {

                }
                Platform.runLater(() ->
                        Main.getInstance().getEventNode().fireEvent(new RoomResponseEvent(RoomResponseEvent.CREATE_ROOM, true, s.getName()))
                );
                break;
            case Constants.OPCODE.JOINSUC:
                JoinRoomSuccessPacket j = (JoinRoomSuccessPacket) packet;
                currentMulticastAddress = j.getIp();
                try {
                    CLIENT_SOCKET.joinGroup(currentMulticastAddress);
                } catch (IOException e) {

                }
                Platform.runLater(() ->
                        Main.getInstance().getEventNode().fireEvent(new RoomResponseEvent(RoomResponseEvent.JOIN_ROOM, true, j.getName()))
                );
                break;
        }
    }

    /**
     * This method takes in a generic client packet, and determines which type of packet it is based on opcode.
     * Then, its appropriate handler is called
     *
     * @param packet A received packet which is determined to be from another client based on its opcode
     */
    private void handleClientPacket(Packet packet) {
        //handle all packets a client should be expected to receive
        switch (packet.getOperationCode()) {
            case Constants.OPCODE.ANNOUNCE:
                handleAnnouncement((AnnouncePacket) packet);
                break;
            case Constants.OPCODE.ANNACK:
                handleAnnouncementAck((AnnounceAckPacket) packet);
                break;
            case Constants.OPCODE.ANNACKACK:
                handleAnnouncementAckAck((AnnounceAckAckPacket) packet);
                break;
            case Constants.OPCODE.MESSAGE:
                handleMessage((MessagePacket) packet);
                break;
            case Constants.OPCODE.MESSAGEACK:
                handleMessageAck((MessageAckPacket) packet);
                break;
            case Constants.OPCODE.LEAVEROOM:
                handleLeaveRoom((LeaveRoomPacket) packet);
                break;
            case Constants.OPCODE.KEEPALIVE:
                handleKeepAlive((KeepAlivePacket) packet);
                break;
            case Constants.OPCODE.KEEPALIVEACK:
                handleKeepAliveAck((KeepAliveAckPacket) packet);
                break;
        }
    }

    /**
     * This method handles the case where an incoming packet is an announcement packet.
     * It adds the announced user to the current list of users, then sends out an acknowledgement
     *
     * @param packet A packet determined to be of type AnnouncePacket
     */
    private void handleAnnouncement(AnnouncePacket packet) {
        TIME_STAMP.getAndIncrement();
        AnnounceAckPacket ackPacket = packet.createAck(TIME_STAMP.get(), username);
        ExpectedPacket ex = new ExpectedPacket(ackPacket.getAckAck(), TIME_STAMP.get(), ackPacket);
        EXPECTED_PACKETS.offer(ex);
        try {
            IO_QUEUE.offer(ackPacket.getDatagramPacket(currentMulticastAddress, Constants.PORTS.CLIENT, aes));
        } catch (CryptoException e) {
            System.exit(1);
        }
        Platform.runLater(() ->
                Main.getInstance().getEventNode().fireEvent(new UserEvent(UserEvent.JOIN_EVENT, packet.getNickName()))
        );
    }

    /**
     * Handles the case where an incoming packet is an announce acknowledgement.
     * This method adds the user from the ack to the current list of users, and will receive as many acks as there are users in the room
     *
     * @param packet A packet determined to be of type AnnounceAckPacket
     */
    private void handleAnnouncementAck(AnnounceAckPacket packet) {
        TIME_STAMP.set(Math.max(packet.getTimestamp(), TIME_STAMP.get()));
        if (!packet.getNickName().equals(username)) {
            return;
        }
        AnnounceAckAckPacket ackPacket = packet.getAckAck();
        DatagramPacket payload = null;
        try {
            payload = ackPacket.getDatagramPacket(currentMulticastAddress, Constants.PORTS.CLIENT, aes);
        } catch (CryptoException e) {
            System.exit(1);
        }
        IO_QUEUE.offer(payload);
        Platform.runLater(() ->
                Main.getInstance().getEventNode().fireEvent(new UserEvent(UserEvent.JOIN_EVENT, packet.getOtherNickName()))
        );
    }

    /**
     * This method fires when receiving an AckAck
     * No additional action is needed to handle this except incrementing the timestamp
     *
     * @param packet A packet determined to be of type AnnounceAckAckPacket
     */
    private void handleAnnouncementAckAck(AnnounceAckAckPacket packet) {
        TIME_STAMP.getAndIncrement();

    }

    /**
     * This method fires when a message is received.
     * Handles consensus by using lamport timestamps for causal consistency.
     * Updates client timestamp based on the max of the packet's timestamp and the client's timestamp
     * It creates a Message object from the contents of the MessagePacket, and adds it to the gui, also creates and sends an ack
     *
     * @param packet A packet determined to be of type MessagePacket. Contains the message and information about the sender
     */
    private void handleMessage(MessagePacket packet) {

        MessageAckPacket ackPacket = packet.createAck();
        IO_QUEUE.offer(ackPacket.getDatagramPacket(currentMulticastAddress, Constants.PORTS.CLIENT));

        //resolve timeStamp
        //long timestamp = Math.min(TIME_STAMP.getAndIncrement(), packet.getTimestamp());
        long timestamp = TIME_STAMP.getAndSet(Math.max(packet.getTimestamp(), TIME_STAMP.get()));

        //create message
        Message m = new Message(packet.getMessage(), packet.getNickName(), timestamp);
        Platform.runLater(() ->
                Main.getInstance().getEventNode().fireEvent(new MessageReceivedEvent(MessageReceivedEvent.MESSAGE_EVENT, m))
        );

    }

    /**
     * Handles message acks
     * No further action required other than incrementing timestamp
     *
     * @param packet A packet determined to be of type MessageAckPacket.
     */
    private void handleMessageAck(MessageAckPacket packet) {
        TIME_STAMP.getAndIncrement();

    }

    /**
     * Handles when another user in the room has left.
     * Removes that user from the list of users in the GUI
     *
     * @param packet A packet determined to be of type LeaveRoomPacket. Contains information about user that left.
     */
    private void handleLeaveRoom(LeaveRoomPacket packet) {
        TIME_STAMP.getAndIncrement();
        Platform.runLater(() ->
                Main.getInstance().getEventNode().fireEvent(new UserEvent(UserEvent.LEAVE_EVENT, packet.getNickname()))
        );
    }


    private void handleKeepAlive(KeepAlivePacket packet) {
        TIME_STAMP.getAndIncrement();
        KeepAliveAckPacket ackPacket = new KeepAliveAckPacket();
        IO_QUEUE.offer(ackPacket.getDatagramPacket(SERVER_ADDRESS, Constants.PORTS.SERVER));
        //TODO implement keepalive logic

    }

    private void handleKeepAliveAck(KeepAliveAckPacket packet) {
        TIME_STAMP.getAndIncrement();
        //TODO implement keepalive logic

    }

    //IMPORTANT ALL OF THESE MESSAGES SHOULD END WITH SENDING A PACKET TO IOQUEUE AND INCREMENTING TIME_STAMP

    /**
     * Fires when a user attempts to create a room.
     * Initializes the aes object with room password, and stores the room password and client username values input from the GUI
     * Sends a room creation request packet to the server, and adds its expected ack to the EXPECTED_PACKETS list for timeout control
     *
     *
     * @param room The current room name
     * @param username Client's username
     * @param password Room password
     */
    @Override
    public void attemptToCreateRoom(String room, String username, String password) {
        try {
            aes = new AES(password);
        } catch (CryptoException e) {
            e.printStackTrace();
            System.exit(1);
        }
        this.username = username;
        RoomCreationRequestPacket creationRequestPacket = new RoomCreationRequestPacket(username, room, password, Constants.TYPE.MULTICAST);
        DatagramPacket packet = creationRequestPacket.getDatagramPacket(SERVER_ADDRESS, Constants.PORTS.SERVER);
        IO_QUEUE.offer(packet);
        SuccessfulMulticastRoomCreationPacket suc = new SuccessfulMulticastRoomCreationPacket(room, password, currentMulticastAddress, Constants.PORTS.SERVER);
        ExpectedPacket ex = new ExpectedPacket(suc, TIME_STAMP.get(), creationRequestPacket);
        EXPECTED_PACKETS.offer(ex);
        TIME_STAMP.getAndIncrement();
    }

    /**
     * Fires when a user attempts to join a room.
     * As either create or join could happen independently, aes is initialized with the password input in the GUI
     *
     * @param username The client's username
     * @param room The room the client is attempting to join
     * @param password Password the user entered in the GUI
     */

    @Override
    public void attemptToJoinRoom(String username, String room, String password) {
        try {
            aes = new AES(password);
        } catch (CryptoException e) {
            e.printStackTrace();
            System.exit(1);
        }
        JoinRoomRequestPacket joinRequestPacket = new JoinRoomRequestPacket(username, room, password, Constants.TYPE.MULTICAST);
        DatagramPacket packet = joinRequestPacket.getDatagramPacket(SERVER_ADDRESS, Constants.PORTS.SERVER);
        JoinRoomSuccessPacket succ = new JoinRoomSuccessPacket(room, password, SERVER_ADDRESS, Constants.PORTS.SERVER, Constants.TYPE.MULTICAST);
        ExpectedPacket ex = new ExpectedPacket(succ, TIME_STAMP.get(), joinRequestPacket);
        EXPECTED_PACKETS.offer(ex);
        IO_QUEUE.offer(packet);
        TIME_STAMP.getAndIncrement();
    }

    /**
     * Sends an encrypted message to all other clients in the room
     * Fires when a user attempts to send a message from the GUI.
     * A MessagePacket is created from the parameters, and is then turned into an encrypted DatagramPacket
     * An expected ack is added to EXPECTED_PACKETS
     * The encrypted DatagramPacket is then added to the IO_QUEUE to be sent, and the timestamp is incremented
     *
     *
     * @param username The client's username
     * @param message The message in plaintext
     * @param password The room password, also in plaintext
     * @return returns the timestamp incremented
     */
    @Override
    public long sendToEveryone(String username, String message, String password) {
        MessagePacket messagePacket = new MessagePacket(username, message, Constants.TYPE.MULTICAST);
        DatagramPacket packet = null;
        try {
            packet = messagePacket.getDatagramPacket(currentMulticastAddress, Constants.PORTS.CLIENT, aes);
        } catch (CryptoException e) {
            System.exit(1);
        }
        MessageAckPacket ex = messagePacket.createAck();
        ExpectedPacket expectedPacket = new ExpectedPacket(ex, TIME_STAMP.get(), messagePacket);
        EXPECTED_PACKETS.offer(expectedPacket);
        System.out.println("Added send to IOQUEUE");
        IO_QUEUE.offer(packet);
        return TIME_STAMP.getAndIncrement();
    }

    /**
     * Send a LeaveRoomPacket to client and server
     * Does not necessitate ack
     *
     * @param username The client's username
     * @param roomname The current room name
     */
    @Override
    public void sendLeavingMessage(String username, String roomname) {
        LeaveRoomPacket packet = new LeaveRoomPacket(username, roomname);
        try {
            DatagramPacket clientPacket = packet.getDatagramPacket(currentMulticastAddress, Constants.PORTS.CLIENT, aes);
            DatagramPacket serverPacket = packet.getDatagramPacket(SERVER_ADDRESS, Constants.PORTS.SERVER, SERVER_PUBLIC_KEY);
            SERVER_SOCKET.send(serverPacket);
            CLIENT_SOCKET.send(clientPacket);
        } catch (CryptoException | IOException e) {
            System.exit(1);
        }
    }

    /**
     * @deprecated
     * Functionality of this method is included in AnnounceAck handle
     *
     */
    @Override
    public void addToSendList(String nickName, Address address) {
        TIME_STAMP.getAndIncrement();
        Platform.runLater(() ->
                Main.getInstance().getEventNode().fireEvent(new UserEvent(UserEvent.JOIN_EVENT, nickName)));
    }

    /**
     * @deprecated
     * Functionality of this method is included in LeaveAck handle
     */
    @Override
    public void removeFromSendList(String nickName) {
        TIME_STAMP.getAndIncrement();
        Platform.runLater(() ->
                Main.getInstance().getEventNode().fireEvent(new UserEvent(UserEvent.LEAVE_EVENT, nickName)));
    }

    /**
     *
     * @param username Client Username
     * @param password Client Password
     */
    @Override
    public void announce(String username, String password) {
        this.username = username;
        AnnouncePacket p = new AnnouncePacket(username, password);
        TIME_STAMP.getAndIncrement();
        try {
            IO_QUEUE.offer(p.getDatagramPacket(currentMulticastAddress, Constants.PORTS.CLIENT, aes));
        } catch (CryptoException e) {
            System.exit(1);
        }
    }


}
