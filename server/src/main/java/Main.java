import crypto.CryptoException;
import server.Server;

import java.net.SocketException;

public class Main {
    public static void main(String[] args) throws CryptoException, SocketException {
        Server server = new Server(13127);
        server.start();
    }
}
