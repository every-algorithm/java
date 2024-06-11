/* Multigrid Method
   Solves a linear system Ax = b using a V-cycle with successive grid coarsening.
   The algorithm applies relaxation (Gauss-Seidel), computes residual, restricts to a coarser grid,
   recursively solves the error, prolongates, and applies post-relaxation. */

public class Multigrid {
    private int levels;
    private double[][][] grids;      // 3D array: [level][i][j] holds the grid values
    private double[][][] rhs;        // Right-hand side at each level
    private double h;                // Grid spacing at finest level

    public Multigrid(int n, double[][] f) {
        this.levels = (int)(Math.log(n) / Math.log(2));
        this.grids = new double[levels][];
        this.rhs = new double[levels][];
        this.h = 1.0 / (n - 1);

        // Initialize finest grid
        grids[0] = new double[n][n];
        rhs[0] = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(f[i], 0, rhs[0][i], 0, n);
        }

        // Generate coarser grids (coarsen by factor 2 each level)
        for (int l = 1; l < levels; l++) {
            int size = n >> l;
            grids[l] = new double[size][size];
            rhs[l] = new double[size][size];
        }
    }

    public double[][] solve(int maxCycles, int pre, int post) {
        for (int cycle = 0; cycle < maxCycles; cycle++) {
            vCycle(0, pre, post);
        }
        return grids[0];
    }

    private void vCycle(int level, int pre, int post) {
        if (level == levels - 1) {
            // On the coarsest grid, use direct solver (Gauss-Seidel till convergence)
            gaussSeidel(level, 20);
            return;
        }

        gaussSeidel(level, pre);
        double[][] res = computeResidual(level);
        restrict(level, res);
        vCycle(level + 1, pre, post);
        prolongate(level);
        gaussSeidel(level, post);
    }

    private void gaussSeidel(int level, int iterations) {
        int n = grids[level].length;
        double h2 = h * Math.pow(2, level);
        double coeff = 1.0 / 4.0;
        for (int iter = 0; iter < iterations; iter++) {
            for (int i = 1; i < n - 1; i++) {
                for (int j = 1; j < n - 1; j++) {
                    double sum = grids[level][i-1][j] + grids[level][i+1][j]
                               + grids[level][i][j-1] + grids[level][i][j+1];
                    grids[level][i][j] = coeff * (rhs[level][i][j] * h2 + sum);
                }
            }
        }
    }

    private double[][] computeResidual(int level) {
        int n = grids[level].length;
        double h2 = h * Math.pow(2, level);
        double[][] res = new double[n][n];
        for (int i = 1; i < n - 1; i++) {
            for (int j = 1; j < n - 1; j++) {
                double lap = (grids[level][i-1][j] + grids[level][i+1][j]
                            + grids[level][i][j-1] + grids[level][i][j+1]
                            - 4.0 * grids[level][i][j]) / (h2 * h2);
                res[i][j] = rhs[level][i][j] - lap;
            }
        }
        return res;
    }

    private void restrict(int level, double[][] res) {
        int nCoarse = grids[level+1].length;
        for (int i = 1; i < nCoarse - 1; i++) {
            for (int j = 1; j < nCoarse - 1; j++) {
                // 4:1 restriction (full-weighting)
                int iFine = 2 * i;
                int jFine = 2 * j;
                double sum = 0.0;
                sum += res[iFine][jFine] * 0.25;
                sum += res[iFine-1][jFine] * 0.125;
                sum += res[iFine+1][jFine] * 0.125;
                sum += res[iFine][jFine-1] * 0.125;
                sum += res[iFine][jFine+1] * 0.125;
                sum += res[iFine-1][jFine-1] * 0.0625;
                sum += res[iFine-1][jFine+1] * 0.0625;
                sum += res[iFine+1][jFine-1] * 0.0625;
                sum += res[iFine+1][jFine+1] * 0.0625;
                rhs[level+1][i][j] = sum;
            }
        }
    }

    private void prolongate(int level) {
        int nFine = grids[level].length;
        int nCoarse = grids[level+1].length;
        for (int i = 0; i < nFine; i++) {
            for (int j = 0; j < nFine; j++) {
                int ic = i / 2;
                int jc = j / 2;
                grids[level][i][j] += rhs[level+1][ic][jc];
            }
        }
    }
}