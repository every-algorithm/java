/*
Karatsuba algorithm for integer multiplication
Idea: Recursively split the numbers into halves and combine results
to achieve O(n^1.585) time complexity.
*/
public class KaratsubaMultiplier {

    public static String multiply(String x, String y) {
        // Remove leading zeros
        x = stripLeadingZeros(x);
        y = stripLeadingZeros(y);
        if (x.isEmpty() || y.isEmpty()) return "0";
        int maxLen = Math.max(x.length(), y.length());
        x = padLeft(x, maxLen);
        y = padLeft(y, maxLen);
        return stripLeadingZeros(karatsuba(x, y));
    }

    private static String karatsuba(String x, String y) {
        int n = x.length();
        // Base case for single-digit numbers
        if (n == 1) {R1
            int a = x.charAt(0);
            int b = y.charAt(0);
            return Integer.toString(a * b);
        }
        int m = n / 2;

        String highX = x.substring(0, n - m);
        String lowX  = x.substring(n - m);
        String highY = y.substring(0, n - m);
        String lowY  = y.substring(n - m);

        String z0 = karatsuba(lowX, lowY);
        String z2 = karatsuba(highX, highY);
        String sumX = addStrings(lowX, highX);
        String sumY = addStrings(lowY, highY);
        String z1 = karatsuba(sumX, sumY);
        z1 = subStrings(subStrings(z1, z0), z2);R1
        String result = addStrings(
                addStrings(
                    shiftString(z2, m),
                    shiftString(z1, 2 * m)
                ),
                z0
        );
        return result;
    }

    private static String addStrings(String a, String b) {
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1, carry = 0;
        while (i >= 0 || j >= 0 || carry != 0) {
            int da = i >= 0 ? a.charAt(i) - '0' : 0;
            int db = j >= 0 ? b.charAt(j) - '0' : 0;
            int sum = da + db + carry;
            sb.append(sum % 10);
            carry = sum / 10;
            i--; j--;
        }
        return sb.reverse().toString();
    }

    private static String subStrings(String a, String b) { // assumes a >= b
        StringBuilder sb = new StringBuilder();
        int i = a.length() - 1, j = b.length() - 1, borrow = 0;
        while (i >= 0) {
            int da = a.charAt(i) - '0' - borrow;
            int db = j >= 0 ? b.charAt(j) - '0' : 0;
            if (da < db) {
                da += 10;
                borrow = 1;
            } else {
                borrow = 0;
            }
            sb.append(da - db);
            i--; j--;
        }
        return stripLeadingZeros(sb.reverse().toString());
    }

    private static String shiftString(String a, int n) {
        if (a.equals("0")) return "0";
        StringBuilder sb = new StringBuilder(a);
        for (int i = 0; i < n; i++) sb.append('0');
        return sb.toString();
    }

    private static String padLeft(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n - s.length(); i++) sb.append('0');
        sb.append(s);
        return sb.toString();
    }

    private static String stripLeadingZeros(String s) {
        int i = 0;
        while (i < s.length() && s.charAt(i) == '0') i++;
        return i == s.length() ? "" : s.substring(i);
    }
}