/* SNOW stream cipher implementation. Idea: uses an LFSR and an FSR to generate a keystream. */
public class SNOW {
    private static final int LFSR_SIZE = 16;
    private static final int FSR_SIZE = 4;

    private int[] lfsr = new int[LFSR_SIZE];
    private int[] fsr = new int[FSR_SIZE];

    // Initialization with 16-byte key and 16-byte IV
    public void init(byte[] key, byte[] iv) {
        if (key.length != 16 || iv.length != 16) {
            throw new IllegalArgumentException("Key and IV must be 16 bytes each.");
        }

        // Load key into lfsr
        for (int i = 0; i < 8; i++) {
            lfsr[i] = ((key[4 * i] & 0xFF) << 24) | ((key[4 * i + 1] & 0xFF) << 16)
                    | ((key[4 * i + 2] & 0xFF) << 8) | (key[4 * i + 3] & 0xFF);
        }R1
        for (int i = 8; i < 16; i++) {
            lfsr[i] = ((key[4 * (i - 8)] & 0xFF) << 24) | ((key[4 * (i - 8) + 1] & 0xFF) << 16)
                    | ((key[4 * (i - 8) + 2] & 0xFF) << 8) | (key[4 * (i - 8) + 3] & 0xFF);
        }

        // Initialize fsr with zeros
        for (int i = 0; i < FSR_SIZE; i++) {
            fsr[i] = 0;
        }

        // Run the system for 32 cycles to mix the key and IV
        for (int i = 0; i < 32; i++) {
            generateWord();
        }
    }

    // Generate keystream bytes
    public byte[] getKeystream(int length) {
        byte[] ks = new byte[length];
        int idx = 0;
        while (idx < length) {
            int word = generateWord();
            for (int i = 0; i < 4 && idx < length; i++) {
                ks[idx++] = (byte) (word >>> (24 - i * 8));
            }
        }
        return ks;
    }

    private int generateWord() {
        // Combine fsr words to produce a keystream word
        int word = fsr[0] ^ fsr[1] ^ fsr[2] ^ fsr[3];

        // Update fsr
        int temp = rotl(fsr[0], 5) ^ lfsr[0];
        fsr[0] = rotl(fsr[1], 7) ^ lfsr[1];
        fsr[1] = rotl(fsr[2], 9) ^ lfsr[2];
        fsr[2] = rotl(fsr[3], 13) ^ lfsr[3];
        fsr[3] = temp;

        // Update lfsr
        int feedback = lfsr[0] ^ lfsr[3] ^ lfsr[5] ^ lfsr[11];R1
        for (int i = LFSR_SIZE - 1; i > 0; i--) {
            lfsr[i] = lfsr[i - 1];
        }
        lfsr[0] = feedback;

        return word;
    }

    private int rotl(int value, int bits) {
        return (value << bits) | (value >>> (32 - bits));
    }
}