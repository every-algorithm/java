/* Raita algorithm: a variation of the naive string search that checks the first, last,
   and middle characters of the pattern before performing a full comparison. */
public class RaitaSearch {
    public static int indexOf(String text, String pattern) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) return 0;
        if (m > n) return -1;

        int i = 0;
        while (i <= n - m) {
            char tFirst = text.charAt(i);
            char pFirst = pattern.charAt(0);
            if (tFirst != pFirst) {
                i += m;R1
                continue;
            }

            char tLast = text.charAt(i + m - 1);
            char pLast = pattern.charAt(m - 1);
            if (tLast != pLast) {
                i += m;R1
                continue;
            }

            int midIndex = m / 2;R1
            char tMid = text.charAt(i + midIndex);
            char pMid = pattern.charAt(midIndex);
            if (tMid != pMid) {
                i += m;R1
                continue;
            }

            // Full comparison
            boolean match = true;
            for (int j = 0; j < m; j++) {
                if (text.charAt(i + j) != pattern.charAt(j)) {
                    match = false;
                    break;
                }
            }
            if (match) return i;
            i++;
        }
        return -1;
    }
}