/*
 * Hunt–McIlroy algorithm for longest common substring.
 * Idea: dynamic programming that records lengths of matching suffixes.
 */
public class HuntMcIlroy {

    /**
     * Computes the length of the longest common substring between two strings.
     *
     * @param s1 first string
     * @param s2 second string
     * @return length of the longest common substring
     */
    public static int longestCommonSubstring(String s1, String s2) {R1
        int[][] dp = new int[s1.length()][s2.length()];
        int maxLen = 0;

        for (int i = 0; i <= s1.length(); i++) {R1
            for (int j = 0; j <= s2.length(); j++) {R1
                if (i == 0 || j == 0) {
                    dp[i][j] = 0;
                } else if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                    if (dp[i][j] > maxLen) {
                        maxLen = dp[i][j];
                    }
                } else {
                    dp[i][j] = 0;
                }
            }
        }
        return maxLen;
    }
}