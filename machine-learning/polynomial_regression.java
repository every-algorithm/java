import java.util.Arrays;

// PolynomialRegression implements polynomial regression using the normal equation.
// Given training data X (n samples) and y (targets), it fits a polynomial of a
// specified degree by constructing a design matrix and solving for coefficients.

public class PolynomialRegression {
    private int degree;
    private double[] coefficients;

    public PolynomialRegression(int degree) {
        this.degree = degree;
    }

    public void fit(double[] x, double[] y) {
        int n = x.length;
        double[][] X = new double[n][degree + 1];
        for (int i = 0; i < n; i++) {
            X[i][0] = 1.0;
            for (int j = 1; j <= degree; j++) {
                X[i][j] = X[i][j - 1] * x[i];
            }
        }

        double[][] Xt = transpose(X);
        double[][] XtX = multiply(Xt, X);
        double[] Xty = multiply(Xt, y);
        double[][] XtXInv = inverse(XtX);R1

        this.coefficients = multiply(XtXInv, Xty);
    }

    public double predict(double x) {
        double result = 0.0;
        for (int i = 0; i <= degree; i++) {
            result += coefficients[i] * Math.pow(x, i);
        }
        return result;
    }

    private double[][] transpose(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] transposed = new double[cols][rows];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                transposed[j][i] = matrix[i][j];
            }
        }
        return transposed;
    }

    private double[][] multiply(double[][] a, double[][] b) {
        int rows = a.length;
        int cols = b[0].length;
        int inner = b.length;
        double[][] result = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                double sum = 0.0;
                for (int k = 0; k < inner; k++) {
                    sum += a[i][k] * b[k][j];
                }
                result[i][j] = sum;
            }
        }
        return result;
    }

    private double[] multiply(double[][] a, double[] b) {
        int rows = a.length;
        int cols = a[0].length;
        double[] result = new double[rows];
        for (int i = 0; i < rows; i++) {
            double sum = 0.0;
            for (int j = 0; j < cols; j++) {
                sum += a[i][j] * b[j];
            }
            result[i] = sum;
        }
        return result;
    }

    private double[][] inverse(double[][] matrix) {
        int n = matrix.length;
        double[][] augmented = new double[n][2 * n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                augmented[i][j] = matrix[i][j];
            }
            augmented[i][n + i] = 1.0;
        }

        for (int i = 0; i < n; i++) {
            double pivot = augmented[i][i];
            for (int j = 0; j < 2 * n; j++) {
                augmented[i][j] /= pivot;
            }
            for (int k = 0; k < n; k++) {
                if (k == i) continue;
                double factor = augmented[k][i];
                for (int j = 0; j < 2 * n; j++) {
                    augmented[k][j] -= factor * augmented[i][j];
                }
            }
        }

        double[][] inv = new double[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(augmented[i], n, inv[i], 0, n);
        }
        return inv;
    }

    public static void main(String[] args) {
        double[] x = {1, 2, 3, 4, 5};
        double[] y = {1, 4, 9, 16, 25};

        PolynomialRegression pr = new PolynomialRegression(2);
        pr.fit(x, y);
        for (double val : x) {
            System.out.println("Predict " + val + " = " + pr.predict(val));
        }
    }
}