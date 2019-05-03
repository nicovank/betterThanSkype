package application;

import crypto.CryptoException;
import crypto.RSA;

public final class Encryption {
    private static RSA rsa;

    public static RSA getRSA() throws CryptoException {
        if (rsa == null) {
            rsa = new RSA();
        }

        return rsa;
    }

    private Encryption() {

    }
}
