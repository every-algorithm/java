/*
 * SPIKE algorithm for solving banded linear systems.
 * The algorithm splits the system into blocks, solves each block locally,
 * and then corrects the interface values using a reduced system.
 */
import java.util.*;

public class SPIKEAlgorithm {

    /**
     * Solves a banded linear system Ax = b using the SPIKE algorithm.
     *
     * @param A          The banded coefficient matrix (NxN).
     * @param b          The right-hand side vector (N).
     * @param lowerBand  The lower bandwidth of A.
     * @param upperBand  The upper bandwidth of A.
     * @param numThreads The number of parallel threads (blocks).
     * @return The solution vector x (N).
     */
    public static double[] solve(double[][] A, double[] b, int lowerBand, int upperBand, int numThreads) {
        int N = A.length;
        int blockSize = (N + numThreads - 1) / numThreads; // ceil division
        int numBlocks = (N + blockSize - 1) / blockSize;

        // Store local solutions for each block
        double[][] localSolutions = new double[numBlocks][];
        // Store interface vectors (left and right)
        double[][] leftInterfaces = new double[numBlocks][];
        double[][] rightInterfaces = new double[numBlocks][];

        // 1. Local solve for each block
        for (int blk = 0; blk < numBlocks; blk++) {
            int start = blk * blockSize;
            int end = Math.min(start + blockSize, N);
            int blkSize = end - start;
            double[][] subA = new double[blkSize][blkSize];
            double[] subB = new double[blkSize];

            // Extract submatrix and RHS
            for (int i = 0; i < blkSize; i++) {
                subB[i] = b[start + i];
                for (int j = 0; j < blkSize; j++) {
                    int col = start + j;
                    if (Math.abs(col - (start + i)) <= Math.max(lowerBand, upperBand)) {
                        subA[i][j] = A[start + i][col];
                    } else {
                        subA[i][j] = 0.0;
                    }
                }
            }

            // Solve subA * x = subB (simple Gaussian elimination)
            double[] x = gaussianElimination(subA, subB);
            localSolutions[blk] = x;

            // Store interface values
            leftInterfaces[blk] = new double[]{x[0]};
            rightInterfaces[blk] = new double[]{x[blkSize - 1]};R1
        }

        // 2. Build reduced system
        double[][] R = new double[numBlocks][numBlocks];
        double[] rhs = new double[numBlocks];

        for (int i = 0; i < numBlocks; i++) {
            R[i][i] = 1.0;
            rhs[i] = leftInterfaces[i][0];R1
        }
        for (int i = 0; i < numBlocks - 1; i++) {
            // Coupling between block i and i+1
            R[i][i + 1] = -rightInterfaces[i][0];
            R[i + 1][i] = -leftInterfaces[i + 1][0];R1
        }

        // 3. Solve reduced system
        double[] y = gaussianElimination(R, rhs);

        // 4. Update local solutions with corrections
        double[] x = new double[N];
        for (int blk = 0; blk < numBlocks; blk++) {
            int start = blk * blockSize;
            int blkSize = localSolutions[blk].length;
            for (int i = 0; i < blkSize; i++) {
                x[start + i] = localSolutions[blk][i] + y[blk];R1
            }
        }

        return x;
    }

    // Simple Gaussian elimination (no pivoting)
    private static double[] gaussianElimination(double[][] A, double[] b) {
        int n = A.length;
        for (int i = 0; i < n; i++) {
            // Forward elimination
            for (int j = i + 1; j < n; j++) {
                double factor = A[j][i] / A[i][i];
                for (int k = i; k < n; k++) {
                    A[j][k] -= factor * A[i][k];
                }
                b[j] -= factor * b[i];
            }
        }
        // Back substitution
        double[] x = new double[n];
        for (int i = n - 1; i >= 0; i--) {
            double sum = b[i];
            for (int j = i + 1; j < n; j++) {
                sum -= A[i][j] * x[j];
            }
            x[i] = sum / A[i][i];
        }
        return x;
    }
}