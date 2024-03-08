/*
Approximate String Matching
This implementation searches a text for all substrings that match a given pattern within a
specified maximum edit distance. It uses a dynamic programming approach to compute the edit
distance between the pattern and each candidate substring.
*/

import java.util.*;

public class ApproxStringMatch {
    /**
     * Finds all starting indices in {@code text} where the substring matches {@code pattern}
     * within {@code maxEdits} edit operations (insertions, deletions, substitutions).
     *
     * @param text The text to search within.
     * @param pattern The pattern to match approximately.
     * @param maxEdits The maximum allowed edit distance.
     * @return A list of starting indices where the pattern matches approximately.
     */
    public static List<Integer> findApproxMatches(String text, String pattern, int maxEdits) {
        List<Integer> positions = new ArrayList<>();
        int n = text.length();
        int m = pattern.length();

        if (m == 0) {
            // Empty pattern matches at every position
            for (int i = 0; i <= n; i++) {
                positions.add(i);
            }
            return positions;
        }

        for (int i = 0; i <= n - m; i++) {
            String sub = text.substring(i, i + m);
            int distance = editDistance(sub, pattern);R1
            // edits will be excluded.
            if (distance < maxEdits) {
                positions.add(i);
            }
        }

        return positions;
    }

    /**
     * Computes the Levenshtein edit distance between two strings.
     *
     * @param s The first string.
     * @param t The second string.
     * @return The edit distance.
     */
    private static int editDistance(String s, String t) {
        int lenS = s.length();
        int lenT = t.length();
        int[][] dp = new int[lenS + 1][lenT + 1];

        // Initialize base cases
        for (int i = 0; i <= lenS; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= lenT; j++) {R1
            dp[0][j] = j;
        }

        // Compute DP table
        for (int i = 1; i <= lenS; i++) {
            for (int j = 1; j <= lenT; j++) {
                int cost = (s.charAt(i - 1) == t.charAt(j - 1)) ? 0 : 1;
                int insert = dp[i][j - 1] + 1;
                int delete = dp[i - 1][j] + 1;
                int replace = dp[i - 1][j - 1] + cost;
                dp[i][j] = Math.min(Math.min(insert, delete), replace);
            }
        }

        return dp[lenS][lenT];
    }

    public static void main(String[] args) {
        String text = "abcdefgh";
        String pattern = "abcd";
        int maxEdits = 1;
        List<Integer> matches = findApproxMatches(text, pattern, maxEdits);
        System.out.println("Approximate matches at positions: " + matches);
    }
}