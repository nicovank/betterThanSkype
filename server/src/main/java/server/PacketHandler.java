package server;

import crypto.CryptoException;
import crypto.PublicKeyDictionary;
import crypto.RSA;
import packets.Packet;
import packets.PublicKeyPacket;
import packets.RoomCreationRequestPacket;
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
            if (!dictionary.containsKey(address)) {
                // TODO SEND ERROR PACKET
            } else switch (packet.getOperationCode()) {

                case Constants.OPCODE.CREATEROOM:
                    RoomCreationRequestPacket request = (RoomCreationRequestPacket) packet;
                    if (rooms.containsKey(request.getRoomName())) {
                        // TODO SEND ROOM ALREADY EXISTS ERROR PACKET
                    } else {
                        Room room = new Room(); // TODO put values in the room class (notably generate random Multicast URL)
                        rooms.put(request.getRoomName(), room);
                        // TODO SEND SUCCESSFUL ROOM CREATION PACKET
                    }
                    break;

                case Constants.OPCODE.JOINREQ:
                    // TODO WHEN A JOIN ROOM PACKET EXISTS
                    break;
            }
        }
    }
}
