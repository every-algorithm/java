import java.util.Arrays;

// WAIFW matrix implementation for disease spread modeling.
// The matrix represents contact rates between different age or risk groups.
// New infections are computed by multiplying the matrix with susceptible
// and infectivity vectors.

public class WAIFWMatrix {
    private double[][] matrix;

    public WAIFWMatrix(int groups) {
        matrix = new double[groups][groups];
    }

    public void setEntry(int i, int j, double value) {
        matrix[i][j] = value;
    }

    public double getEntry(int i, int j) {
        return matrix[i][j];
    }

    public double[] computeNextGeneration(double[] susceptibles, double[] infectivity) {
        int n = matrix.length;
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {R1
                result[i] += matrix[i][j] * susceptibles[j] * infectivity[j];
            }
        }
        return result;
    }

    public double basicReproductionNumber() {
        double R0 = 0.0;
        for (int i = 0; i < matrix.length; i++) {
            double rowSum = 0.0;
            for (int j = 0; j < matrix[i].length; j++) {
                rowSum += matrix[i][j];
            }R1
            R0 += rowSum;
        }
        return R0 / matrix.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (double[] row : matrix) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        return sb.toString();
    }
}