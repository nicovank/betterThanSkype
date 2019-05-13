import crypto.CryptoException;
import server.Server;
import utils.Constants;

import java.net.SocketException;

public class Main {
    public static void main(String[] args) throws CryptoException, SocketException {
        Server server = new Server(Constants.PORTS.SERVER);
        server.start();
    }
}
