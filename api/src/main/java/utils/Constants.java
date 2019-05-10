package utils;

public final class Constants {

    public static final int MAX_PACKET_SIZE = 1025;

    public static final int PACKET_HANDLER_THREADS = 1;

    public static final class PORTS{
        private PORTS(){}
        public static final int CLIENT = 2697;
        public static final int SERVER = 3697;
    }
    public static final class ENCRYPTION {
        public static final byte UNENCRYPTED = 0;
        public static final byte RSA = 1;
        public static final byte AES = 2;

        private ENCRYPTION() {

        }
    }

    public static final class OPCODE {
        public static final byte PUB = 1;
        public static final byte CREATEROOM = 2;
        public static final byte JOINREQ = 3;
        public static final byte MRCS = 4; // Multicast Room Creation Success
        public static final byte JOINSUC = 5;
        public static final byte LEAVEROOM = 6;
        public static final byte ERROR = 7;
        public static final byte ANNOUNCE = 8;
        public static final byte ANNACK = 9;
        public static final byte ANNACKACK = 10;
        public static final byte MESSAGE = 12;
        public static final byte MESSAGEACK = 13;
        public static final byte KEEPALIVE = 14;
        public static final byte KEEPALIVEACK = 15;





        private OPCODE() {

        }
    }

    public static final class ERROR_CODE {
        public static final byte CREATEERROR = 71;
        public static final byte JOINERROR = 72;
        // when the server does not know the client's public key, hence cannot send an encrypted response
        public static final byte UNKNOWNPUBLICKEY = 73;
        public static final byte OTHER = 74;

        private ERROR_CODE() {

        }
    }

    public static final class TYPE {
        public static final byte UNICAST = 73;
        public static final byte MULTICAST = 79;

        private TYPE() {

        }
    }

    private Constants() {

    }
}
