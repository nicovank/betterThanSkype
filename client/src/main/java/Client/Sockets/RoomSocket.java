package Client.Sockets;

import Client.Events.*;
import Client.Main;
import crypto.CryptoException;
import crypto.RSA;
import packets.*;
import utils.Address;
import utils.Constants;
import java.io.IOException;
import java.net.*;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

public class RoomSocket implements IRoomSocket,Runnable{
    private final InetAddress SERVER_ADDRESS;
    private final DatagramSocket SERVER_SOCKET;
    private final MulticastSocket CLIENT_SOCKET;
    private final RSA KEYS;
    private final PublicKey OUR_KEY;
    private final PublicKey SERVER_PUBLIC_KEY;
    private final ConcurrentLinkedQueue<DatagramPacket> IO_QUEUE;
    private final AtomicLong TIME_STAMP;
    private InetAddress currentMulticastAddress;

    public RoomSocket() throws IOException, CryptoException {
        //setup SERVER_SOCKET
        SERVER_SOCKET = new DatagramSocket(Constants.PORTS.SERVER);
        CLIENT_SOCKET = new MulticastSocket(Constants.PORTS.CLIENT);
        SERVER_SOCKET.setSoTimeout(10000);
        SERVER_ADDRESS = InetAddress.getByName("pi.cs.oswego.edu");

        //generate our RSA security key.
        KEYS = new RSA();
        OUR_KEY = KEYS.getPublicKey();

        //inform our Server of our existence.
        PublicKeyPacket prPacket = new PublicKeyPacket(OUR_KEY);
        byte[] bytes = new byte[Constants.MAX_PACKET_SIZE];
        DatagramPacket sendPacket = prPacket.getDatagramPacket(SERVER_ADDRESS,Constants.PORTS.SERVER);
        SERVER_SOCKET.send(sendPacket);

        //Get ack and key from server
        boolean successfulServerAck = false;
        DatagramPacket receivePacket= new DatagramPacket(bytes,bytes.length);
        Packet p = null;

        while(!successfulServerAck) {
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
        TIME_STAMP = new AtomicLong(0);
    }

    public void run() {
        try{
            SERVER_SOCKET.setSoTimeout(100);
            CLIENT_SOCKET.setSoTimeout(100);
        } catch (SocketException e) {
            System.out.println("Thread Socket Error has occurred");
            return;
        }

        while(!Thread.interrupted()){
            //always send before receiving
            while(!IO_QUEUE.isEmpty()){
                DatagramPacket packet = IO_QUEUE.poll();
                try {
                    if (packet.getPort() == Constants.PORTS.SERVER) {
                        SERVER_SOCKET.send(packet);
                    } else if (packet.getPort() == Constants.PORTS.CLIENT) {
                        CLIENT_SOCKET.send(packet);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            //handle Server packet if there is one.
            //Server requests are the priority
            try{
                byte[] bytes = new byte[Constants.MAX_PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(bytes,bytes.length);
                SERVER_SOCKET.receive(packet);
                Packet p = Packet.parse(packet.getData(),KEYS); //TODO is encryption correct?
                handleServerPacket(p);
            } catch (IOException | InvalidPacketFormatException | CryptoException e) {

            }

            //handle Client packet if there is one.
            try{
                byte[] bytes = new byte[Constants.MAX_PACKET_SIZE];
                DatagramPacket packet = new DatagramPacket(bytes,bytes.length);
                CLIENT_SOCKET.receive(packet);         //TODO encryption?
                Packet p = Packet.parse(packet.getData());
                handleClientPacket(p);
            } catch (IOException | InvalidPacketFormatException | CryptoException e) {

            }
        }
    }

    private void handleServerPacket(Packet packet){
        //handle all packets a client should be expected to recieve from the server.
        switch (packet.getOperationCode()) {
            case Constants.OPCODE.CRSUC:
                SuccessfulRoomCreationPacket s = (SuccessfulRoomCreationPacket) packet;
                //handle room stuff
                Main.getInstance().getEventNode().fireEvent(new RoomResponseEvent(RoomResponseEvent.CREATE_ROOM,true,s.getName()));
                break;
            case Constants.OPCODE.JOINSUC:
                JoinRoomSuccessPacket j = (JoinRoomSuccessPacket) packet;
                Main.getInstance().getEventNode().fireEvent(new RoomResponseEvent(RoomResponseEvent.JOIN_ROOM,true,j.getName()));
                break;
        }
    }
    private void handleClientPacket(Packet packet){
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
                handleMessageAck((MessageAckPacket)packet);
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

    private void handleAnnouncement(AnnouncePacket packet){
        TIME_STAMP.getAndIncrement();
        AnnounceAckPacket ackPacket = packet.createAck(TIME_STAMP.get());
        IO_QUEUE.offer(ackPacket.getDatagramPacket(SERVER_ADDRESS,Constants.PORTS.SERVER));
        String username = ackPacket.getNickName();

        //TODO create event for Controllers to listen to
    }

    private void handleAnnouncementAck(AnnounceAckPacket packet){
        TIME_STAMP.getAndIncrement();
        AnnounceAckAckPacket ackPacket = new AnnounceAckAckPacket();
        IO_QUEUE.offer(ackPacket.getDatagramPacket(SERVER_ADDRESS,Constants.PORTS.SERVER));
        //TODO handle not receiving packet for a long time needing a resend.
    }

    private void handleAnnouncementAckAck(AnnounceAckAckPacket packet) {
        TIME_STAMP.getAndIncrement();

        //TODO nothing? terminate waiting condition for handleAnnouncementAck
    }

    private void handleMessage(MessagePacket packet){

        MessageAckPacket ackPacket = packet.createAck();
        IO_QUEUE.offer(ackPacket.getDatagramPacket(SERVER_ADDRESS,Constants.PORTS.SERVER));

        //resolve timeStamp
        long timestamp = Math.max(TIME_STAMP.getAndIncrement(),packet.getTimestamp());

        //create message
        Message m = new Message(packet.getMessage(),packet.getNickName(),timestamp);
        Main.getInstance().getEventNode().fireEvent(new MessageReceivedEvent(MessageReceivedEvent.MESSAGE_EVENT,m));

        //TODO timer if ack isn't received fast enough.
    }

    private void handleMessageAck(MessageAckPacket packet){
        TIME_STAMP.getAndIncrement();

        //TODO nothing? break timer for ack packet.
    }

    private void handleLeaveRoom(LeaveRoomPacket packet){
        TIME_STAMP.getAndIncrement();

        //TODO create event for Controller to listen to
    }

    private void handleKeepAlive(KeepAlivePacket packet){
        TIME_STAMP.getAndIncrement();
        KeepAliveAckPacket ackPacket = new KeepAliveAckPacket();
        IO_QUEUE.offer(ackPacket.getDatagramPacket(SERVER_ADDRESS,Constants.PORTS.SERVER));

        //TODO handle logistics for this
    }
    private void handleKeepAliveAck(KeepAliveAckPacket packet){
        TIME_STAMP.getAndIncrement();

        //TODO nothing?
    }

    //IMPORTANT ALL OF THESE MESSAGES SHOULD END WITH SENDING A PACKET TO IOQUEUE AND INCREMENTING TIME_STAMP
    @Override
    public void attemptToCreateRoom(String room, String username, String password) {
        //TODO Needs roomname.  Type?
        RoomCreationRequestPacket creationRequestPacket = new RoomCreationRequestPacket(room,username,password,Constants.TYPE.UNICAST);
        DatagramPacket packet = creationRequestPacket.getDatagramPacket(SERVER_ADDRESS,Constants.PORTS.SERVER);
        IO_QUEUE.offer(packet);
        TIME_STAMP.getAndIncrement();
    }

    @Override
    public void attemptToJoinRoom(String room, String username, String password) {
        JoinRoomPacket joinRequestPacket = new JoinRoomPacket(room,username,password,Constants.TYPE.UNICAST);
        DatagramPacket packet = joinRequestPacket.getDatagramPacket(SERVER_ADDRESS,Constants.PORTS.SERVER);
        IO_QUEUE.offer(packet);
        TIME_STAMP.getAndIncrement();
        //TODO if timeout for ack, send failure.
    }

    @Override
    public long sendToEveryone(String message, String password) {
        MessagePacket messagePacket = new MessagePacket(message,password,Constants.TYPE.UNICAST);
        DatagramPacket packet = messagePacket.getDatagramPacket(currentMulticastAddress,Constants.PORTS.SERVER);
        packet.setPort(Constants.PORTS.CLIENT);
        //packet.setAddress("0.0.0.0"); //TODO get MULTICAST IP if needed.
        IO_QUEUE.offer(packet);
        return TIME_STAMP.getAndIncrement();
    }

    @Override
    public void sendLeavingMessage(String username, String roomname) {
        LeaveRoomPacket packet = new LeaveRoomPacket(username, roomname);
        DatagramPacket clientPacket = packet.getDatagramPacket(currentMulticastAddress,Constants.PORTS.SERVER);
        LeaveRoomPacket packet2 = new LeaveRoomPacket(username, roomname);
        DatagramPacket serverPacket = packet2.getDatagramPacket(SERVER_ADDRESS,Constants.PORTS.SERVER);
        IO_QUEUE.offer(clientPacket);
        IO_QUEUE.offer(serverPacket);
    }

    @Override
    public void addToSendList(String nickName, Address address) {
        //TODO implement still needs to persist username/addresses
        TIME_STAMP.getAndIncrement();
        Main.getInstance().getEventNode().fireEvent(new UserJoinedEvent(UserJoinedEvent.JOIN_EVENT,nickName));
    }

    @Override
    public void removeFromSendList(String nickName) {
        //TODO implement still needs to persist username/addresses
        TIME_STAMP.getAndIncrement();
        Main.getInstance().getEventNode().fireEvent(new UserLeftEvent(UserLeftEvent.LEAVE_EVENT,nickName));
    }
}
