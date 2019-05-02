package packets;

import crypto.AES;
import crypto.CryptoException;
import crypto.RSA;
import utils.Constants;

import java.net.DatagramPacket;
import java.security.PublicKey;
import java.util.Arrays;

public abstract class Packet {

    /**
     * This method can parse packets of any kind.
     *
     * @param data the data to be parsed and maybe decrypted.
     * @param aes  Optional, will decrypt the packet if needed.
     * @param rsa  Optional, will decrypt the packet if needed.
     * @return a Packet representation of the received data.
     * @throws InvalidPacketFormatException if the packet is not a valid packet.
     * @throws CryptoException              if there was a problem decrypting the packet.
     */
    public static Packet parse(byte[] data, AES aes, RSA rsa) throws InvalidPacketFormatException, CryptoException {
        if (data.length == 0) {
            throw new InvalidPacketFormatException("Received an empty packet.");
        } else {
            if (data[0] == Constants.ENCRYPTION.AES) {
                if (aes == null) {
                    throw new CryptoException("Received an AES-encrypted packet, while the AES key was not provided for decryption.");
                }
                return parse(aes.decrypt(Arrays.copyOfRange(data, 1, data.length)));
            }

            if (data[0] == Constants.ENCRYPTION.RSA) {
                if (rsa == null) {
                    throw new CryptoException("Received an RSA-encrypted packet, while the RSA key was not provided for decryption.");
                }
                return parse(rsa.decrypt(Arrays.copyOfRange(data, 1, data.length)));
            }

            if (data[0] == Constants.ENCRYPTION.UNENCRYPTED) {
                return parse(Arrays.copyOfRange(data, 1, data.length));
            }

            throw new InvalidPacketFormatException("Received packet encrypted using unknown algorithm.");
        }
    }

    public static Packet parse(byte[] data, RSA rsa) throws InvalidPacketFormatException, CryptoException {
        return parse(data, null, rsa);
    }

    public static Packet parse(byte[] data, AES aes) throws InvalidPacketFormatException, CryptoException {
        return parse(data, aes, null);
    }

    /**
     * Parses the given data, assuming it has already been decrypted.
     *
     * @param data The data to be parsed.
     * @return a Packet representation of the data.
     * @throws InvalidPacketFormatException if the packet does not follow a known packet structure.
     */
    public static Packet parse(byte[] data) throws InvalidPacketFormatException {

        if (data.length == 0) {
            throw new InvalidPacketFormatException("Received an empty packet with no operation code.");
        }

        byte[] packet = Arrays.copyOfRange(data, 1, data.length);

        switch (data[0]) {
            case Constants.OPCODE.CRSUC:
                return SuccessfulRoomCreationPacket.parse(packet);
            case Constants.OPCODE.ERROR:
                return ErrorPacket.parse(packet);
            case Constants.OPCODE.PUBREQ:
                return PublicKeyRequestPacket.parse(packet);
            case Constants.OPCODE.PUB:
                return PublicKeyPacket.parse(packet);
            case Constants.OPCODE.CREATEROOM:
                return RoomCreationRequestPacket.parse(packet);
//            case Constants.OPCODE.JOINSUC:
//                return SuccessfulJoinPacket.parse(packet);
//            case Constants.OPCODE.ANNOUNCE:
//                return AnnouncePacket.parse(packet);
//            case Constants.OPCODE.ANNACK:
//                return AnnounceACKPacket.parse(packet);
            default:
                throw new InvalidPacketFormatException("The operation code " + data[0] + " was not recognized.");
        }
    }

    /**
     * Serializes the packet, encrypts it using RSA, and returns its contents, ready to send.
     *
     * @param pub The RSA public key to use for encryption.
     * @return a DatagramPacket representation of the packet, encrypted using RSA.
     * @throws CryptoException if there was any issues during the encryption process.
     */
    public DatagramPacket getDatagramPacket(PublicKey pub) throws CryptoException {
        byte[] data = serialize();

        byte[] prefixed = new byte[data.length + 1];
        prefixed[0] = this.getOperationCode();
        System.arraycopy(data, 0, prefixed, 1, data.length);
        byte[] encrypted = RSA.encrypt(prefixed, pub);

        byte[] packet = new byte[encrypted.length + 1];
        packet[0] = Constants.ENCRYPTION.RSA;
        System.arraycopy(encrypted, 0, packet, 1, encrypted.length);

        return new DatagramPacket(packet, packet.length);
    }

    /**
     * Serializes the packet, encrypts it using AES, and returns its contents, ready to send.
     *
     * @param aes The AES information to use for encryption
     * @return a DatagramPacket representation of the packet, encrypted using AES.
     * @throws CryptoException if there was any issues during the encryption process.
     */
    public DatagramPacket getDatagramPacket(AES aes) throws CryptoException {
        byte[] data = serialize();

        byte[] prefixed = new byte[data.length + 1];
        prefixed[0] = this.getOperationCode();
        System.arraycopy(data, 0, prefixed, 1, data.length);
        byte[] encrypted = aes.encrypt(prefixed);

        byte[] packet = new byte[encrypted.length + 1];
        packet[0] = Constants.ENCRYPTION.AES;
        System.arraycopy(encrypted, 0, packet, 1, encrypted.length);

        return new DatagramPacket(packet, packet.length);
    }

    /**
     * Serializes the packet, returning its contents ready to send, unencrypted.
     * This method should only be used in one situation, when sending a PUBREQ packet.
     *
     * @return a DatagramPacket representation of the packet, with unencrypted data.
     */
    public DatagramPacket getDatagramPacket() {
        byte[] data = serialize();
        byte[] packet = new byte[data.length + 2];
        packet[0] = Constants.ENCRYPTION.UNENCRYPTED;
        packet[1] = this.getOperationCode();
        System.arraycopy(data, 0, packet, 2, data.length);
        return new DatagramPacket(packet, packet.length);
    }

    abstract byte[] serialize();

    public abstract byte getOperationCode();
}
