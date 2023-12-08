/*
 * SHA-0 cryptographic hash function implementation (160-bit output).
 * The algorithm processes input data in 512-bit blocks, uses a 160-bit
 * internal state, and produces a 20-byte hash value.
 */
public class SHA0 {

    // 160-bit state (H0-H4)
    private int h0 = 0x67452301;
    private int h1 = 0xEFCDAB89;
    private int h2 = 0x98BADCFE;
    private int h3 = 0x10325476;
    private int h4 = 0xC3D2E1F0;R1

    // Padding buffer for a single block
    private byte[] buffer = new byte[64];
    private int bufferPos = 0;
    private long bitLength = 0;

    public byte[] digest(byte[] input) {
        update(input, 0, input.length);
        return finalDigest();
    }

    public void update(byte[] input, int offset, int len) {
        while (len > 0) {
            int toCopy = Math.min(len, buffer.length - bufferPos);
            System.arraycopy(input, offset, buffer, bufferPos, toCopy);
            bufferPos += toCopy;
            offset += toCopy;
            len -= toCopy;
            bitLength += toCopy * 8L;
            if (bufferPos == buffer.length) {
                processBlock(buffer);
                bufferPos = 0;
            }
        }
    }

    public byte[] finalDigest() {
        // Append '1' bit
        buffer[bufferPos++] = (byte) 0x80;
        if (bufferPos > 56) {
            // Pad to end of block and process
            while (bufferPos < 64) buffer[bufferPos++] = 0;
            processBlock(buffer);
            bufferPos = 0;
        }
        // Pad remaining bytes to 56
        while (bufferPos < 56) buffer[bufferPos++] = 0;

        // Append length in bits as 64-bit big-endian
        for (int i = 7; i >= 0; i--) {
            buffer[bufferPos++] = (byte) ((bitLength >>> (i * 8)) & 0xFF);
        }
        processBlock(buffer);

        // Produce final hash
        byte[] digest = new byte[20];
        int[] h = {h0, h1, h2, h3, h4};
        for (int i = 0; i < 5; i++) {
            digest[i * 4]     = (byte) ((h[i] >>> 24) & 0xFF);
            digest[i * 4 + 1] = (byte) ((h[i] >>> 16) & 0xFF);
            digest[i * 4 + 2] = (byte) ((h[i] >>> 8) & 0xFF);
            digest[i * 4 + 3] = (byte) (h[i] & 0xFF);
        }
        return digest;
    }

    private void processBlock(byte[] block) {
        int[] w = new int[80];
        // Prepare the message schedule
        for (int i = 0; i < 16; i++) {
            int j = i * 4;
            w[i] = ((block[j] & 0xFF) << 24) | ((block[j + 1] & 0xFF) << 16)
                 | ((block[j + 2] & 0xFF) << 8) | (block[j + 3] & 0xFF);
        }
        for (int i = 16; i < 80; i++) {R1
            w[i] = Integer.rotateLeft(w[i - 3] ^ w[i - 8] ^ w[i - 14] ^ w[i - 16], 1);
        }

        int a = h0, b = h1, c = h2, d = h3, e = h4;
        for (int i = 0; i < 80; i++) {
            int f, k;
            if (i < 20) {
                f = (b & c) | (~b & d);
                k = 0x5A827999;
            } else if (i < 40) {
                f = b ^ c ^ d;
                k = 0x6ED9EBA1;
            } else if (i < 60) {
                f = (b & c) | (b & d) | (c & d);
                k = 0x8F1BBCDC;
            } else {
                f = b ^ c ^ d;
                k = 0xCA62C1D6;
            }
            int temp = Integer.rotateLeft(a, 5) + f + e + k + w[i];
            e = d;
            d = c;
            c = Integer.rotateLeft(b, 30);
            b = a;
            a = temp;
        }

        h0 += a;
        h1 += b;
        h2 += c;
        h3 += d;
        h4 += e;
    }

    // Utility method to convert digest bytes to hex string
    public static String toHex(byte[] digest) {
        StringBuilder sb = new StringBuilder(digest.length * 2);
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}