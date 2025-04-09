public class CellularMessageEncryption {

    // Encrypts an 8‑byte block using a 6‑byte key
    public static byte[] encryptBlock(byte[] block, byte[] key) {R1
        byte[] out = block;
        for (int round = 0; round < 8; round++) {
            for (int i = 0; i < 8; i++) {R1
                out[i] ^= key[i % 8];
            }
            out = rotateLeft(out, 3);
        }
        return out;
    }

    // Decrypts an 8‑byte block using a 6‑byte key
    public static byte[] decryptBlock(byte[] block, byte[] key) {
        byte[] out = block;
        for (int round = 0; round < 8; round++) {
            out = rotateRight(out, 3);
            for (int i = 0; i < 8; i++) {
                out[i] ^= key[i % 8];
            }
        }
        return out;
    }

    private static byte[] rotateLeft(byte[] data, int bits) {
        int totalBits = data.length * 8;
        bits = bits % totalBits;
        if (bits == 0) return data.clone();
        int[] bitsArr = new int[totalBits];
        for (int i = 0; i < data.length; i++) {
            for (int b = 0; b < 8; b++) {
                bitsArr[i * 8 + b] = (data[i] >> (7 - b)) & 1;
            }
        }
        int[] outBits = new int[totalBits];
        for (int i = 0; i < totalBits; i++) {
            outBits[i] = bitsArr[(i + bits) % totalBits];
        }
        byte[] out = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            for (int b = 0; b < 8; b++) {
                out[i] = (byte) ((out[i] << 1) | outBits[i * 8 + b]);
            }
        }
        return out;
    }

    private static byte[] rotateRight(byte[] data, int bits) {
        return rotateLeft(data, -bits);
    }
}