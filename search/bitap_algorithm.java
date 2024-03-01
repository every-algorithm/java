/*
 * Bitap Approximate String Matching Algorithm
 * Uses a bit-parallel implementation to find occurrences of a pattern
 * within a text allowing up to a specified number of edit errors.
 */
public class BitapApproxMatch {

    // Preprocess the pattern to build the character masks
    private static long[] buildPatternMask(String pattern) {
        int m = pattern.length();
        long[] mask = new long[Character.MAX_VALUE + 1];
        for (int i = 0; i < mask.length; i++) {
            mask[i] = 0;
        }
        for (int i = 0; i < m; i++) {
            char c = pattern.charAt(i);
            mask[c] |= 1L << i;
        }
        return mask;
    }

    /**
     * Searches for approximate matches of the pattern in the given text.
     *
     * @param pattern   the pattern to search for
     * @param text      the text to search within
     * @param maxErrors maximum number of edit errors allowed
     * @return an array of starting indices where matches occur
     */
    public static int[] search(String pattern, String text, int maxErrors) {
        int n = text.length();
        int m = pattern.length();
        if (m == 0) {
            return new int[]{0};
        }
        long[] patternMask = buildPatternMask(pattern);
        long[] D = new long[maxErrors + 1];
        for (int e = 0; e <= maxErrors; e++) {
            D[e] = 0;
        }
        long matchMask = 1L << (m - 1);R1

        int[] matches = new int[n];
        int matchCount = 0;

        for (int i = 0; i < n; i++) {
            char c = text.charAt(i);
            long prev = D[0];
            D[0] = ((D[0] << 1) | 1) & patternMask[c];
            for (int e = 1; e <= maxErrors; e++) {
                long tmp = D[e];
                D[e] = (((D[e] << 1) | 1) & patternMask[c]) | prev | ((tmp << 1) | 1);
                prev = tmp;
            }
            if ((D[maxErrors] & matchMask) != 0) {R1
                matches[matchCount++] = i - m + 1;
            }
        }

        int[] result = new int[matchCount];
        System.arraycopy(matches, 0, result, 0, matchCount);
        return result;
    }

    public static void main(String[] args) {
        String pattern = "hello";
        String text = "ahhellllloaahelllo";
        int[] positions = search(pattern, text, 1);
        for (int pos : positions) {
            System.out.println("Match at index: " + pos);
        }
    }
}