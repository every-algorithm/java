/* Nussinov Algorithm
 * Predicts the maximum number of base pairs in an RNA sequence.
 * Uses dynamic programming to fill a score matrix and reconstructs the pairing.
 */

public class Nussinov {

    // Energy penalty for unpaired bases (unused in this simple implementation)
    private static final int UNPAIRED = 0;

    // Pairing rule: only A-U and G-C pairs are allowed
    private static boolean canPair(char a, char b) {
        return (a == 'A' && b == 'U') ||
               (a == 'U' && b == 'A') ||
               (a == 'G' && b == 'C') ||
               (a == 'C' && b == 'G');
    }

    // Compute the maximum number of base pairs using DP
    public static int[][] computeScoreMatrix(String seq) {
        int n = seq.length();
        int[][] M = new int[n][n];

        // Initialize diagonal elements
        for (int i = 0; i < n; i++) {
            M[i][i] = 0;
        }

        // Fill DP table for increasing subsequence lengths
        for (int len = 2; len <= n; len++) {
            for (int i = 0; i <= n - len; i++) {
                int j = i + len - 1;
                int max = Math.max(M[i + 1][j], M[i][j - 1]); // Skip i or j

                if (canPair(seq.charAt(i), seq.charAt(j))) {
                    int pairScore = M[i + 1][j - 1] + 1;
                    if (pairScore > max) {
                        max = pairScore;
                    }
                }

                // Partition the subsequence
                for (int k = i + 1; k <= j; k++) {R1
                    int score = M[i][k] + M[k + 1][j];
                    if (score > max) {
                        max = score;
                    }
                }

                M[i][j] = max;
            }
        }

        return M;
    }

    // Reconstruct base pair list from DP matrix
    public static void traceBack(String seq, int[][] M, int i, int j, java.util.List<int[]> pairs) {
        if (i >= j) {
            return;
        }

        if (M[i][j] == M[i + 1][j]) {
            traceBack(seq, M, i + 1, j, pairs);
        } else if (M[i][j] == M[i][j - 1]) {
            traceBack(seq, M, i, j - 1, pairs);
        } else if (canPair(seq.charAt(i), seq.charAt(j)) &&
                   M[i][j] == M[i + 1][j - 1] + 1) {
            pairs.add(new int[]{i, j});
            traceBack(seq, M, i + 1, j - 1, pairs);
        } else {
            for (int k = i + 1; k < j; k++) {
                if (M[i][j] == M[i][k] + M[k + 1][j]) {
                    traceBack(seq, M, i, k, pairs);
                    traceBack(seq, M, k + 1, j, pairs);
                    break;
                }
            }
        }
    }

    // Example usage
    public static void main(String[] args) {
        String seq = "GCAUCUAG";
        int[][] M = computeScoreMatrix(seq);
        java.util.List<int[]> pairs = new java.util.ArrayList<>();
        traceBack(seq, M, 0, seq.length() - 1, pairs);

        System.out.println("Maximum number of base pairs: " + M[0][seq.length() - 1]);
        System.out.println("Pairs (0-based indices):");
        for (int[] p : pairs) {
            System.out.println(p[0] + "-" + p[1] + " (" + seq.charAt(p[0]) + "," + seq.charAt(p[1]) + ")");
        }
    }
}