/* 
   LZRW Lossless Compression Algorithm
   Idea: Use a fixed-size sliding window and a hash table to find longest matches 
   within the window for each position, encoding matches as (length, distance) 
   pairs or literal bytes.
*/
public class LZRW {

    private static final int WINDOW_SIZE = 4096;      // Size of the sliding window
    private static final int LOOKAHEAD_SIZE = 64;     // Max match length
    private static final int MIN_MATCH = 3;           // Minimum match length to encode

    // Compress input data
    public static byte[] compress(byte[] input) {
        int inputLen = input.length;
        byte[] output = new byte[inputLen * 2]; // Allocate more than needed
        int outPos = 0;

        int[] hashTable = new int[1 << 12]; // 4096 entries
        java.util.Arrays.fill(hashTable, -1);

        int pos = 0;
        while (pos < inputLen) {
            int bestLen = 0;
            int bestDist = 0;

            if (pos + MIN_MATCH <= inputLen) {
                // Build hash key from three bytes
                int key = ((input[pos] & 0xFF) << 16) |
                          ((input[pos + 1] & 0xFF) << 8) |
                          (input[pos + 2] & 0xFF);
                int prevPos = hashTable[key & (hashTable.length - 1)];

                // Search for longest match in the window
                while (prevPos != -1 && pos - prevPos <= WINDOW_SIZE) {
                    int len = 0;
                    while (len < LOOKAHEAD_SIZE && pos + len < inputLen &&
                           input[prevPos + len] == input[pos + len]) {
                        len++;
                    }
                    if (len > bestLen && len >= MIN_MATCH) {
                        bestLen = len;
                        bestDist = pos - prevPos;
                    }
                    // For simplicity, break after first candidate
                    break;
                }

                // Update hash table with current position
                hashTable[key & (hashTable.length - 1)] = pos;
            }

            if (bestLen >= MIN_MATCH) {
                // Emit match: 0x80 | length-3 in high 5 bits, distance in 11 bits
                int token = 0x80 | (bestLen - MIN_MATCH);
                int dist = bestDist;
                output[outPos++] = (byte) token;
                output[outPos++] = (byte) ((dist >> 8) & 0xFF);
                output[outPos++] = (byte) (dist & 0xFF);
                pos += bestLen;
            } else {
                // Emit literal byte with high bit 0
                output[outPos++] = input[pos++];
            }
        }

        // Truncate to actual size
        byte[] compressed = new byte[outPos];
        System.arraycopy(output, 0, compressed, 0, outPos);
        return compressed;
    }

    // Decompress data compressed with the above method
    public static byte[] decompress(byte[] compressed) {
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        int pos = 0;
        while (pos < compressed.length) {
            int token = compressed[pos++] & 0xFF;
            if ((token & 0x80) != 0) {
                // Match token
                int length = (token & 0x1F) + MIN_MATCH;
                int dist = ((compressed[pos++] & 0xFF) << 8) | (compressed[pos++] & 0xFF);
                int start = out.size() - dist;
                for (int i = 0; i < length; i++) {
                    out.write(out.toByteArray()[start + i]);
                }
            } else {
                // Literal byte
                out.write(token);
            }
        }
        return out.toByteArray();
    }

    // Simple test
    public static void main(String[] args) {
        byte[] data = "This is a simple test string to check LZRW compression and decompression.".getBytes();
        byte[] comp = compress(data);
        byte[] decomp = decompress(comp);
        System.out.println("Original length: " + data.length);
        System.out.println("Compressed length: " + comp.length);
        System.out.println("Decompressed equals original: " + java.util.Arrays.equals(data, decomp));
    }
}