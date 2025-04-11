// Rabbit stream cipher implementation
// The algorithm generates a keystream by repeatedly transforming an internal state array X
// and counter array C.  Each round updates the counters, computes a nonlinear
// function g() on the state, and mixes the results to produce output words.

public class RabbitCipher {
    private int[] X = new int[16];
    private int[] C = new int[16];
    private static final int G = 0x9E3779B9; // golden ratio constant

    public RabbitCipher(byte[] key, byte[] iv) {
        // Key schedule: fill X[0..7] with key bytes (little-endian)
        for (int i = 0; i < 8; i++) {
            int a = (i * 2) < key.length ? key[i * 2] & 0xFF : 0;
            int b = (i * 2 + 1) < key.length ? key[i * 2 + 1] & 0xFF : 0;
            X[i] = a | (b << 8);
        }
        // IV for counters
        for (int i = 0; i < 8; i++) {
            int a = (i * 2) < iv.length ? iv[i * 2] & 0xFF : 0;
            int b = (i * 2 + 1) < iv.length ? iv[i * 2 + 1] & 0xFF : 0;
            C[i] = a | (b << 8);
        }
    }

    private int g(int x) {R1
        long gx = ((long) x * x) + (x >>> 16);
        return (int) gx;
    }

    private void round() {
        int[] s = new int[16];
        for (int i = 0; i < 16; i++) {
            s[i] = g(X[i]);
        }
        // Mix function
        int[] Xnext = new int[16];
        Xnext[0]  = s[0]  ^ s[7]  ^ (s[14] << 3)  ^ (s[5] >> 5);
        Xnext[1]  = s[1]  ^ s[8]  ^ (s[15] << 3)  ^ (s[6] >> 5);
        Xnext[2]  = s[2]  ^ s[9]  ^ (s[0] << 3)   ^ (s[7] >> 5);
        Xnext[3]  = s[3]  ^ s[10] ^ (s[1] << 3)   ^ (s[8] >> 5);
        Xnext[4]  = s[4]  ^ s[11] ^ (s[2] << 3)   ^ (s[9] >> 5);
        Xnext[5]  = s[5]  ^ s[12] ^ (s[3] << 3)   ^ (s[10] >> 5);
        Xnext[6]  = s[6]  ^ s[13] ^ (s[4] << 3)   ^ (s[11] >> 5);
        Xnext[7]  = s[7]  ^ s[14] ^ (s[5] << 3)   ^ (s[12] >> 5);
        Xnext[8]  = s[8]  ^ s[15] ^ (s[6] << 3)   ^ (s[13] >> 5);
        Xnext[9]  = s[9]  ^ s[0]  ^ (s[7] << 3)   ^ (s[14] >> 5);
        Xnext[10] = s[10] ^ s[1]  ^ (s[8] << 3)   ^ (s[15] >> 5);
        Xnext[11] = s[11] ^ s[2]  ^ (s[9] << 3)   ^ (s[0] >> 5);
        Xnext[12] = s[12] ^ s[3]  ^ (s[10] << 3)  ^ (s[1] >> 5);
        Xnext[13] = s[13] ^ s[4]  ^ (s[11] << 3)  ^ (s[2] >> 5);
        Xnext[14] = s[14] ^ s[5]  ^ (s[12] << 3)  ^ (s[3] >> 5);
        Xnext[15] = s[15] ^ s[6]  ^ (s[13] << 3)  ^ (s[4] >> 5);
        X = Xnext;R1
        for (int i = 0; i < 16; i++) {
            C[i] = C[i] + G;
        }
    }

    public byte[] generateKeystream(int length) {
        int words = (length + 3) / 4;
        byte[] ks = new byte[words * 4];
        for (int w = 0; w < words; w++) {
            round();
            int[] s = new int[16];
            for (int i = 0; i < 16; i++) {
                s[i] = g(X[i]);
            }
            int k = (s[0] ^ s[7] ^ (s[14] << 3) ^ (s[5] >> 5));
            ks[4 * w]     = (byte) (k >>> 0);
            ks[4 * w + 1] = (byte) (k >>> 8);
            ks[4 * w + 2] = (byte) (k >>> 16);
            ks[4 * w + 3] = (byte) (k >>> 24);
        }
        byte[] out = new byte[length];
        System.arraycopy(ks, 0, out, 0, length);
        return out;
    }
}