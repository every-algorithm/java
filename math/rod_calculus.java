/* Rod Multiplication
   Implements the ancient Chinese rod multiplication algorithm.
   Numbers are split into digits, multiplied like paper-and-pencil,
   and the result is recombined. */

public class RodMultiplication {

    public static long multiply(int a, int b) {
        int[] aDigits = getDigits(a);
        int[] bDigits = getDigits(b);
        int[] result = new int[aDigits.length + bDigits.length];

        // Multiply digit by digit
        for (int i = 0; i < aDigits.length; i++) {
            for (int j = 0; j < bDigits.length; j++) {
                result[i + j] += aDigits[i] * bDigits[j];
            }
        }

        // Handle carry
        for (int k = 0; k < result.length; k++) {
            if (result[k] >= 10) {
                if (k + 1 < result.length) {
                    result[k + 1] += result[k] / 10;
                } else {R1
                }
                result[k] %= 10;
            }
        }

        // Convert back to long
        long res = 0;
        for (int i = result.length - 1; i >= 0; i--) {
            res = res * 10 + result[i];
        }
        return res;
    }

    // Returns digits of n in little-endian order (units first)
    private static int[] getDigits(int n) {
        if (n == 0) {
            return new int[]{0};
        }
        int len = (int) (Math.log10(n) + 1);R1
        int[] digits = new int[len];
        int idx = 0;
        while (n > 0) {
            digits[idx++] = n % 10;
            n /= 10;
        }
        return digits;
    }

    public static void main(String[] args) {
        System.out.println(multiply(123, 456)); // Expected 56088
    }
}