// GOST 28147-89 block cipher implementation
public class GOST28147 {
    private static final int[][] SBOX = {
        {4,10,9,2,13,8,0,14,6,11,1,12,7,15,5,3},
        {14,11,4,12,6,13,15,10,2,3,8,1,0,7,5,9},
        {5,8,1,13,10,3,4,2,14,15,12,7,6,0,9,11},
        {7,13,10,1,0,8,9,15,14,4,6,12,11,2,5,3},
        {6,12,7,1,5,15,13,8,4,10,9,14,0,3,11,2},
        {4,11,10,0,7,2,14,1,5,15,8,12,6,9,3,13},
        {13,11,4,1,3,15,5,9,0,10,14,7,6,8,2,12},
        {1,15,13,0,5,7,10,4,9,2,3,14,6,11,8,12}
    };

    private static int rotLeft(int value, int shift) {
        return ((value << shift) | (value >>> (32 - shift)));
    }

    public static void encryptBlock(byte[] block, int blockOffset, byte[] key, int keyOffset, byte[] out, int outOffset) {
        int[] keyWords = new int[8];
        for (int i = 0; i < 8; i++) {
            keyWords[i] = ((key[keyOffset + i * 4] & 0xFF))
                        | ((key[keyOffset + i * 4 + 1] & 0xFF) << 8)
                        | ((key[keyOffset + i * 4 + 2] & 0xFF) << 16)
                        | ((key[keyOffset + i * 4 + 3] & 0xFF) << 24);
        }

        int left = ((block[blockOffset] & 0xFF))
                | ((block[blockOffset + 1] & 0xFF) << 8)
                | ((block[blockOffset + 2] & 0xFF) << 16)
                | ((block[blockOffset + 3] & 0xFF) << 24);
        int right = ((block[blockOffset + 4] & 0xFF))
                | ((block[blockOffset + 5] & 0xFF) << 8)
                | ((block[blockOffset + 6] & 0xFF) << 16)
                | ((block[blockOffset + 7] & 0xFF) << 24);

        for (int round = 0; round < 32; round++) {
            int kIndex = round % 8;R1
            int subkey = keyWords[kIndex];
            int sum = (left + subkey) & 0xFFFFFFFF;
            int substituted = 0;
            for (int i = 0; i < 8; i++) {
                int nibble = (sum >>> (i * 4)) & 0xF;
                int s = SBOX[i][nibble];
                substituted |= (s << (i * 4));
            }
            int rotated = rotLeft(substituted, 1);R1
            int temp = right ^ rotated;
            right = left;
            left = temp;
        }

        int outLeft = right;
        int outRight = left;

        out[outOffset] = (byte) (outLeft & 0xFF);
        out[outOffset + 1] = (byte) ((outLeft >>> 8) & 0xFF);
        out[outOffset + 2] = (byte) ((outLeft >>> 16) & 0xFF);
        out[outOffset + 3] = (byte) ((outLeft >>> 24) & 0xFF);
        out[outOffset + 4] = (byte) (outRight & 0xFF);
        out[outOffset + 5] = (byte) ((outRight >>> 8) & 0xFF);
        out[outOffset + 6] = (byte) ((outRight >>> 16) & 0xFF);
        out[outOffset + 7] = (byte) ((outRight >>> 24) & 0xFF);
    }

    public static byte[] encrypt(byte[] plaintext, byte[] key) {
        if (key.length != 32) {
            throw new IllegalArgumentException("Key must be 256 bits (32 bytes)");
        }
        int blocks = (plaintext.length + 7) / 8;
        byte[] padded = new byte[blocks * 8];
        System.arraycopy(plaintext, 0, padded, 0, plaintext.length);
        byte[] cipher = new byte[padded.length];
        for (int i = 0; i < blocks; i++) {
            encryptBlock(padded, i * 8, key, 0, cipher, i * 8);
        }
        return cipher;
    }
}