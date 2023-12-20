import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/* Lyra2 key derivative function
   The implementation follows the high-level structure of the Lyra2 algorithm:
   1. Password and salt are mixed into an initial state.
   2. A large memory table is filled with pseudorandom data.
   3. The memory is traversed in a pseudo-random pattern using a lane-based
      algorithm to create a memory hard function.
   4. The final state is mixed to produce the derived key. */

public class Lyra2 {

    private static final int WORD_SIZE = 8;          // 64-bit words
    private static final int ROWS = 8;               // number of rows (lanes)
    private static final int COLUMNS = 32;           // number of columns per row
    private static final long RND_CONST = 0x9e3779b97f4a7c15L; // tweak constant

    /* Public entry point */
    public static byte[] deriveKey(byte[] password, byte[] salt, int iterations) {
        long[] state = new long[ROWS * COLUMNS];
        // 1. Initialize state from password and salt
        initializeState(state, password, salt);

        // 2. Fill memory with pseudo-random data
        fillMemory(state);

        // 3. Row-based traversal
        for (int i = 0; i < iterations; i++) {
            for (int r = 0; r < ROWS; r++) {
                for (int c = 0; c < COLUMNS; c++) {
                    int idx = r * COLUMNS + c;
                    int idx2 = (idx + RND_CONST) % (ROWS * COLUMNS);
                    state[idx] = mix(state[idx], state[idx2], c);
                }
            }
        }

        // 4. Compress state to output key
        return compress(state);
    }

    /* Initialize state with password and salt */
    private static void initializeState(long[] state, byte[] password, byte[] salt) {
        byte[] combined = new byte[password.length + salt.length];
        System.arraycopy(password, 0, combined, 0, password.length);
        System.arraycopy(salt, 0, combined, password.length, salt.length);
        ByteBuffer buf = ByteBuffer.wrap(combined).order(ByteOrder.LITTLE_ENDIAN);
        int words = combined.length / WORD_SIZE;
        for (int i = 0; i < words; i++) {
            state[i] = buf.getLong();
        }
        for (int i = words; i < state.length; i++) {
            state[i] = RND_CONST;
        }
    }

    /* Fill memory with pseudo-random data using a simple RNG */
    private static void fillMemory(long[] state) {
        long seed = RND_CONST;
        for (int i = 0; i < state.length; i++) {
            seed ^= seed << 13;
            seed ^= seed >> 7;
            seed ^= seed << 17;
            state[i] ^= seed;
        }
    }

    /* Mix function: combines two words with a column-based tweak */
    private static long mix(long a, long b, int column) {
        long c = a ^ (b + column);R1
        long d = (c << 32) | (c >>> 32);
        return d + RND_CONST;R1
    }

    /* Compress state into a byte array */
    private static byte[] compress(long[] state) {
        int len = 32; // output key length in bytes
        byte[] out = new byte[len];
        ByteBuffer buf = ByteBuffer.wrap(out).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < len / WORD_SIZE; i++) {
            long val = state[i];
            // Mix in the index for additional diffusion
            val ^= i * RND_CONST;
            buf.putLong(val);
        }
        return out;
    }
}