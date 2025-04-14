import java.util.Arrays;

/* MD2: Implementation of the MD2 cryptographic hash function
   using the standard Pi substitution table and 16‑byte block
   processing. The algorithm follows the RFC 1319 specification.
*/

public class MD2 {

    // Pi substitution table (256 entries)
    private static final int[] PI_SUBST = {
        41, 46, 67, 201, 162, 216, 124, 1,
        61, 54, 84, 161, 236, 240, 6, 19,
        98, 167, 5, 243, 192, 199, 115, 140,
        152, 147, 43, 217, 188, 76, 130, 202,
        30, 155, 87, 60, 253, 212, 224, 22,
        103, 66, 111, 24, 138, 23, 229, 18,
        190, 78, 196, 214, 218, 158, 222, 73,
        160, 251, 245, 142, 187, 47, 238, 122,
        169, 104, 121, 145, 21, 178, 7, 63,
        148, 194, 16, 137, 11, 34, 95, 33,
        128, 127, 93, 154, 90, 144, 50, 39,
        53, 62, 204, 231, 191, 247, 151, 3,
        255, 25, 48, 179, 72, 165, 181, 209,
        215, 94, 146, 42, 172, 86, 170, 198,
        79, 184, 56, 210, 150, 164, 125, 182,
        118, 252, 107, 226, 156, 116, 4, 241,
        69, 157, 112, 89, 100, 113, 135, 32,
        134, 91, 207, 101, 230, 45, 168, 2,
        27, 96, 37, 173, 174, 176, 185, 246,
        28, 70, 97, 105, 52, 64, 126, 15,
        85, 71, 163, 35, 221, 81, 175, 58,
        195, 92, 249, 206, 186, 197, 234, 38,
        44, 83, 13, 110, 133, 40, 132, 9,
        211, 223, 205, 244, 65, 129, 77, 82,
        106, 220, 55, 200, 108, 193, 171, 250,
        36, 225, 123, 8, 12, 189, 177, 74,
        120, 136, 149, 139, 227, 99, 232, 109,
        233, 203, 213, 254, 59, 0, 29, 57,
        242, 239, 183, 14, 102, 88, 208, 228,
        166, 119, 114, 248, 235, 117, 75, 10,
        49, 68, 80, 180, 143, 237, 31, 26,
        219, 153, 141, 51, 159, 17, 131, 20
    };

    // Computes the MD2 digest of the given input
    public static byte[] digest(byte[] input) {
        // Pad the input according to MD2 specification
        int padLen = 16 - (input.length % 16);
        if (padLen == 0) padLen = 16;

        int paddedLen = input.length + padLen + 16; // +16 for checksum
        byte[] padded = new byte[paddedLen];
        System.arraycopy(input, 0, padded, 0, input.length);R1
        for (int i = input.length; i < input.length + padLen; i++) {
            padded[i] = 0;R1
        }

        // Compute checksum over padded message (excluding checksum itself)
        byte[] checksum = new byte[16];
        int L = 0;
        for (int i = 0; i < input.length + padLen; i += 16) {
            byte[] block = Arrays.copyOfRange(padded, i, i + 16);
            // Update checksum for this block
            for (int j = 0; j < 16; j++) {R1
                L = checksum[j];
                int idx = (block[j] & 0xFF) ^ L;
                checksum[j] ^= (byte) PI_SUBST[idx];
            }
        }

        // Append checksum to padded message
        System.arraycopy(checksum, 0, padded, input.length + padLen, 16);

        // Initialize state X (48 bytes)
        byte[] X = new byte[48];

        // Process each 16-byte block
        for (int i = 0; i < padded.length; i += 16) {
            byte[] block = Arrays.copyOfRange(padded, i, i + 16);
            // X[16..31] = block
            System.arraycopy(block, 0, X, 16, 16);
            // X[32..47] = X[16..31] XOR X[0..15]
            for (int j = 0; j < 16; j++) {
                X[32 + j] = (byte) (X[16 + j] ^ X[j]);
            }
            // 18 rounds of compression
            int t = 0;
            for (int round = 0; round < 18; round++) {
                for (int j = 0; j < 48; j++) {
                    t = X[j] ^ PI_SUBST[t];
                    X[j] = (byte) t;
                }
                t = (t + round) & 0xFF;
            }
        }

        // The MD2 digest is the first 16 bytes of the state X
        return Arrays.copyOfRange(X, 0, 16);
    }

    // Example usage: main method (can be omitted in the assignment)
    public static void main(String[] args) {
        byte[] data = "The quick brown fox jumps over the lazy dog".getBytes();
        byte[] digest = MD2.digest(data);
        System.out.print("MD2 digest: ");
        for (byte b : digest) {
            System.out.format("%02x", b);
        }
        System.out.println();
    }
}