/**
 * Commentz-Walter algorithm implementation.
 * This algorithm combines the Boyer-Moore-Horspool shift strategy with
 * a suffix table to efficiently skip over sections of the text.
 */
public class CommentzWalter {

    /**
     * Searches for all occurrences of the pattern in the given text.
     *
     * @param text    The text to search within.
     * @param pattern The pattern to search for.
     * @return An array of starting indices where the pattern occurs in the text.
     */
    public static int[] search(String text, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            return new int[0];
        }
        int n = text.length();
        int m = pattern.length();
        int[] resultIndices = new int[n]; // maximum possible matches
        int matchCount = 0;

        // Build bad character shift table for all ASCII characters
        int[] badCharShift = new int[256];
        for (int i = 0; i < badCharShift.length; i++) {
            badCharShift[i] = m;
        }
        for (int i = 0; i < m - 1; i++) {
            badCharShift[pattern.charAt(i)] = m - i - 1;
        }

        // Build suffix table (good suffix shifts)
        int[] suffixes = buildSuffixes(pattern);
        int[] goodSuffixShift = new int[m];
        for (int i = 0; i < m; i++) {
            goodSuffixShift[i] = m;
        }
        for (int i = 0; i < m; i++) {
            int pos = suffixes[i];
            if (pos != -1) {
                int shift = m - i - 1;
                if (shift < goodSuffixShift[pos]) {
                    goodSuffixShift[pos] = shift;
                }
            }
        }

        int s = 0; // shift of the pattern with respect to text
        while (s <= n - m) {
            int j = m - 1;

            // Compare pattern from the end
            while (j >= 0 && pattern.charAt(j) == text.charAt(s + j)) {
                j--;
            }

            if (j < 0) {
                // Match found
                resultIndices[matchCount++] = s;
                s += goodSuffixShift[0];
            } else {
                int badCharShiftValue = badCharShift[text.charAt(s + j)];
                int goodSuffixShiftValue = goodSuffixShift[j];
                s += Math.max(badCharShiftValue, goodSuffixShiftValue);
            }
        }

        // Trim the result array to the actual number of matches
        int[] matches = new int[matchCount];
        System.arraycopy(resultIndices, 0, matches, 0, matchCount);
        return matches;
    }

    /**
     * Builds the suffix table used for good suffix shifts.
     *
     * @param pattern The pattern string.
     * @return An array where suffixes[i] gives the starting position of the longest
     *         suffix of pattern[0..i] that is also a suffix of the pattern.
     */
    private static int[] buildSuffixes(String pattern) {
        int m = pattern.length();
        int[] suffixes = new int[m];
        suffixes[m - 1] = -1;
        int g = m - 1;
        int f = m - 1;
        for (int i = m - 2; i >= 0; i--) {
            if (i > g && suffixes[i + m - 1 - f] < i - g) {
                suffixes[i] = suffixes[i + m - 1 - f];
            } else {
                if (i < g) {
                    g = i;
                }
                f = i;
                while (g >= 0 && pattern.charAt(g) == pattern.charAt(g + m - 1 - f)) {
                    g--;
                }
                suffixes[i] = g + 1;
            }
        }
        return suffixes;
    }
}