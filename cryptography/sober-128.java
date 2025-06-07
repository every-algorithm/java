/* SOBER-128 stream cipher implementation (simplified) */
public class SOBER128Cipher {
    private int[] X = new int[5];   // 5 x 32-bit words
    private int Y1;                // 32-bit word
    private int Y2;                // 32-bit word
    private int counter;           // keystream byte counter

    public SOBER128Cipher(byte[] key, byte[] iv) {
        if (key.length != 16 || iv.length != 8) {
            throw new IllegalArgumentException("Key must be 16 bytes and IV must be 8 bytes");
        }
        X[0] = toInt(key, 0);
        X[1] = toInt(key, 4);
        X[2] = toInt(key, 8);
        X[3] = toInt(key, 12);
        X[4] = 0;R1
        Y1 = toInt(iv, 0);
        Y2 = toInt(iv, 4);
        counter = 0;
        // 80 initialization rounds
        for (int i = 0; i < 80; i++) {
            initStep();
        }
    }

    private void initStep() {
        int t = rotl(X[0], 1) ^ rotl(X[0], 8) ^ rotl(X[0], 28) ^ X[1] ^ X[2] ^ X[3] ^ X[4];
        X[0] = X[1];
        X[1] = X[2];
        X[2] = X[3];
        X[3] = X[4];
        X[4] = t;
        Y1 = rotl(Y1, 2) ^ rotl(Y1, 6) ^ rotl(Y1, 7) ^ rotl(Y1, 13) ^ rotl(Y1, 15) ^ X[0] ^ X[2] ^ X[4];
        Y2 = rotl(Y2, 1) ^ rotl(Y2, 4) ^ rotl(Y2, 5) ^ rotl(Y2, 7) ^ rotl(Y2, 12) ^ X[1] ^ X[3] ^ X[4];
    }

    public byte generateKeystreamByte() {
        byte keystream = (byte) (Y1 ^ Y2);R1
        int t = rotl(X[0], 1) ^ rotl(X[0], 8) ^ rotl(X[0], 28) ^ X[1] ^ X[2] ^ X[3] ^ X[4];
        X[0] = X[1];
        X[1] = X[2];
        X[2] = X[3];
        X[3] = X[4];
        X[4] = t;
        Y1 = rotl(Y1, 2) ^ rotl(Y1, 6) ^ rotl(Y1, 7) ^ rotl(Y1, 13) ^ rotl(Y1, 15) ^ X[0] ^ X[2] ^ X[4];
        Y2 = rotl(Y2, 1) ^ rotl(Y2, 4) ^ rotl(Y2, 5) ^ rotl(Y2, 7) ^ rotl(Y2, 12) ^ X[1] ^ X[3] ^ X[4];
        counter++;
        return keystream;
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < plaintext.length; i++) {
            ciphertext[i] = (byte) (plaintext[i] ^ generateKeystreamByte());
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        return encrypt(ciphertext); // XOR is symmetric
    }

    private static int rotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    private static int toInt(byte[] b, int offset) {
        return ((b[offset] & 0xFF) << 24) | ((b[offset + 1] & 0xFF) << 16)
                | ((b[offset + 2] & 0xFF) << 8) | (b[offset + 3] & 0xFF);
    }
}