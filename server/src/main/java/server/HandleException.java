package server;

import crypto.CryptoException;
import packets.ErrorPacket;
import packets.Packet;
import utils.Address;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.security.PublicKey;

public class HandleException extends Exception {

    private byte code;
    private String message;
    private Address address;
    private PublicKey key;

    public HandleException(Address address, PublicKey key, byte code) {
        this(address, key, code, null);
    }

    public HandleException(Address address, byte code) {
        this(address, null, code, null);
    }

    public HandleException(InetAddress address, int port, PublicKey key, byte code) {
        this(new Address(address, port), key, code, null);
    }

    public HandleException(InetAddress address, int port, byte code) {
        this(new Address(address, port), null, code, null);
    }

    public HandleException(InetAddress address, int port, PublicKey key, byte code, String format, Object... args) {
        this(new Address(address, port), key, code, format, args);
    }

    public HandleException(InetAddress address, int port, byte code, String format, Object... args) {
        this(new Address(address, port), null, code, format, args);
    }

    public HandleException(Address address, byte code, String format, Object... args) {
        this(address, null, code, format, args);
    }

    public HandleException(Address address, PublicKey key, byte code, String format, Object... args) {
        this.code = code;
        this.message = ((format == null) ? null : String.format(format, args));
        this.address = address;
        this.key = key;
    }

    public DatagramPacket getErrorPacket() throws CryptoException {
        Packet packet = new ErrorPacket(code, message);

        if (key != null) {
            return packet.getDatagramPacket(address, key);
        }

        return packet.getDatagramPacket(address);
    }
}
