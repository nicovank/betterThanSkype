package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

public final class Address {
    private InetAddress address;
    private int port;

    public Address(InetAddress address, int port) {
        this.address = address;
        this.port = port;
    }

    public static Address randomMulticastGroup() throws UnknownHostException {
        return new Address(InetAddress.getByName("224.1.1.13"), 7896);
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, port);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof Address)) return false;
        return ((Address) other).address.equals(address) && ((Address) other).port == port;
    }

    public boolean equals(InetAddress address, int port) {
        return address.equals(this.address) && port == this.port;
    }
}
