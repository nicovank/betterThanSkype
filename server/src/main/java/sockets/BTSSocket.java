package sockets;

import crypto.CryptoException;
import crypto.RSA;
import packets.InvalidPacketFormatException;
import packets.Packet;
import utils.Address;
import utils.Constants;
import utils.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

public final class BTSSocket {

    private final DatagramSocket socket;
    private final RSA rsa;

    public BTSSocket(int port, RSA rsa) throws SocketException {
        socket = new DatagramSocket(port);
        this.rsa = rsa;
    }

    /**
     * Receive a UDP packet, and try to parse it to a Packet. If there is any error with the received packet, it will be
     * dropped.
     *
     * @param timeout how long the socket should wait before returning null when no data is received.
     * @return a received Packet, or null if nothing was received.
     * @throws IOException thrown by socket.receive(DatagramPacket).
     */
    public Pair<Packet, Address> receive(int timeout) throws IOException {
        socket.setSoTimeout(timeout);
        DatagramPacket packet = new DatagramPacket(new byte[Constants.MAX_PACKET_SIZE], Constants.MAX_PACKET_SIZE);

        try {
            socket.receive(packet);
            Packet received = Packet.parse(Arrays.copyOf(packet.getData(), packet.getLength()), rsa);

            System.out.println("Received packet with OPCODE " + received.getOperationCode() + " from " + packet.getAddress() + " at port " + packet.getPort() + ".");

            return new Pair<>(
                    received,
                    new Address(packet.getAddress(), packet.getPort())
            );
        } catch (SocketTimeoutException e) {
            return null;
        } catch (InvalidPacketFormatException e) {
            System.out.println("[DEBUG] Received invalid packet. More details below.");
            e.printStackTrace(System.out);
            return null;
        } catch (CryptoException e) {
            System.out.println("[DEBUG] There was a problem with the decryption of a received packet. More details below.");
            e.printStackTrace(System.out);
            return null;
        }
    }

    public void send(DatagramPacket packet) throws IOException {
        System.out.println("Sending packet to " + packet.getAddress() + " at port " + packet.getPort() + ".");
        socket.send(packet);
    }

    public int getPort() {
        return socket.getLocalPort();
    }
}
