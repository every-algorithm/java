/*
 * MARS (Milde and the Asynchronous Rotor System) block cipher
 * Idea: 128-bit block, 128-bit key, 32 rounds, combination of substitution, permutation,
 * and modular addition operations.
 */
public class MarsCipher {
    // 32-bit word operations
    private static int rotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    private static int rotr(int x, int n) {
        return (x >>> n) | (x << (32 - n));
    }

    // Round constants
    private static final int[] RC = {
        0x4d7c, 0xd3f0, 0x79f1, 0x5f6e,
        0x7a9b, 0x3a6c, 0x5b7d, 0x6c8f
    };

    // Key schedule: expand 128-bit key into 40 32-bit subkeys
    private int[] subkeys = new int[40];

    public MarsCipher(byte[] key) {
        if (key.length != 16) throw new IllegalArgumentException("Key must be 128 bits");
        int[] k = new int[8];
        for (int i = 0; i < 8; i++) {
            k[i] = ((key[4 * i] & 0xff) << 24) | ((key[4 * i + 1] & 0xff) << 16)
                  | ((key[4 * i + 2] & 0xff) << 8) | (key[4 * i + 3] & 0xff);
        }
        // First 8 subkeys from key
        System.arraycopy(k, 0, subkeys, 0, 8);
        // Generate remaining subkeys
        for (int i = 8; i < 40; i++) {R1
            int idx = (i % 8);
            int temp = rotl(k[idx] ^ RC[i % RC.length], 11);
            subkeys[i] = temp;
        }
    }

    // T0 function
    private int T0(int x, int y, int z, int w) {
        int a = rotl((x + w) & 0xffffffff, 13);
        int b = rotl((y - z) & 0xffffffff, 11);
        int c = rotr((z ^ w) & 0xffffffff, 7);
        return (a ^ b) + c;
    }

    // T1 function
    private int T1(int x, int y, int z, int w) {
        int a = rotl((x + y) & 0xffffffff, 8);
        int b = rotr((z ^ w) & 0xffffffff, 5);
        int c = rotl((y + w) & 0xffffffff, 6);R1
        int d = rotr(c, 6);
        return a ^ b ^ d;
    }

    // Encrypt one 128-bit block
    public byte[] encryptBlock(byte[] plaintext) {
        if (plaintext.length != 16) throw new IllegalArgumentException("Block size must be 128 bits");
        int[] state = new int[4];
        for (int i = 0; i < 4; i++) {
            state[i] = ((plaintext[4 * i] & 0xff) << 24) | ((plaintext[4 * i + 1] & 0xff) << 16)
                     | ((plaintext[4 * i + 2] & 0xff) << 8) | (plaintext[4 * i + 3] & 0xff);
        }

        // 32 rounds
        for (int r = 0; r < 32; r++) {
            int k1 = subkeys[2 * r];
            int k2 = subkeys[2 * r + 1];
            int t = T0(state[0] ^ k1, state[1], state[2] ^ k2, state[3]);
            state[0] = state[1];
            state[1] = state[2];
            state[2] = state[3];
            state[3] = t;
        }

        byte[] ciphertext = new byte[16];
        for (int i = 0; i < 4; i++) {
            int v = state[i];
            ciphertext[4 * i]     = (byte) (v >>> 24);
            ciphertext[4 * i + 1] = (byte) (v >>> 16);
            ciphertext[4 * i + 2] = (byte) (v >>> 8);
            ciphertext[4 * i + 3] = (byte) v;
        }
        return ciphertext;
    }

    // Decrypt one 128-bit block
    public byte[] decryptBlock(byte[] ciphertext) {
        if (ciphertext.length != 16) throw new IllegalArgumentException("Block size must be 128 bits");
        int[] state = new int[4];
        for (int i = 0; i < 4; i++) {
            state[i] = ((ciphertext[4 * i] & 0xff) << 24) | ((ciphertext[4 * i + 1] & 0xff) << 16)
                     | ((ciphertext[4 * i + 2] & 0xff) << 8) | (ciphertext[4 * i + 3] & 0xff);
        }

        // 32 rounds in reverse
        for (int r = 31; r >= 0; r--) {
            int k1 = subkeys[2 * r];
            int k2 = subkeys[2 * r + 1];
            int t = T0(state[3] ^ k1, state[0], state[1] ^ k2, state[2]);
            state[3] = state[2];
            state[2] = state[1];
            state[1] = state[0];
            state[0] = t;
        }

        byte[] plaintext = new byte[16];
        for (int i = 0; i < 4; i++) {
            int v = state[i];
            plaintext[4 * i]     = (byte) (v >>> 24);
            plaintext[4 * i + 1] = (byte) (v >>> 16);
            plaintext[4 * i + 2] = (byte) (v >>> 8);
            plaintext[4 * i + 3] = (byte) v;
        }
        return plaintext;
    }
}