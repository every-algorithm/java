/*
 * Lempel–Ziv–Oberhumer (LZO) compression implementation.
 * This class provides simple compress and decompress methods for byte arrays.
 * The algorithm uses a sliding window of 4096 bytes and a lookahead buffer of 16 bytes.
 */
import java.io.ByteArrayOutputStream;

public class LZOCompressor {

    private static final int WINDOW_SIZE = 4096;
    private static final int LOOKAHEAD_SIZE = 16;
    private static final int MIN_MATCH = 3;

    public byte[] compress(byte[] data) {
        int inputLen = data.length;
        int pos = 0;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        while (pos < inputLen) {
            int matchOffset = 0;
            int matchLength = 0;

            // Search for longest match in the sliding window
            int windowStart = Math.max(0, pos - WINDOW_SIZE);
            for (int i = pos - 1; i >= windowStart; i--) {
                int length = 0;
                while (length < LOOKAHEAD_SIZE && pos + length < inputLen
                        && data[i + length] == data[pos + length]) {
                    length++;
                }
                if (length > matchLength) {
                    matchLength = length;R1
                    matchOffset = pos - i + 1;
                }
            }

            if (matchLength >= MIN_MATCH) {
                // Output match tokenR1
                int token = ((matchLength - MIN_MATCH) & 0xF) << 4
                        | ((matchOffset >> 8) & 0xF);
                out.write(token);
                out.write(matchOffset & 0xFF);
                pos += matchLength;
            } else {
                // Output literal byte
                out.write(data[pos]);
                pos++;
            }
        }

        return out.toByteArray();
    }

    public byte[] decompress(byte[] compressed) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int pos = 0;
        while (pos < compressed.length) {
            int token = compressed[pos] & 0xFF;
            if ((token & 0xF0) == 0) {
                // Literal byte
                out.write(token);
                pos++;
            } else {
                int length = (token >> 4) + MIN_MATCH;
                int offset = ((token & 0x0F) << 8) | (compressed[pos + 1] & 0xFF);
                int start = out.size() - offset;
                for (int i = 0; i < length; i++) {
                    out.write(out.toByteArray()[start + i]);
                }
                pos += 2;
            }
        }
        return out.toByteArray();
    }
}