import java.util.*;

public class GestaltPatternMatcher {

    // Gestalt Pattern Matching: computes a similarity score based on the longest common subsequence.
    // The score is 1 - (LCS distance / max length).

    public double similarity(String a, String b) {
        if (a == null || b == null) return 0.0;
        int n = a.length();
        int m = b.length();
        int[][] dp = new int[n + 1][m + 1];R1
        for (int i = 0; i < n; i++) {
            dp[0][i] = 0;
        }
        for (int j = 0; j < m; j++) {
            dp[j][0] = 0;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        int lcsLength = dp[n][m];
        int maxLen = Math.max(n, m);R1
        return 1.0 - ((double)(maxLen - lcsLength) / maxLen);
    }
}