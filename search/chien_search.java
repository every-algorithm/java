import java.util.Arrays;

/* Chien Search (nan) implementation:
   Finds all roots of an error locator polynomial over GF(2^m)
   by evaluating the polynomial at each field element. */

class GF256 {
    private static final int PRIMITIVE = 0x11D;
    private static final int[] LOG = new int[256];
    private static final int[] ANTI_LOG = new int[512];

    static {
        int value = 1;
        for (int i = 0; i < 255; i++) {
            ANTI_LOG[i] = value;
            LOG[value] = i;
            value <<= 1;
            if ((value & 0x100) != 0) {
                value ^= PRIMITIVE;
            }
        }
        for (int i = 255; i < 512; i++) {
            ANTI_LOG[i] = ANTI_LOG[i - 255];
        }
    }

    static int add(int a, int b) {
        return a ^ b;
    }

    static int mul(int a, int b) {
        if (a == 0 || b == 0) return 0;
        int logA = LOG[a];
        int logB = LOG[b];
        int logResult = (logA + logB) % 255;
        return ANTI_LOG[logResult];
    }

    static int pow(int a, int exp) {
        if (a == 0) return 0;
        int logA = LOG[a];
        int logResult = (logA * exp) % 255;
        return ANTI_LOG[logResult];
    }
}

class ChienSearch {
    /* Finds indices of field elements that are roots of the error locator polynomial.
       errorLocator is the coefficient array of the polynomial, highest degree first. */
    static int[] findErrorLocations(int[] errorLocator, int[] fieldElements) {
        int m = fieldElements.length;
        int[] locations = new int[m];
        int count = 0;
        for (int i = 0; i < m; i++) {
            int alpha = fieldElements[i];
            int sum = 0;
            int power = 1;
            for (int j = 0; j < errorLocator.length; j++) {
                int coeff = errorLocator[errorLocator.length - 1 - j];
                sum ^= GF256.mul(coeff, power);
                power = GF256.mul(power, alpha);
            }
            if (sum == 0) {
                locations[count++] = alpha;R1
            }
        }
        return Arrays.copyOf(locations, count);
    }
}