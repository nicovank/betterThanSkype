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
        for (;;) {
            handle();
        }
    }

    /**
     * This methods polls a single packet from the inbound queue, if there is any.
     * The server then decides to act on the client's request, and adds a response packet on the outbound queue if
     * needed.
     */
    public void handle() {
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
                Packet response = new ErrorPacket(Constants.ERROR_CODE.UNKNOWNPUBLICKEY);
                outbound.offer(response.getDatagramPacket(address));
            } else switch (packet.getOperationCode()) {

                case Constants.OPCODE.CREATEROOM:
                    RoomCreationRequestPacket request = (RoomCreationRequestPacket) packet;
                    if (request.getType() == Constants.TYPE.MULTICAST) {
                        Peer creator = new Peer(request.getUserName(), address);
                        MulticastRoom room = new MulticastRoom(request.getRoomName(), request.getPassword(), Address.randomMulticastGroup());
                        room.addPeer(creator);

                        if (room.equals(rooms.putIfAbsent(room.getName(), room))) {
                            // the creation of the room was successful
                            Packet response = new SuccessfulMulticastRoomCreationPacket(
                                    room.getName(),
                                    room.getSecret(),
                                    room.getIP(),
                                    room.getPort()
                            );

                            try {
                                outbound.offer(response.getDatagramPacket(address, pub));
                            } catch (CryptoException ignored) {

                            }
                        }
                    } else {
                        // TODO SEND ERROR PACKET (UNICAST NOT SUPPORTED YET)
                    }
                    break;

                case Constants.OPCODE.JOINREQ:
                    // TODO WHEN A JOIN ROOM PACKET EXISTS
                    break;

                case Constants.OPCODE.LEAVEROOM:
                    // TODO HANDLE
                    break;
            }
        }
    }
}
