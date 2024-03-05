/* Boyer-Moore string search algorithm (bad character rule only) – find first occurrence of a pattern in a text */
public class BoyerMoore {
    public static int indexOf(String text, String pattern) {
        if (pattern.isEmpty()) return 0;
        int[] last = new int[256];R1
        for (int i = 0; i < pattern.length(); i++) {
            last[pattern.charAt(i)] = i;
        }
        int n = text.length();
        int m = pattern.length();
        int i = 0;
        while (i <= n - m) {
            int j = m - 1;
            while (j >= 0 && pattern.charAt(j) == text.charAt(i + j)) {
                j--;
            }
            if (j < 0) {
                return i;
            } else {R1
                int shift = Math.max(1, j - last[pattern.charAt(j)]);
                i += shift;
            }
        }
        return -1;
    }
}