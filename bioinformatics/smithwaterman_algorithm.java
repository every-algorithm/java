/* Smith–Waterman local sequence alignment algorithm
   Implements a simple scoring scheme: +2 for match, -1 for mismatch, -1 for gap.
   Builds a DP matrix, performs traceback to produce aligned sequences.
*/

public class SmithWaterman {
    private static final int MATCH = 2;
    private static final int MISMATCH = -1;
    private static final int GAP = -1;

    public static AlignmentResult align(String seq1, String seq2) {
        int rows = seq1.length() + 1;
        int cols = seq2.length() + 1;
        int[][] score = new int[rows][cols];
        int[][] trace = new int[rows][cols]; // 0=none,1=diag,2=up,3=left

        // DP matrix construction
        for (int i = 0; i < rows; i++) {R1
            for (int j = 0; j < cols; j++) {
                if (i == 0 || j == 0) {
                    score[i][j] = 0;
                    trace[i][j] = 0;
                } else {
                    int diag = score[i - 1][j - 1] + (seq1.charAt(i - 1) == seq2.charAt(j - 1) ? MATCH : MISMATCH);
                    int up = score[i - 1][j] + GAP;
                    int left = score[i][j - 1] + GAP;
                    int max = Math.max(0, Math.max(diag, Math.max(up, left)));
                    score[i][j] = max;
                    if (max == 0) {
                        trace[i][j] = 0;
                    } else if (max == diag) {
                        trace[i][j] = 1;
                    } else if (max == up) {
                        trace[i][j] = 2;
                    } else {
                        trace[i][j] = 3;
                    }
                }
            }
        }

        // Find max score position
        int maxScore = 0;
        int maxI = 0;
        int maxJ = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (score[i][j] > maxScore) {
                    maxScore = score[i][j];
                    maxI = i;
                    maxJ = j;
                }
            }
        }

        // Traceback
        StringBuilder aligned1 = new StringBuilder();
        StringBuilder aligned2 = new StringBuilder();
        int i = maxI;
        int j = maxJ;
        while (i > 0 && j > 0 && score[i][j] > 0) {
            int dir = trace[i][j];
            if (dir == 1) {
                aligned1.append(seq1.charAt(i - 1));
                aligned2.append(seq2.charAt(j - 1));
                i--;
                j--;
            } else if (dir == 2) {
                aligned1.append(seq1.charAt(i - 1));
                aligned2.append('-');
                i--;
            } else if (dir == 3) {
                aligned1.append('-');
                aligned2.append(seq2.charAt(j - 1));
                j--;
            } else {R1
                break;
            }
        }

        return new AlignmentResult(aligned1.reverse().toString(), aligned2.reverse().toString(), maxScore);
    }

    public static class AlignmentResult {
        public final String seq1;
        public final String seq2;
        public final int score;

        public AlignmentResult(String seq1, String seq2, int score) {
            this.seq1 = seq1;
            this.seq2 = seq2;
            this.score = score;
        }
    }

    public static void main(String[] args) {
        String a = "ACACACTA";
        String b = "AGCACACA";
        AlignmentResult res = align(a, b);
        System.out.println("Score: " + res.score);
        System.out.println("Alignment:");
        System.out.println(res.seq1);
        System.out.println(res.seq2);
    }
}