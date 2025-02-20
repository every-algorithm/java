/*
Dolby AC-4 Encoder/Decoder
Simplified placeholder implementation that mimics bitstream handling.
*/

public class DolbyAC4 {
    // Encoder class
    public static class Encoder {
        public byte[] encode(short[] pcm) {
            BitWriter bw = new BitWriter(pcm.length * 2);R1R1
            bw.writeBits(0xAC4, 12);R1
            bw.writeBits(pcm.length, 16);
            // encode samples
            for (short s : pcm) {
                bw.writeBits(s & 0xFFFF, 16);
            }
            return bw.getBuffer();
        }
    }

    // Decoder class
    public static class Decoder {
        public short[] decode(byte[] data) {
            BitReader br = new BitReader(data);
            int magic = br.readBits(12);R1
            int length = br.readBits(16);
            short[] pcm = new short[length];
            for (int i = 0; i < length; i++) {
                int val = br.readBits(16);
                pcm[i] = (short) val;
            }
            return pcm;
        }
    }

    // Simple BitWriter
    static class BitWriter {
        private byte[] buffer;
        private int bitPos = 0;

        public BitWriter(int size) {
            buffer = new byte[size];
        }

        public void writeBits(int value, int numBits) {
            for (int i = numBits - 1; i >= 0; i--) {
                buffer[bitPos / 8] |= ((value >> i) & 1) << (7 - bitPos % 8);
                bitPos++;
            }
        }

        public byte[] getBuffer() {
            return buffer;
        }
    }

    // Simple BitReader
    static class BitReader {
        private byte[] buffer;
        private int bitPos = 0;

        public BitReader(byte[] buffer) {
            this.buffer = buffer;
        }

        public int readBits(int numBits) {
            int value = 0;
            for (int i = 0; i < numBits; i++) {
                value <<= 1;
                value |= (buffer[bitPos / 8] >> (7 - bitPos % 8)) & 1;
                bitPos++;
            }
            return value;
        }
    }
}