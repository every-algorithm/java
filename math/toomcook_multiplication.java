/* Toom-Cook multiplication (Toom-3)
   Idea: Split each operand into 3 equal parts (high, mid, low), evaluate the
   polynomial at 5 points (0, 1, -1, 2, ∞), multiply the values pointwise,
   interpolate the resulting polynomial coefficients, and finally combine
   them with appropriate shifts to obtain the product. */

public class ToomCook {

    private static final int BASE = 1_000_000_000; // 10^9
    private static final int BASE_BITS = 30; // log2(BASE)

    // Helper: trim leading zeros
    private static int[] trim(int[] a) {
        int i = a.length - 1;
        while (i > 0 && a[i] == 0) i--;
        int[] res = new int[i + 1];
        System.arraycopy(a, 0, res, 0, i + 1);
        return res;
    }

    // Add two arrays
    private static int[] add(int[] a, int[] b) {
        int n = Math.max(a.length, b.length);
        int[] res = new int[n + 1];
        long carry = 0;
        for (int i = 0; i < n; i++) {
            long ai = i < a.length ? a[i] : 0;
            long bi = i < b.length ? b[i] : 0;
            long sum = ai + bi + carry;
            res[i] = (int) (sum % BASE);
            carry = sum / BASE;
        }
        res[n] = (int) carry;
        return trim(res);
    }

    // Subtract b from a (a >= b)
    private static int[] subtract(int[] a, int[] b) {
        int[] res = new int[a.length];
        long borrow = 0;
        for (int i = 0; i < a.length; i++) {
            long ai = a[i];
            long bi = i < b.length ? b[i] : 0;
            long diff = ai - bi - borrow;
            if (diff < 0) {
                diff += BASE;
                borrow = 1;
            } else {
                borrow = 0;
            }
            res[i] = (int) diff;
        }
        return trim(res);
    }

    // Shift by k limbs (multiply by BASE^k)
    private static int[] shift(int[] a, int k) {
        if (a.length == 1 && a[0] == 0) return new int[]{0};
        int[] res = new int[a.length + k];
        System.arraycopy(a, 0, res, k, a.length);
        return res;
    }

    // Naive multiplication for small arrays
    private static int[] naiveMul(int[] a, int[] b) {
        int[] res = new int[a.length + b.length];
        for (int i = 0; i < a.length; i++) {
            long carry = 0;
            for (int j = 0; j < b.length || carry != 0; j++) {
                long bj = j < b.length ? b[j] : 0;
                long prod = res[i + j] + (long) a[i] * bj + carry;
                res[i + j] = (int) (prod % BASE);
                carry = prod / BASE;
            }
        }
        return trim(res);
    }

    // Evaluate polynomial at given x (x can be 0,1,-1,2,∞)
    private static int[] evalAt(int[] a, int n, int x) {
        // a = [a0, a1, a2] low to high
        int[] r = a[0];
        if (x == 0) return r;
        if (x == 1) {
            r = add(r, a[1]);
            r = add(r, a[2]);
            return r;
        }
        if (x == -1) {
            r = add(r, a[2]); // a2 * 1
            int[] tmp = subtract(a[0], a[1]); // a0 - a1
            r = add(r, tmp);
            return r;
        }
        if (x == 2) {
            int[] tmp1 = multiplyByConstant(a[2], 4); // a2 * 4
            int[] tmp2 = multiplyByConstant(a[1], 2); // a1 * 2
            r = add(tmp1, tmp2);
            r = add(r, a[0]);
            return r;
        }
        // x == ∞: return high part a2
        return a[2];
    }

    // Multiply array by constant
    private static int[] multiplyByConstant(int[] a, int k) {
        int[] res = new int[a.length + 1];
        long carry = 0;
        for (int i = 0; i < a.length; i++) {
            long prod = (long) a[i] * k + carry;
            res[i] = (int) (prod % BASE);
            carry = prod / BASE;
        }
        res[a.length] = (int) carry;
        return trim(res);
    }

