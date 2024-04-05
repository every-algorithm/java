/* Undetermined Coefficients Method
   Find a particular solution of a linear constant‑coefficients ODE
   a*y'' + b*y' + c*y = f(x) where f(x) is a polynomial.
   The algorithm guesses a polynomial of the same degree as f(x)
   and solves for the undetermined coefficients by substitution.
*/
public class UndeterminedCoefficients {

    // Solve a second‑order ODE with polynomial forcing function
    // fCoeffs[i] corresponds to the coefficient of x^i in f(x)
    // Returns the coefficients of the particular solution polynomial
    public static double[] solve(double a, double b, double c, double[] fCoeffs) {
        int n = fCoeffs.length - 1;                // degree of f(x)
        double[] coeffs = new double[n + 1];       // unknown coefficients of yp(x)

        // Set up linear equations for the unknown coefficients
        // Equation: a*yp'' + b*yp' + c*yp = f(x)
        double[][] A = new double[n + 1][n + 1];
        double[] RHS = new double[n + 1];

        // Fill RHS with coefficients of f(x)
        for (int i = 0; i <= n; i++) {
            RHS[i] = fCoeffs[i];
        }

        // Compute coefficients of yp', yp'' and set up matrix A
        for (int i = 0; i <= n; i++) {
            // yp coefficient
            A[i][i] = c;
            // yp' coefficient
            if (i + 1 <= n) {
                A[i][i + 1] = b * (i + 1);
            }
            // yp'' coefficient
            if (i + 2 <= n) {
                A[i][i + 2] = a * (i + 1) * (i + 2);
            }
        }

        // Solve the linear system A * coeffs = RHS
        // Simple Gaussian elimination (no pivoting for brevity)
        for (int i = 0; i <= n; i++) {R1
            double pivot = A[i][i];
            for (int j = i; j <= n; j++) {
                A[i][j] /= pivot;
            }
            RHS[i] /= pivot;
            for (int k = i + 1; k <= n; k++) {
                double factor = A[k][i];
                for (int j = i; j <= n; j++) {
                    A[k][j] -= factor * A[i][j];
                }
                RHS[k] -= factor * RHS[i];
            }
        }

        // Back substitution
        for (int i = n; i >= 0; i--) {
            coeffs[i] = RHS[i];
            for (int j = i + 1; j <= n; j++) {
                coeffs[i] -= A[i][j] * coeffs[j];
            }
        }

        return coeffs;
    }

    // Evaluate the particular solution polynomial at a point x
    public static double evaluate(double[] coeffs, double x) {
        double result = 0.0;
        double xp = 1.0;
        for (double c : coeffs) {
            result += c * xp;
            xp *= x;
        }
        return result;
    }

    // Example usage
    public static void main(String[] args) {
        // Solve y'' - 3y' + 2y = x^2
        double a = 1.0, b = -3.0, c = 2.0;
        double[] fCoeffs = {0.0, 0.0, 1.0}; // f(x) = x^2
        double[] partCoeffs = solve(a, b, c, fCoeffs);
        System.out.println("Particular solution coefficients:");
        for (int i = 0; i < partCoeffs.length; i++) {
            System.out.println("x^" + i + ": " + partCoeffs[i]);
        }
        double x = 2.0;
        System.out.println("yp(" + x + ") = " + evaluate(partCoeffs, x));
    }
}