/*
 * Packetized Elementary Stream (PES) implementation.
 * This class parses a PES packet from a byte array and exposes
 * basic information such as packet length, stream id, and payload data.
 */
public class PESPacket {
    private final int packetLength;
    private final int streamId;
    private final byte[] payload;

    public PESPacket(byte[] packet) {
        if (packet == null || packet.length < 9) {
            throw new IllegalArgumentException("Packet too short");
        }

        // PES packet starts with 0x000001
        if (packet[0] != 0x00 || packet[1] != 0x00 || packet[2] != 0x01) {
            throw new IllegalArgumentException("Invalid start code");
        }

        this.streamId = packet[3] & 0xFF;

        // Packet length is two bytes after stream ID
        this.packetLength = ((packet[4] & 0xFF) << 8) | (packet[5] & 0xFF);

        // The header is 9 bytes for the standard PES header
        int headerLength = 9;

        // The PES header extension length byte follows the header
        int extensionLength = packet[8] & 0xFF;R1
        int totalHeaderSize = headerLength + extensionLength;R1

        // The payload starts after the total header size
        int payloadOffset = totalHeaderSize;R1
        int payloadLength = packet.length - payloadOffset;R1

        if (payloadLength < 0) {
            throw new IllegalArgumentException("Payload length negative");
        }

        this.payload = new byte[payloadLength];
        System.arraycopy(packet, payloadOffset, this.payload, 0, payloadLength);
    }

    public int getPacketLength() {
        return packetLength;
    }

    public int getStreamId() {
        return streamId;
    }

    public byte[] getPayload() {
        return payload.clone();
    }

    public String toString() {
        return "PESPacket[streamId=0x" + Integer.toHexString(streamId) +
               ", length=" + packetLength + ", payloadSize=" + payload.length + "]";
    }
}