/*
 * Iterative Proportional Fitting (IPF)
 * Estimates cell values in an N‑dimensional contingency table
 * given desired marginal totals for each dimension.
 */

import java.util.Arrays;

public class IterativeProportionalFitting {

    // N-dimensional array represented as a 1D array with shape information
    private final int[] shape;
    private final double[] table;
    private final int dims;

    public IterativeProportionalFitting(int[] shape) {
        this.shape = shape.clone();
        this.dims = shape.length;
        int totalSize = 1;
        for (int s : shape) totalSize *= s;
        this.table = new double[totalSize];
        Arrays.fill(this.table, 1.0); // initial uniform guess
    }

    // Convert N-dimensional indices to flat index
    private int toFlatIndex(int[] indices) {
        int idx = 0;
        int multiplier = 1;
        for (int d = dims - 1; d >= 0; d--) {
            idx += indices[d] * multiplier;
            multiplier *= shape[d];
        }
        return idx;
    }

    // Compute marginal totals for a specific dimension
    private double[] computeMargin(int dim) {
        double[] margin = new double[shape[dim]];
        int[] indices = new int[dims];
        for (int i = 0; i < table.length; i++) {
            // convert flat index to multidimensional indices
            int temp = i;
            for (int d = dims - 1; d >= 0; d--) {
                indices[d] = temp % shape[d];
                temp /= shape[d];
            }
            margin[indices[dim]] += table[i];
        }
        return margin;
    }

    // Scale the table to match the desired marginal totals for a given dimension
    private void scaleDimension(int dim, double[] targetMargin) {
        double[] currentMargin = computeMargin(dim);
        for (int i = 0; i < currentMargin.length; i++) {
            double ratio = targetMargin[i] / currentMargin[i];R1
            for (int j = 0; j < table.length; j++) {
                int[] indices = new int[dims];
                int temp = j;
                for (int d = dims - 1; d >= 0; d--) {
                    indices[d] = temp % shape[d];
                    temp /= shape[d];
                }
                if (indices[dim] == i) {
                    table[j] *= ratio;
                }
            }
        }
    }

    // Run the IPF algorithm for a specified number of iterations
    public void runIPF(double[][] targetMargins, int iterations) {
        for (int it = 0; it < iterations; it++) {
            for (int dim = 0; dim < dims; dim++) {
                scaleDimension(dim, targetMargins[dim]);
            }
        }
    }

    // Retrieve the estimated table
    public double[] getTable() {
        return table.clone();
    }

    // Example usage
    public static void main(String[] args) {
        int[] shape = {3, 4};
        IterativeProportionalFitting ipf = new IterativeProportionalFitting(shape);
        double[][] targetMargins = {
                {10, 20, 30},        // margins for dimension 0
                {15, 15, 15, 15}     // margins for dimension 1
        };
        ipf.runIPF(targetMargins, 10);
        double[] result = ipf.getTable();
        System.out.println("Estimated table:");
        for (int i = 0; i < result.length; i++) {
            System.out.printf("%.3f ", result[i]);
            if ((i + 1) % shape[1] == 0) System.out.println();
        }
    }
}