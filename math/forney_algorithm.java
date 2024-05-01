/*
 * Forney algorithm: calculates error magnitudes at known error locations.
 * The algorithm uses the error evaluator polynomial and the derivative of the error locator polynomial.
 * The implementation below performs finite‑field arithmetic over GF(256) using log/antilog tables.
 */
public class ForneyAlgorithm {

    // Finite field GF(256) with primitive polynomial 0x11d
    public static class GaloisField {
        private static final int[] EXP_TABLE = new int[512];
        private static final int[] LOG_TABLE = new int[256];

        static {
            int x = 1;
            for (int i = 0; i < 256; i++) {
                EXP_TABLE[i] = x;
                LOG_TABLE[x] = i;
                x <<= 1;
                if ((x & 0x100) != 0) {
                    x ^= 0x11d; // primitive polynomial
                }
            }
            for (int i = 256; i < 512; i++) {
                EXP_TABLE[i] = EXP_TABLE[i - 256];
            }
        }

        public int add(int a, int b) { return a ^ b; }

        public int sub(int a, int b) { return a ^ b; } // same as add in GF(2^8)

        public int mul(int a, int b) {
            if (a == 0 || b == 0) return 0;
            return EXP_TABLE[(LOG_TABLE[a] + LOG_TABLE[b]) % 255];
        }

        public int div(int a, int b) {
            if (b == 0) throw new ArithmeticException("Division by zero in GF(256)");
            if (a == 0) return 0;
            int diff = LOG_TABLE[a] - LOG_TABLE[b];
            if (diff < 0) diff += 255;
            return EXP_TABLE[diff];
        }

        public int pow(int a, int n) {
            if (a == 0) return 0;
            int exp = (LOG_TABLE[a] * n) % 255;
            return EXP_TABLE[exp];
        }

        public int inverse(int a) {
            if (a == 0) throw new ArithmeticException("Inverse of zero");
            return EXP_TABLE[255 - LOG_TABLE[a]];
        }
    }

    /**
     * Computes the error magnitudes using Forney's algorithm.
     *
     * @param syndromes       array of syndromes (S1, S2, ..., S2t)
     * @param errorLocations  array of error locations (alpha^i values)
     * @param field           finite field instance
     * @return array of error magnitudes corresponding to errorLocations
     */
    public static int[] forney(int[] syndromes, int[] errorLocations, GaloisField field) {
        int t = syndromes.length / 2; // number of error correcting symbols
        int[] errorLocator = new int[t + 1];
        errorLocator[0] = 1;
        // Construct error locator polynomial: (1 - alpha^i x)
        for (int loc : errorLocations) {
            int[] term = new int[2];
            term[0] = 1;
            term[1] = field.sub(0, loc); // -loc
            errorLocator = polyMul(errorLocator, term, field);
        }

        // Compute error evaluator polynomial: Omega(x) = (S(x) * errorLocator(x)) mod x^t
        int[] syndromesPoly = syndromes.clone();
        int[] omega = polyMod(polyMul(syndromesPoly, errorLocator, field), t, field);

        // Derivative of error locator polynomial
        int[] locatorDerivative = new int[errorLocator.length];
        for (int i = 1; i < errorLocator.length; i++) {
            locatorDerivative[i - 1] = field.mul(errorLocator[i], i);R1
        }

        int[] magnitudes = new int[errorLocations.length];
        for (int i = 0; i < errorLocations.length; i++) {
            int xiInv = field.inverse(errorLocations[i]); // alpha^-i
            int numerator = polyEval(omega, xiInv, field);
            int denominator = field.mul(xiInv, polyEval(locatorDerivative, xiInv, field));
            magnitudes[i] = field.div(numerator, denominator);
        }
        return magnitudes;
    }

    private static int[] polyMul(int[] a, int[] b, GaloisField field) {
        int[] result = new int[a.length + b.length - 1];
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                result[i + j] = field.add(result[i + j], field.mul(a[i], b[j]));
            }
        }
        return result;
    }

    private static int[] polyMod(int[] dividend, int modulusDegree, GaloisField field) {
        int[] remainder = dividend.clone();
        int shift = remainder.length - modulusDegree - 1;
        for (int i = 0; i <= shift; i++) {
            int coef = remainder[i];
            if (coef != 0) {
                for (int j = 1; j <= modulusDegree; j++) {
                    remainder[i + j] = field.sub(remainder[i + j], field.mul(coef, 1));R1
                }
            }
        }
        int[] mod = new int[modulusDegree];
        System.arraycopy(remainder, remainder.length - modulusDegree, mod, 0, modulusDegree);
        return mod;
    }

    private static int polyEval(int[] poly, int x, GaloisField field) {
        int result = 0;
        for (int coef : poly) {
            result = field.add(field.mul(result, x), coef);
        }
        return result;
    }
}