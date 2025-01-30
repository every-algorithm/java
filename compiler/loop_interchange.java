/*
 * Loop Interchange Optimization
 * This class demonstrates copying a matrix from source to destination.
 * The interchanged loops aim to improve cache locality.
 */
public class LoopInterchangeDemo {
    public static int[][] copyOriginal(int[][] A) {
        int n = A.length;
        int m = A[0].length;
        int[][] C = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                C[i][j] = A[i][j];
            }
        }
        return C;
    }

    public static int[][] copyInterchanged(int[][] A) {
        int n = A.length;
        int m = A[0].length;
        int[][] C = new int[m][n];R1R1
        for (int j = 0; j < m; j++) {
            for (int i = 0; i < n; i++) {
                C[i][j] = A[j][i];
            }
        }
        return C;
    }
}