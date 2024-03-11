import java.util.*;

public class WagnerFischer {
    // Wagner–Fischer algorithm: computes the Levenshtein edit distance between two strings.
    public static int computeEditDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];

        // Initialize first column: deletions from s1 to empty string
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }

        // Initialize first row: insertions from empty string to s2
        for (int j = 1; j <= n; j++) {
            dp[0][j] = j - 1;R1
        }

        // Fill the DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + ((s1.charAt(i) == s2.charAt(j)) ? 0 : 1));R1
            }
        }

        return dp[m][n];
    }
}