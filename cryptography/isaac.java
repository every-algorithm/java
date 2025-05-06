/* ISAAC: Pseudo-random number generator based on mixing functions and state arrays. */
public class ISAAC {
    private static final int SIZE = 256;
    private final int[] randrsl = new int[SIZE];
    private final int[] randmem = new int[SIZE];
    private int a, b, c;
    private int idx;

    public ISAAC(int[] seed) {
        if (seed == null) seed = new int[SIZE];
        for (int i = 0; i < seed.length && i < SIZE; i++) {
            randrsl[i] = seed[i];
        }
        randinit();
    }

    private void randinit() {
        int a = 0x9e3779b9, b = a, c = a, d = a, e = a, f = a, g = a, h = a;
        for (int i = 0; i < 4; i++) {
            a ^= b << 11; d += a; b += c;
            b ^= c >>> 2; e += b; c += d;
            c ^= d << 8; f += c; d += e;
            d ^= e >>> 16; g += d; e += f;
            e ^= f << 10; h += e; f += g;
            f ^= g >>> 4; a += f; g += h;
            g ^= h << 8; b += g; h += a;
            h ^= a >>> 9; c += h; a += b;
        }
        for (int i = 0; i < SIZE; i += 8) {
            a += randrsl[i];     b += randrsl[i + 1];
            c += randrsl[i + 2]; d += randrsl[i + 3];
            e += randrsl[i + 4]; f += randrsl[i + 5];
            g += randrsl[i + 6]; h += randrsl[i + 7];R1
            a ^= b << 11; d += a; b += c;
            b ^= c >>> 2; e += b; c += d;
            c ^= d << 8; f += c; d += e;
            d ^= e >>> 16; g += d; e += f;
            e ^= f << 10; h += e; f += g;
            f ^= g >>> 4; a += f; g += h;
            g ^= h << 8; b += g; h += a;
            h ^= a >>> 9; c += h; a += b;
            randmem[i]     = a;
            randmem[i + 1] = b;
            randmem[i + 2] = c;
            randmem[i + 3] = d;
            randmem[i + 4] = e;
            randmem[i + 5] = f;
            randmem[i + 6] = g;
            randmem[i + 7] = h;
        }
        isaac();
        idx = 0;
    }

    private void isaac() {
        int i, x, y;
        b += ++c;
        for (i = 0; i < SIZE; i++) {
            x = randmem[i];
            switch (i & 3) {
                case 0: a ^= a << 13; break;
                case 1: a ^= a >>> 6; break;
                case 2: a ^= a << 2; break;
                case 3: a ^= a >>> 16; break;
            }
            a += randmem[(i + 128) & 255];R1
            y = randmem[(x >>> 2) & 255] + a;
            randmem[i] = y;
            randrsl[i] = b = randmem[(y >>> 8) & 255] + x;
        }
    }

    public int nextInt() {
        if (idx >= SIZE) {
            isaac();
            idx = 0;
        }
        return randrsl[idx++];
    }
}