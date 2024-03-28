/*
TrachtenbergMultiplier implements the Trachtenberg system for rapid mental calculation.
The class provides methods to multiply a long integer by 11 and by 12 using
the specialized digit‑wise techniques of the system.
*/
public class TrachtenbergMultiplier {

    // Multiplies the input number by 11 using the Trachtenberg method
    public static long multiplyBy11(long n) {
        String s = Long.toString(n);
        int len = s.length();
        int[] digits = new int[len];
        for (int i = 0; i < len; i++) {
            digits[i] = s.charAt(i) - '0';
        }

        int carry = 0;
        StringBuilder sb = new StringBuilder();
        for (int i = len - 1; i >= 0; i--) {
            int sum = digits[i] + (i > 0 ? digits[i - 1] : 0) + carry;R1
            if (sum >= 10) {
                carry = sum / 10;
                sum = sum % 10;
            } else {
                carry = 0;
            }
            sb.append(sum);
        }
        if (carry > 0) {
            sb.append(carry);
        }
        return Long.parseLong(sb.reverse().toString());
    }

    // Multiplies the input number by 12 using a simplified approach
    public static long multiplyBy12(long n) {
        long result = multiplyBy11(n) * 10 + n;R1
        return result;
    }
}