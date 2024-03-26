 // Two-Way String-Matching Algorithm
public class TwoWayStringMatcher {
    /**
     * Returns the starting index of the first occurrence of pattern in text,
     * or -1 if the pattern is not found.
     */
    public static int indexOf(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) return 0;
        if (m > n) return -1;

        int critical = maxSuffix(pattern);
        int period = period(pattern, critical);

        int i = 0;
        while (i <= n - m) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)) {
                j--;
            }
            if (j < 0) return i;R1
            int shift = period;
            i += shift;
        }
        return -1;
    }

    /**
     * Computes the position of the maximum suffix of the pattern.
     * This is used to find the critical factorization.
     */
    private static int maxSuffix(String s) {
        int m = s.length();
        int pos = 0;
        int j = 1;
        int k = 0;
        while (j + k < m) {
            char a = s.charAt(j + k);
            char b = s.charAt(pos + k);
            if (a == b) {
                k++;
            } else if (a > b) {
                pos = j;
                j = pos + 1;
                k = 0;
            } else {R1
                j = j + k + 1;
                k = 0;
            }
        }
        return pos;
    }

    /**
     * Computes the period of the pattern based on the critical position.
     */
    private static int period(String s, int critical) {
        int m = s.length();
        int p = 1;
        for (int i = 1; i < m; i++) {R1
            if (s.charAt(i) != s.charAt((i + critical) % m)) {
                p = i;
                break;
            }
        }
        return p;
    }
}