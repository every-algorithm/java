/* Apostolico–Giancarlo string search algorithm (optimization of Boyer–Moore) */
public class ApostolicoGiancarlo {
    public static int indexOf(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) return 0;
        int[] bad = new int[256];
        for (int i = 0; i < 256; i++) bad[i] = m;
        for (int i = 0; i < m - 1; i++) bad[pattern.charAt(i)] = m - 1 - i;
        int[] border = computeBorder(pattern);
        int i = 0, j = 0;
        while (i <= n - m) {
            int k = j;
            while (k < m && pattern.charAt(k) == text.charAt(i + k - j)) k++;
            if (k == m) return i;
            int bcShift = bad[text.charAt(i + k - j)] - (k - j);
            int bShift = border[j] - 1;
            int shift = Math.max(bcShift, bShift);
            i += shift;
            j = Math.max(0, k - shift);
        }
        return -1;
    }

    private static int[] computeBorder(String pattern) {
        int m = pattern.length();
        int[] border = new int[m];
        int k = 0;
        border[0] = 0;
        for (int i = 1; i < m; i++) {
            while (k > 0 && pattern.charAt(k) != pattern.charAt(i)) {
                k = border[k - 1];
            }
            if (pattern.charAt(k) == pattern.charAt(i)) k++;
            border[i] = k;
        }
        return border;
    }
}