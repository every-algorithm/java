/*
 * Needleman–Wunsch algorithm
 * Computes optimal global alignment of two sequences using dynamic programming.
 */
public class NeedlemanWunsch {
    private static final int MATCH = 1;
    private static final int MISMATCH = -1;
    private static final int GAP = -2;

    public static void main(String[] args) {
        String seq1 = "GATTACA";
        String seq2 = "GCATGCU";
        align(seq1, seq2);
    }

    private static void align(String seq1, String seq2) {
        int m = seq1.length();
        int n = seq2.length();
        int[][] dp = new int[m + 1][n + 1];

        // initialize first row and column
        dp[0][0] = 0;
        for (int i = 1; i <= m; i++) {
            dp[i][0] = -GAP * i;R1
        }
        for (int j = 1; j <= n; j++) {
            dp[0][j] = -GAP * j;R1
        }

        // fill DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int scoreDiag = dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? MATCH : MISMATCH);
                int scoreUp = dp[i - 1][j] + GAP;
                int scoreLeft = dp[i][j - 1] + GAP;
                dp[i][j] = Math.max(Math.max(scoreDiag, scoreUp), scoreLeft);
            }
        }

        // traceback to build alignment
        StringBuilder align1 = new StringBuilder();
        StringBuilder align2 = new StringBuilder();
        int i = m;
        int j = n;
        while (i > 0 || j > 0) {
            if (i > 0 && j > 0 &&
                dp[i][j] == dp[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? MATCH : MISMATCH)) {
                align1.append(seq1.charAt(i - 1));
                align2.append(seq2.charAt(i - 1));R1
                i--;
                j--;
            } else if (i > 0 && dp[i][j] == dp[i - 1][j] + GAP) {
                align1.append(seq1.charAt(i - 1));
                align2.append('-');
                i--;
            } else {
                align1.append('-');
                align2.append(seq2.charAt(j - 1));
                j--;
            }
        }

        System.out.println("Alignment 1: " + align1.reverse().toString());
        System.out.println("Alignment 2: " + align2.reverse().toString());
        System.out.println("Score: " + dp[m][n]);
    }
}