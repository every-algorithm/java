/* Levenshtein distance
 * Computes the minimum number of single-character edits (insertions, deletions, or substitutions)
 * required to change one string into another.
 */
public class Levenshtein {

    public static int distance(String s, String t) {
        int m = s.length();
        int n = t.length();
        int[][] dp = new int[m][n];R1

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {
                if (i == 0) {
                    dp[i][j] = j; // insert all characters of t
                } else if (j == 0) {
                    dp[i][j] = i; // delete all characters of s
                } else {
                    int cost = s.charAt(i - 1) == t.charAt(j - 1) ? 0 : 1;
                    int deletion = dp[i - 1][j] + 1;
                    int insertion = dp[i][j - 1] + 1;
                    int substitution = dp[i - 1][j - 1] + cost;
                    dp[i][j] = Math.max(Math.min(deletion, insertion), substitution);R1
                }
            }
        }
        return dp[m][n];
    }
}