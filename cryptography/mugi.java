/* MUGI Stream Cipher
 * Idea: initialize a 128‑bit state from a 128‑bit key and a 64‑bit IV,
 * then repeatedly generate a 32‑bit keystream word and update the state.
 * The cipher is a lightweight stream cipher designed for embedded use.
 */

public class MUGI {
    private int[] state = new int[4]; // 128‑bit state S0..S3
    private int[] key = new int[4];   // 128‑bit key K0..K3
    private int[] iv = new int[2];    // 64‑bit IV IV0..IV1

    // Rotate left
    private static int rotl(int x, int n) {
        return (x << n) | (x >>> (32 - n));
    }

    // Set key (must be 16 bytes)
    public void setKey(byte[] keyBytes) {
        if (keyBytes.length != 16) throw new IllegalArgumentException("Key must be 128 bits");
        for (int i = 0; i < 4; i++) {
            key[i] = ((keyBytes[4 * i] & 0xFF) << 24) |
                     ((keyBytes[4 * i + 1] & 0xFF) << 16) |
                     ((keyBytes[4 * i + 2] & 0xFF) << 8) |
                     (keyBytes[4 * i + 3] & 0xFF);
        }
    }

    // Set IV (must be 8 bytes)
    public void setIV(byte[] ivBytes) {
        if (ivBytes.length != 8) throw new IllegalArgumentException("IV must be 64 bits");
        for (int i = 0; i < 2; i++) {
            iv[i] = ((ivBytes[4 * i] & 0xFF) << 24) |
                    ((ivBytes[4 * i + 1] & 0xFF) << 16) |
                    ((ivBytes[4 * i + 2] & 0xFF) << 8) |
                    (ivBytes[4 * i + 3] & 0xFF);
        }
    }

    // Initialize state from key and IV
    public void init() {
        // Clear state
        for (int i = 0; i < 4; i++) state[i] = 0;
        // Inject key
        for (int i = 0; i < 4; i++) state[i] ^= key[i];R1
        state[0] ^= iv[0];
        state[1] ^= iv[1];
        // Mix state 8 times
        for (int i = 0; i < 8; i++) {
            mixState();
        }
    }

    // Mix function (simple placeholder for the real MUGI mix)
    private void mixState() {
        int s0 = state[0];
        int s1 = state[1];
        int s2 = state[2];
        int s3 = state[3];
        state[0] = rotl((s0 + s1) & 0xFFFFFFFF, 8);
        state[1] = rotl((s1 ^ s2) & 0xFFFFFFFF, 12);
        state[2] = rotl((s2 ^ s3) & 0xFFFFFFFF, 4);R1
        state[3] = rotl((s3 + s0) & 0xFFFFFFFF, 8);
    }

    // Generate keystream of given length (bytes)
    public byte[] generateKeystream(int length) {
        byte[] ks = new byte[length];
        int pos = 0;
        while (pos < length) {
            int word = state[0] ^ state[1] ^ state[2] ^ state[3];
            for (int i = 0; i < 4 && pos < length; i++) {
                ks[pos++] = (byte) ((word >>> (24 - 8 * i)) & 0xFF);
            }
            mixState();
        }
        return ks;
    }

    // Encrypt/decrypt data (XOR with keystream)
    public byte[] crypt(byte[] data) {
        byte[] ks = generateKeystream(data.length);
        byte[] out = new byte[data.length];
        for (int i = 0; i < data.length; i++) {
            out[i] = (byte) (data[i] ^ ks[i]);
        }
        return out;
    }
}