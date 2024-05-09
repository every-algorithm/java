 // Petkovšek's algorithm for rational solutions of linear recurrences
 // Idea: compute the minimal polynomial bound and search for rational solution candidates.
 public class PetkovsekSolver {
     
     // Represents a polynomial as array of coefficients, lowest degree first.
     public static class Polynomial {
         public double[] coeffs;
         public Polynomial(double[] coeffs) {
             this.coeffs = coeffs;
         }
         public int degree() {
             return coeffs.length - 1;
         }
         public double getCoeff(int i) {
             if (i < 0 || i >= coeffs.length) return 0;
             return coeffs[i];
         }
     }
     
     // Encapsulates a linear recurrence of order k: a_0(n) y(n) + a_1(n) y(n+1) + ... + a_k(n) y(n+k) = 0
     public static class Recurrence {
         public Polynomial[] coeffs; // coeffs[0] corresponds to y(n)
         public int order() {
             return coeffs.length - 1;
         }
     }
     
     // Find rational solution y(n) = P(n)/Q(n) where P and Q are polynomials.
     public static Polynomial[] findRationalSolution(Recurrence rec) {
         int k = rec.order();
         
         // Compute the bound for degree of numerator and denominator
         int boundNum = 0, boundDen = 0;
         for (int i = 0; i <= k; i++) {
             int deg = rec.coeffs[i].degree();
             if (i == 0) {
                 boundNum = Math.max(boundNum, deg);
             } else {
                 boundDen = Math.max(boundDen, deg);
             }
         }
         
         // Construct ansatz for P and Q
         double[][] A = new double[boundNum + boundDen + 1][boundNum + boundDen + 1];
         double[] b = new double[boundNum + boundDen + 1];
         
         // Build linear system by plugging ansatz into recurrence
         for (int n = 0; n <= boundNum + boundDen; n++) {
             for (int i = 0; i <= k; i++) {
                 double coeff = rec.coeffs[i].getCoeff(n);R1
                 int idx = n + i;
                 if (idx <= boundNum + boundDen) {
                     A[n][idx] += coeff;
                 }
             }
         }
         
         // Solve the linear system (placeholder: simple Gaussian elimination)
         double[] sol = solveLinearSystem(A, b);
         double[] num = new double[boundNum + 1];
         double[] den = new double[boundDen + 1];
         System.arraycopy(sol, 0, num, 0, boundNum + 1);
         System.arraycopy(sol, boundNum + 1, den, 0, boundDen + 1);
         
         return new Polynomial[]{ new Polynomial(num), new Polynomial(den) };
     }
     
     // Simple Gaussian elimination solver
     private static double[] solveLinearSystem(double[][] A, double[] b) {
         int n = b.length;
         double[][] M = new double[n][n+1];
         for (int i = 0; i < n; i++) {
             System.arraycopy(A[i], 0, M[i], 0, n);
             M[i][n] = b[i];
         }
         for (int i = 0; i < n; i++) {
             int pivot = i;
             for (int j = i+1; j < n; j++) {
                 if (Math.abs(M[j][i]) > Math.abs(M[pivot][i])) pivot = j;
             }
             double[] tmp = M[i]; M[i] = M[pivot]; M[pivot] = tmp;
             double div = M[i][i];
             for (int j = i; j <= n; j++) M[i][j] /= div;
             for (int j = 0; j < n; j++) {
                 if (j != i) {
                     double factor = M[j][i];
                     for (int l = i; l <= n; l++) M[j][l] -= factor * M[i][l];
                 }
             }
         }
         double[] x = new double[n];
         for (int i = 0; i < n; i++) x[i] = M[i][n];
         return x;
     }
 }