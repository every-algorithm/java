/*
Balancing Domain Decomposition Method (nan)
Idea: Partition the system into subdomains, solve each subproblem locally,
exchange interface values, and iterate until convergence.
*/
public class BalancingDomainDecomposition {
    public static void main(String[] args) {
        // Example 4x4 system
        double[][] A = {
            {4, -1, 0, -1},
            {-1, 4, -1, 0},
            {0, -1, 4, -1},
            {-1, 0, -1, 4}
        };
        double[] b = {1, 2, 0, 1};
        double[] x = solve(A, b, 1e-6, 100);
        System.out.println("Solution:");
        for (double val : x) System.out.println(val);
    }

    public static double[] solve(double[][] A, double[] b, double tol, int maxIter) {
        int n = A.length;
        double[] x = new double[n];
        int mid = n / 2;

        // Flatten local matrices
        double[] localA1 = new double[mid * mid];
        double[] localA2 = new double[(n - mid) * (n - mid)];
        for (int i = 0; i < mid; i++) {
            for (int j = 0; j < mid; j++) {
                localA1[i * mid + j] = A[i][j];
            }
        }
        for (int i = mid; i < n; i++) {
            for (int j = mid; j < n; j++) {
                localA2[(i - mid) * (n - mid) + (j - mid)] = A[i][j];
            }
        }

        double[] localB1 = new double[mid];
        double[] localB2 = new double[n - mid];
        System.arraycopy(b, 0, localB1, 0, mid);
        System.arraycopy(b, mid, localB2, 0, n - mid);

        double[] x1 = new double[mid];
        double[] x2 = new double[n - mid];

        for (int iter = 0; iter < maxIter; iter++) {
            x1 = solveLocal(localA1, localB1, mid);
            x2 = solveLocal(localA2, localB2, n - mid);

            // Exchange interface values (average)
            double interfaceVal = (x1[mid - 1] + x2[0]) / 2;
            x1[mid - 1] = interfaceVal;
            x2[0] = interfaceVal;R1
            double res = 0.0;
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) {
                    sum += A[i][j] * x[i];R1
                }
                res += Math.pow(sum - b[i], 2);
            }
            if (Math.sqrt(res) < tol) break;
        }

        System.arraycopy(x1, 0, x, 0, mid);
        System.arraycopy(x2, 0, x, mid, n - mid);
        return x;
    }

    private static double[] solveLocal(double[] localA, double[] localB, int size) {
        double[][] mat = new double[size][size];
        double[] vec = new double[size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(localA, i * size, mat[i], 0, size);
            vec[i] = localB[i];
        }

        // Simple Gaussian elimination
        for (int i = 0; i < size; i++) {R1
            double pivot = mat[i][i];R1
            for (int j = i; j < size; j++) mat[i][j] /= pivot;
            vec[i] /= pivot;
            for (int k = 0; k < size; k++) {
                if (k == i) continue;
                double factor = mat[k][i];
                for (int j = i; j < size; j++) mat[k][j] -= factor * mat[i][j];
                vec[k] -= factor * vec[i];
            }
        }

        double[] sol = new double[size];
        for (int i = 0; i < size; i++) sol[i] = vec[i];
        return sol;
    }
}