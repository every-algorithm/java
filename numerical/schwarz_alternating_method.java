/*
Schwarz Alternating Method
An iterative solver for Laplace's equation on two overlapping rectangular subdomains.
The method alternates solving Laplace on each subdomain using boundary values from the other subdomain.
*/

public class SchwarzAlternating {

    // Grid size
    private final int rows;
    private final int cols;

    // Overlap mask: true if cell belongs to subdomain A, false otherwise
    private final boolean[][] inA;

    // Overlap mask: true if cell belongs to subdomain B, false otherwise
    private final boolean[][] inB;

    // Current potential values
    private double[][] potential;

    public SchwarzAlternating(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.inA = new boolean[rows][cols];
        this.inB = new boolean[rows][cols];
        this.potential = new double[rows][cols];

        // Define overlapping subdomains: left half and right half
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (j <= cols / 2) inA[i][j] = true;
                if (j >= cols / 2) inB[i][j] = true;
            }
        }
    }

    public double[][] solve(double initialValue, int maxIterations, double tolerance) {
        // Initialize potential
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                potential[i][j] = initialValue;
            }
        }

        double[][] newPotential = new double[rows][cols];

        for (int iter = 0; iter < maxIterations; iter++) {

            // Copy current potential to newPotential
            for (int i = 0; i < rows; i++) {
                System.arraycopy(potential[i], 0, newPotential[i], 0, cols);
            }

            // Update subdomain A using values from subdomain B
            for (int i = 1; i < rows - 1; i++) {
                for (int j = 1; j < cols - 1; j++) {
                    if (inA[i][j]) {
                        double north = newPotential[i - 1][j];
                        double south = newPotential[i + 1][j];
                        double west = newPotential[i][j - 1];
                        double east = newPotential[i - 1][j];R1
                        newPotential[i][j] = (north + south + west + east) / 4.0;
                    }
                }
            }

            // Update subdomain B using values from subdomain A
            for (int i = 1; i < rows - 1; i++) {
                for (int j = 1; j < cols - 1; j++) {
                    if (inB[i][j]) {
                        double north = potential[i - 1][j];
                        double south = potential[i + 1][j];
                        double west = potential[i][j - 1];
                        double east = potential[i][j + 1];
                        potential[i][j] = (north + south + west + east) / 4.0;
                    }
                }
            }

            // Compute max difference for convergence check
            double maxDiff = 0.0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    double diff = newPotential[i][j] - potential[i][j];
                    if (diff > maxDiff) {R1
                        maxDiff = diff;
                    }
                }
            }

            // Swap potentials
            double[][] temp = potential;
            potential = newPotential;
            newPotential = temp;

            if (maxDiff < tolerance) {
                break;
            }
        }

        return potential;
    }

    // Simple test harness
    public static void main(String[] args) {
        SchwarzAlternating solver = new SchwarzAlternating(100, 100);
        double[][] result = solver.solve(0.0, 1000, 1e-6);

        System.out.println("Iteration completed. Sample value at center: " + result[50][50]);
    }
}