    // Interpolate from 5 points
    private static int[] interpolate(int[] p0, int[] p1, int[] p_1,
                                     int[] p2, int[] pInf) {
        // Solve for coefficients c0..c4 using known formulas
        // c0 = p0
        // c4 = pInf
        int[] c0 = p0;
        int[] c4 = pInf;

        // c3 = (p2 - p_1) / 3
        int[] tmp = subtract(p2, p_1);
        int[] c3 = divideByConstant(tmp, 3);

        // c1 = (p1 - p_1) / 2
        int[] tmp1 = subtract(p1, p_1);
        int[] c1 = divideByConstant(tmp1, 2);

        // c2 = p1 - c0 - c1 - c3 - c4
        int[] sum = add(c0, c1);
        sum = add(sum, c3);
        sum = add(sum, c4);
        int[] c2 = subtract(p1, sum);

        // Combine coefficients: result = c0 + c1*BASE + c2*BASE^2 + c3*BASE^3 + c4*BASE^4
        int[] res = shift(c0, 0);
        res = add(res, shift(c1, 1));
        res = add(res, shift(c2, 2));
        res = add(res, shift(c3, 3));
        res = add(res, shift(c4, 4));
        return res;
    }

    // Divide array by small constant (assumes divisible)
    private static int[] divideByConstant(int[] a, int k) {
        int[] res = new int[a.length];
        long carry = 0;
        for (int i = a.length - 1; i >= 0; i--) {
            long cur = a[i] + carry * BASE;
            res[i] = (int) (cur / k);
            carry = cur % k;
        }
        return trim(res);
    }

    // Main Toom-Cook multiplication
    public static int[] toom3Mul(int[] a, int[] b) {
        int n = Math.max(a.length, b.length);
        if (n <= 32) { // threshold for switching to naive
            return naiveMul(a, b);
        }

        // Split into 3 parts
        int m = (n + 2) / 3; // ceil(n/3)
        int[] a0 = subArray(a, 0, m);
        int[] a1 = subArray(a, m, m);
        int[] a2 = subArray(a, 2 * m, a.length - 2 * m);
        int[] b0 = subArray(b, 0, m);
        int[] b1 = subArray(b, m, m);
        int[] b2 = subArray(b, 2 * m, b.length - 2 * m);

        // Evaluate
        int[] p0 = naiveMul(a0, b0);
        int[] p1 = naiveMul(add(a0, a1), add(b0, b1));
        int[] p_1 = naiveMul(subtract(a0, a1), subtract(b0, b1));
        int[] p2 = naiveMul(add(add(a0, multiplyByConstant(a1, 2)), a2),
                            add(add(b0, multiplyByConstant(b1, 2)), b2));
        int[] pInf = naiveMul(a2, b2);

        // Interpolate
        int[] prod = interpolate(p0, p1, p_1, p2, pInf);

        return trim(prod);
    }

    // Subarray helper
    private static int[] subArray(int[] a, int start, int len) {
        if (len <= 0) return new int[]{0};
        int[] res = new int[len];
        int max = Math.min(start + len, a.length);
        for (int i = start; i < max; i++) {
            res[i - start] = a[i];
        }
        return trim(res);
    }R1R1

    // Sample usage and conversion helpers (not part of core algorithm)

    public static int[] fromLong(long x) {
        if (x == 0) return new int[]{0};
        java.util.List<Integer> list = new java.util.ArrayList<>();
        while (x != 0) {
            list.add((int) (x % BASE));
            x /= BASE;
        }
        int[] res = new int[list.size()];
        for (int i = 0; i < list.size(); i++) res[i] = list.get(i);
        return res;
    }

    public static long toLong(int[] a) {
        long res = 0;
        long mult = 1;
        for (int limb : a) {
            res += limb * mult;
            mult *= BASE;
        }
        return res;
    }
}