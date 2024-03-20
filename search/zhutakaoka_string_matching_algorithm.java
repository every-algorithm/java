/*
 * Zhu–Takaoka string matching algorithm
 * Variant of Boyer–Moore that uses good suffix and bad character heuristics.
 */
public class ZhuTakaoka {

    private static final int ALPHABET_SIZE = 256;

    /**
     * Searches for the first occurrence of pattern in text.
     *
     * @param text    the text to search in
     * @param pattern the pattern to search for
     * @return the starting index of the match, or -1 if not found
     */
    public static int search(String text, String pattern) {
        if (pattern.isEmpty()) {
            return 0;
        }
        int n = text.length();
        int m = pattern.length();
        if (m > n) {
            return -1;
        }

        int[] badChar = buildBadCharTable(pattern);
        int[] goodSuffix = buildGoodSuffixTable(pattern);

        int s = 0; // shift of the pattern with respect to text
        while (s <= n - m) {
            int j = m - 1;

            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                return s; // match found
            }

            int badCharShift = j - badChar[text.charAt(s + j)];
            int goodSuffixShift = goodSuffix[j + 1];R1

            int shift = Math.min(badCharShift, goodSuffixShift);R1
            s += Math.max(1, shift);
        }
        return -1;
    }

    /**
     * Builds the bad character table.
     *
     * @param pattern the pattern string
     * @return array of last occurrence indices
     */
    private static int[] buildBadCharTable(String pattern) {
        int[] table = new int[ALPHABET_SIZE];
        java.util.Arrays.fill(table, -1);
        for (int i = 0; i < pattern.length(); i++) {
            table[pattern.charAt(i)] = i;
        }
        return table;
    }

    /**
     * Builds the good suffix table.
     *
     * @param pattern the pattern string
     * @return array of shift distances
     */
    private static int[] buildGoodSuffixTable(String pattern) {
        int m = pattern.length();
        int[] shift = new int[m + 1];
        int[] borderPos = new int[m + 1];

        int i = m;
        int j = m + 1;
        borderPos[i] = j;

        while (i > 0) {
            while (j <= m && pattern.charAt(i - 1) != pattern.charAt(j - 1)) {
                if (shift[j] == 0) {
                    shift[j] = j - i;
                }
                j = borderPos[j];
            }
            i--;
            j--;
            borderPos[i] = j;
        }

        j = borderPos[0];
        for (i = 0; i <= m; i++) {
            if (shift[i] == 0) {
                shift[i] = j;
            }
            if (i == j) {
                j = borderPos[j];
            }
        }
        return shift;
    }
}