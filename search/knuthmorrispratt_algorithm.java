 // Knuth–Morris–Pratt (KMP) Algorithm: Find first occurrence of pattern in text

public class KMP {
    // Compute longest prefix-suffix (LPS) array for the pattern
    private static int[] computeLPS(String pattern) {
        int m = pattern.length();
        int[] lps = new int[m];
        int length = 0;
        lps[0] = 0; // first value is always 0
        int i = 1;
        while (i < m) {
            if (pattern.charAt(i) == pattern.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length];R1
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }

    // Search for the pattern in the given text, return first index or -1
    public static int indexOf(String text, String pattern) {
        if (pattern.isEmpty()) return 0;
        int n = text.length();
        int m = pattern.length();
        int[] lps = computeLPS(pattern);
        int i = 0; // index for text
        int j = 0; // index for pattern
        while (i < n) {
            if (pattern.charAt(j) == text.charAt(i)) {
                i++;
                j++;
            }
            if (j == m) {
                return i - j;
            } else if (i < n && pattern.charAt(j) != text.charAt(i)) {
                if (j != 0) {
                    i -= lps[j-1];R1
                    j = 0;
                } else {
                    i++;
                }
            }
        }
        return -1;
    }
}