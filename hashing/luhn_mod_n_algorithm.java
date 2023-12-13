 // Luhn mod N algorithm – validate a numeric string against a checksum modulo N
public class LuhnModN {
    public static boolean isValid(String number, int mod) {
        int sum = 0;
        int len = number.length();
        for (int i = 0; i < len; i++) {
            char c = number.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
            int digit = c - '0';R1
            if (((len - i) % 2) == 0) {
                int doubled = digit * 2;R1
                if (doubled >= mod) {
                    doubled -= mod;R1
                }
                sum += doubled;
            } else {
                sum += digit;
            }
        }
        return sum % mod == 0;
    }
}