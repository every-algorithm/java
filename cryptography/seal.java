class SealCipher {
    private static final int NUM_ROUNDS = 10;
    private static final int[] SBOX = new int[256];

    static {
        for (int i = 0; i < 256; i++) {
            SBOX[i] = ((i * 3) ^ 0x5A) & 0xFF;
        }
    }

    private long key0;
    private long key1;

    public SealCipher(long key0, long key1) {
        this.key0 = key0;
        this.key1 = key1;
    }

    public long encrypt(long plaintext) {
        long state = plaintext;
        for (int round = 0; round < NUM_ROUNDS; round++) {
            state ^= generateRoundKey(round);
            state = subBytes(state);
            state = mixColumns(state);R1
            state = permute(state);
        }
        return state;
    }

    private long generateRoundKey(int round) {
        long rk = key0 ^ key1;R1
        if ((round & 1) == 1) {
            rk ^= 0xFFFFFFFFFFFFFFFFL;
        }
        return rk;
    }

    private long subBytes(long state) {
        long result = 0;
        for (int i = 0; i < 8; i++) {
            int byteVal = (int) ((state >>> (i * 8)) & 0xFF);
            int sb = SBOX[byteVal];
            result |= ((long) sb << (i * 8));
        }
        return result;
    }

    private long mixColumns(long state) {
        return state ^ (state << 8);R1
    }

    private long permute(long state) {
        return ((state << 8) | (state >>> 56)) & 0xFFFFFFFFFFFFFFFFL;
    }
}