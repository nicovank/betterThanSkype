package server;

import crypto.CryptoException;
import crypto.PublicKeyDictionary;
import crypto.RSA;
import packets.*;
import rooms.MulticastRoom;
import rooms.Peer;
import rooms.Room;
import utils.Address;
import utils.Constants;
import utils.Pair;

import java.net.DatagramPacket;
import java.security.PublicKey;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public final class PacketHandler extends Thread {
    private final Queue<Pair<Packet, Address>> inbound;
    private final Queue<DatagramPacket> outbound;
    private final RSA rsa;

    private static final PublicKeyDictionary<Address> dictionary = new PublicKeyDictionary<>();
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public PacketHandler(Queue<Pair<Packet, Address>> inbound, Queue<DatagramPacket> outbound, RSA rsa) {
        this.inbound = inbound;
        this.outbound = outbound;
        this.rsa = rsa;
    }

    @Override
    public void run() {
        for (; ; ) {
            try {
                try {
                    handle();
                } catch (HandleException e) {
                    outbound.offer(e.getErrorPacket());
                }
            } catch (CryptoException ignored) {

            }
        }
    }

    /**
     * This methods polls a single packet from the inbound queue, if there is any.
     * The server then decides to act on the client's request, and adds a response packet on the outbound queue if
     * needed.
     */
    public void handle() throws HandleException, CryptoException {
        Pair<Packet, Address> pair = inbound.poll();
        if (pair == null) return;

        Packet packet = pair.getA();
        Address address = pair.getB();

        if (packet.getOperationCode() == Constants.OPCODE.PUB) {
            try {
                PublicKey pub = ((PublicKeyPacket) packet).getPublicKey();
                dictionary.put(address, pub);
                Packet response = new PublicKeyPacket(rsa.getPublicKey());
                DatagramPacket payload = response.getDatagramPacket(address, pub);
                outbound.offer(payload);
            } catch (CryptoException ignored) {

            }
        } else {
            PublicKey pub = dictionary.get(address);
            if (pub == null) {
                // We do not know the client's public key, and therefore cannot process their request.
                throw new HandleException(address, Constants.ERROR_CODE.UNKNOWNPUBLICKEY);
            }

            switch (packet.getOperationCode()) {

                case Constants.OPCODE.CREATEROOM:
                    RoomCreationRequestPacket rcr = (RoomCreationRequestPacket) packet;
                    if (rcr.getType() == Constants.TYPE.MULTICAST) {
                        Peer creator = new Peer(rcr.getUserName(), address);
                        MulticastRoom room = new MulticastRoom(rcr.getRoomName(), rcr.getPassword(), Address.randomMulticastGroup());
                        room.addPeer(creator);

                        if (room.equals(rooms.putIfAbsent(room.getName(), room))) {
                            // the creation of the room was successful
                            Packet response = new SuccessfulMulticastRoomCreationPacket(
                                    room.getName(),
                                    room.getSecret(),
                                    room.getIP(),
                                    room.getPort()
                            );

                            outbound.offer(response.getDatagramPacket(address, pub));
                        } else {
                            throw new HandleException(address, pub, Constants.ERROR_CODE.CREATEERROR, "A room named '%s' already exists.", rcr.getRoomName());
                        }
                    } else {
                        // TODO SEND ERROR PACKET (UNICAST NOT SUPPORTED YET)
                    }
                    break;

                case Constants.OPCODE.JOINREQ:
                    JoinRoomRequestPacket jrr = (JoinRoomRequestPacket) packet;
                    Room room = rooms.get(jrr.getRoomName());

                    if (room == null) {
                        throw new HandleException(address, pub, Constants.ERROR_CODE.JOINERROR, "There is no room named '%s'.", jrr.getRoomName());
                    }

                    if (!room.getPassword().equals(jrr.getPassword())) {
                        throw new HandleException(address, pub, Constants.ERROR_CODE.JOINERROR, "Incorrect password.");
                    }

                    if (!room.addPeer(new Peer(jrr.getUserName(), address))) {
                        throw new HandleException(address, pub, Constants.ERROR_CODE.JOINERROR, "Ths nickname is already used by someone.");
                    }

                    if (room.getType() == Constants.TYPE.MULTICAST) {
                        MulticastRoom mroom = (MulticastRoom) room;
                        outbound.offer(new JoinRoomSuccessPacket(
                                mroom.getName(),
                                mroom.getSecret(),
                                mroom.getIP(),
                                mroom.getPort(),
                                mroom.getType()
                        ).getDatagramPacket(address, pub));
                    } else {
                        // TODO SEND UNICAST NOT SUPPORTED ERROR.
                    }

                    break;

                case Constants.OPCODE.LEAVEROOM:
                    // TODO HANDLE
                    break;
            }
        }
    }
}
