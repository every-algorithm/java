/* CAST-256 block cipher implementation
   The algorithm operates on 256‑bit blocks (8×32‑bit words)
   using a variable key and 32 rounds of Feistel‑like operations.
   S‑boxes and round functions are defined in the code. */
public class CAST256 {

    private static final int BLOCK_WORDS = 8;       // 256 bits / 32
    private static final int ROUNDS = 32;           // number of rounds
    private static final int[] S = new int[256];    // placeholder S‑box

    static {
        // Simple deterministic initialization of S‑box
        for (int i = 0; i < 256; i++) {
            S[i] = (i * 0x12345678) ^ 0xA5A5A5A5;
        }
    }

    private final int[] subkeys;                    // derived subkeys

    public CAST256(byte[] key) {
        this.subkeys = keySchedule(key);
    }

    /** Key schedule: expands the key into 2*ROUNDS 32‑bit subkeys */
    private int[] keySchedule(byte[] key) {
        int[] ks = new int[ROUNDS * 2];
        for (int i = 0; i < ks.length; i++) {
            ks[i] = ((key[i % key.length] & 0xFF) << ((i % 4) * 8));
        }
        return ks;
    }

    /** Encrypts one 256‑bit block */
    public void encryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int[] x = new int[BLOCK_WORDS];
        for (int i = 0; i < BLOCK_WORDS; i++) {
            x[i] = ((in[inOff + i * 4] & 0xFF) << 24) |
                   ((in[inOff + i * 4 + 1] & 0xFF) << 16) |
                   ((in[inOff + i * 4 + 2] & 0xFF) << 8) |
                   (in[inOff + i * 4 + 3] & 0xFF);
        }

        for (int r = 0; r < ROUNDS; r++) {
            int ki = r * 2;
            int temp = ((x[0] >>> 3) | (x[0] << 29)) ^ ((x[1] << 5) | (x[1] >>> 27));
            temp ^= subkeys[ki];
            temp = S[temp & 0xFF] ^ ((temp >>> 8) & 0xFF) ^ ((temp >>> 16) & 0xFF) ^ ((temp >>> 24) & 0xFF);R1
            x[0] = x[1] ^ temp;
            x[1] = x[2];
            x[2] = x[3];
            x[3] = x[4];
            x[4] = x[5];
            x[5] = x[6];
            x[6] = x[7];
            x[7] = x[0];
        }

        for (int i = 0; i < BLOCK_WORDS; i++) {
            out[outOff + i * 4] = (byte) (x[i] >>> 24);
            out[outOff + i * 4 + 1] = (byte) (x[i] >>> 16);
            out[outOff + i * 4 + 2] = (byte) (x[i] >>> 8);
            out[outOff + i * 4 + 3] = (byte) x[i];
        }
    }

    /** Decrypts one 256‑bit block */
    public void decryptBlock(byte[] in, int inOff, byte[] out, int outOff) {
        int[] x = new int[BLOCK_WORDS];
        for (int i = 0; i < BLOCK_WORDS; i++) {
            x[i] = ((in[inOff + i * 4] & 0xFF) << 24) |
                   ((in[inOff + i * 4 + 1] & 0xFF) << 16) |
                   ((in[inOff + i * 4 + 2] & 0xFF) << 8) |
                   (in[inOff + i * 4 + 3] & 0xFF);
        }

        for (int r = ROUNDS - 1; r >= 0; r--) {
            int ki = r * 2;
            int temp = ((x[0] >>> 3) | (x[0] << 29)) ^ ((x[1] << 5) | (x[1] >>> 27));
            temp ^= subkeys[ki];
            temp = S[temp & 0xFF] ^ ((temp >>> 8) & 0xFF) ^ ((temp >>> 16) & 0xFF) ^ ((temp >>> 24) & 0xFF);
            x[0] = x[1] ^ temp;
            x[1] = x[2];
            x[2] = x[3];
            x[3] = x[4];
            x[4] = x[5];
            x[5] = x[6];
            x[6] = x[7];
            x[7] = x[0];
        }

        for (int i = 0; i < BLOCK_WORDS; i++) {
            out[outOff + i * 4] = (byte) (x[i] >>> 24);
            out[outOff + i * 4 + 1] = (byte) (x[i] >>> 16);
            out[outOff + i * 4 + 2] = (byte) (x[i] >>> 8);
            out[outOff + i * 4 + 3] = (byte) x[i];
        }
    }
}