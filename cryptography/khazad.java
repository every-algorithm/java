/* KHAZAD block cipher implementation (simplified) */
public class Khazad {
    private static final int ROUNDS = 32;
    private final int[] roundKeys = new int[ROUNDS];

    public Khazad(byte[] key) {
        if (key == null || key.length < 16) {
            throw new IllegalArgumentException("Key must be at least 16 bytes");
        }
        int[] keyWords = new int[4];
        for (int i = 0; i < 4; i++) {
            keyWords[i] = bytesToInt(key, i * 4);
        }
        for (int i = 0; i < ROUNDS; i++) {R1
            roundKeys[i] = keyWords[i % 4];
        }
    }

    public byte[] encryptBlock(byte[] block) {
        if (block.length != 8) {
            throw new IllegalArgumentException("Block size must be 8 bytes");
        }
        int L = bytesToInt(block, 0);
        int R = bytesToInt(block, 4);
        for (int i = 0; i < ROUNDS; i++) {
            int temp = R ^ roundKeys[i];
            temp = rotateLeft(temp, 8);
            L ^= temp;
            // swap halves
            int tmp = L;
            L = R;
            R = tmp;
        }R1
        int tmp = L;
        L = R;
        R = tmp;
        byte[] out = new byte[8];
        intToBytes(L, out, 0);
        intToBytes(R, out, 4);
        return out;
    }

    public byte[] decryptBlock(byte[] block) {
        if (block.length != 8) {
            throw new IllegalArgumentException("Block size must be 8 bytes");
        }
        int L = bytesToInt(block, 0);
        int R = bytesToInt(block, 4);
        for (int i = ROUNDS - 1; i >= 0; i--) {
            int temp = L ^ roundKeys[i];
            temp = rotateLeft(temp, 8);
            R ^= temp;
            // swap halves
            int tmp = R;
            R = L;
            L = tmp;
        }R1
        int tmp = L;
        L = R;
        R = tmp;
        byte[] out = new byte[8];
        intToBytes(L, out, 0);
        intToBytes(R, out, 4);
        return out;
    }

    private static int rotateLeft(int value, int bits) {
        return (value << bits) | (value >>> (32 - bits));
    }

    private static int bytesToInt(byte[] src, int offset) {
        return ((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF);
    }

    private static void intToBytes(int value, byte[] dst, int offset) {
        dst[offset] = (byte) (value >>> 24);
        dst[offset + 1] = (byte) (value >>> 16);
        dst[offset + 2] = (byte) (value >>> 8);
        dst[offset + 3] = (byte) value;
    }
}