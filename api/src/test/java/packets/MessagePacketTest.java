package packets;

import org.junit.Test;

import static org.junit.Assert.*;

public class MessagePacketTest {

    @Test(expected = InvalidPacketFormatException.class)
    public void parseNoMessage() throws InvalidPacketFormatException {
        MessagePacket packet = new MessagePacket("nick", "", 0);
        byte[] correct = {4, 'n', 'i', 'c', 'k', 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

        MessagePacket.parse(correct);


    }

    @Test
    public void parse() throws InvalidPacketFormatException {
        MessagePacket packet = new MessagePacket("nick", "nick", 0);
        byte[] correct = {4, 'n', 'i', 'c', 'k', 0, 0, 0, 4, 'n', 'i', 'c', 'k', 0, 0, 0, 0, 0, 0, 0, 0};

        assertEquals(packet, MessagePacket.parse(correct));
    }

    @Test
    public void serialize() {
        MessagePacket packet = new MessagePacket("nick", "thick", 0);
        byte[] correct = {4, 'n', 'i', 'c', 'k', 0, 0, 0, 5, 't', 'h', 'i', 'c', 'k', 0, 0, 0, 0, 0, 0, 0, 0};

        assertArrayEquals(correct, packet.serialize());
    }

    @Test
    public void reflexive() throws InvalidPacketFormatException {
        MessagePacket packet = new MessagePacket("nick", "thick", 0);
        byte[] correct = {4, 'n', 'i', 'c', 'k', 0, 0, 0, 5, 't', 'h', 'i', 'c', 'k', 0, 0, 0, 0, 0, 0, 0, 0};
        MessagePacket packet1 = MessagePacket.parse(correct);

        assertEquals(packet1.getNickName(),packet.getNickName());
        assertEquals(packet.getMessage(),packet1.getMessage());
        assert packet1.getTimestamp() ==0;
        assertArrayEquals(correct,packet1.serialize());

    }
}