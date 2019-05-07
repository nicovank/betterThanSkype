package server;

import crypto.CryptoException;
import crypto.RSA;
import packets.Packet;
import sockets.BTSSocket;
import utils.Address;
import utils.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends Thread {

    private static final int HANDLER_THREADS = 2;
    private static final int TIMEOUT = 50;

    private final Queue<Pair<Packet, Address>> inbound;
    private final Queue<DatagramPacket> outbound;
    private final RSA rsa;

    private final BTSSocket socket;

    public Server(int port) throws CryptoException, SocketException {
        inbound = new ConcurrentLinkedQueue<>();
        outbound = new ConcurrentLinkedQueue<>();
        rsa = new RSA();

        socket = new BTSSocket(port);
    }

    @Override
    public void run() {

        System.out.println("Server started on port " + socket.getPort() + ".");

        // spawn handler threads
        ExecutorService service = Executors.newFixedThreadPool(HANDLER_THREADS);
        for (int i = 0; i < HANDLER_THREADS; ++i) {
            PacketHandler handler = new PacketHandler(inbound, outbound, rsa);
            service.execute(handler);
        }

        // keep sending any packet in the outbound queue, and receive + parse incoming packets and put them in the
        // inbound queue.
        main: for (;;) {
            DatagramPacket packet = outbound.poll();

            while (packet != null) {
                try {
                    socket.send(packet);
                } catch (IOException e) {
                    System.err.println("[ERROR] A major error occurred. More details below.");
                    e.printStackTrace(System.err);

                    service.shutdown();
                    break main;
                }

                packet = outbound.poll();
            }

            try {

                Pair<Packet, Address> received = socket.receive(TIMEOUT);
                if (received != null) {
                    inbound.offer(received);
                }

            } catch (IOException e) {
                System.err.println("[ERROR] A major error occurred. More details below.");
                e.printStackTrace(System.err);

                service.shutdown();
                break main;
            }
        }
    }
}
