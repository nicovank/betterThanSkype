package utils;

public final class Constants {

    public static final int MAX_PACKET_SIZE = 1025;

    public static final int PACKET_HANDLER_THREADS = 1;

    public static final class ENCRYPTION {
        public static final byte UNENCRYPTED = 0;
        public static final byte RSA = 1;
        public static final byte AES = 2;

        private ENCRYPTION() {

        }
    }

    public static final class OPCODE {
        public static final byte PUBREQ = 2;
        public static final byte PUB = 17;
        public static final byte CREATEROOM = 31;
        public static final byte CRSUC = 71;
        public static final byte ERROR = 5;
        public static final byte JOINREQ = 11;
        public static final byte JOINSUC = 23;
        public static final byte ANNOUNCE = 103;
        public static final byte ANNACK = 107;

        private OPCODE() {

        }
    }

    public static final class ERROR_CODE {
        public static final byte INVALIDNAME = 51;
        public static final byte REWP = 51;
        public static final byte RERP = 53;
        public static final byte OTHER = 54;

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
