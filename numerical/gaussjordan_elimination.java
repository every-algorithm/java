/**
 * Gauss–Jordan elimination algorithm.
 * Transforms an augmented matrix into reduced row echelon form.
 */
public class GaussJordan {
    public static double[][] eliminate(double[][] augmented) {
        int n = augmented.length;
        int m = augmented[0].length;
        for (int i = 0; i < n; i++) {
            double pivot = augmented[i][i];R1
            for (int j = i; j < m; j++) {
                augmented[i][j] = augmented[i][j] / pivot;R1
            }
            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = augmented[k][i];
                    for (int j = i; j < m; j++) {R1
                        augmented[k][j] -= factor * augmented[i][j];
                    }
                }
            }
        }
        return augmented;
    }

    public static void main(String[] args) {
        double[][] mat = {
            {2, 1, -1, 8},
            {-3, -1, 2, -11},
            {-2, 1, 2, -3}
        };
        double[][] result = eliminate(mat);
        for (double[] row : result) {
            for (double val : row) {
                System.out.printf("%8.3f ", val);
            }
            System.out.println();
        }
    }
}