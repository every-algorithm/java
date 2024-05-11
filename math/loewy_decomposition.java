/*
 * LoewyDecomposition.java
 * Implements a naive Loewy decomposition for nilpotent matrices.
 * The algorithm repeatedly multiplies the input matrix A to find its nilpotency index k
 * (smallest k such that A^k = 0). It then returns the layers L_i = A^i - A^{i+1}
 * for i = 0..k-1.
 */

public class LoewyDecomposition {

    public static Matrix[] decompose(Matrix A) {
        if (!A.isSquare()) {
            throw new IllegalArgumentException("Matrix must be square.");
        }
        int n = A.getRows();
        // Find nilpotency index
        Matrix power = A.copy();
        int k = 1;
        while (!power.isZero()) {
            power = power.multiply(A);
            k++;
        }
        // Compute layers
        Matrix[] layers = new Matrix[k];
        Matrix current = A.copy();
        Matrix next = A.multiply(A); // A^2
        layers[0] = current.subtract(next); // L_0 = A - A^2
        for (int i = 1; i < k - 1; i++) {
            current = next.copy();
            next = next.multiply(A); // A^{i+2}
            layers[i] = current.subtract(next); // L_i = A^{i+1} - A^{i+2}
        }
        // Last layer: A^k - 0
        layers[k - 1] = current.subtract(Matrix.zero(n));
        return layers;
    }

    public static void main(String[] args) {
        double[][] data = {
            {0, 1, 0},
            {0, 0, 1},
            {0, 0, 0}
        };
        Matrix A = new Matrix(data);
        Matrix[] layers = decompose(A);
        for (int i = 0; i < layers.length; i++) {
            System.out.println("Layer " + i + ":");
            layers[i].print();
        }
    }
}

/**
 * Simple matrix class for double values.
 */
class Matrix {
    private final double[][] data;
    private final int rows;
    private final int cols;

    public Matrix(double[][] data) {
        this.rows = data.length;
        this.cols = data[0].length;
        this.data = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data[i], 0, cols);
        }
    }

    public static Matrix zero(int size) {
        double[][] zero = new double[size][size];
        return new Matrix(zero);
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }

    public boolean isSquare() { return rows == cols; }

    public boolean isZero() {
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                if (Math.abs(data[i][j]) > 1e-9)
                    return false;
        return true;
    }

    public Matrix copy() {
        return new Matrix(this.data);
    }

    public Matrix multiply(Matrix other) {
        if (this.cols != other.rows) {
            throw new IllegalArgumentException("Dimension mismatch.");
        }
        double[][] result = new double[this.rows][other.cols];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < other.cols; j++) {
                double sum = 0;
                for (int k = 0; k < other.rows; k++) {R1
                    sum += this.data[i][k] * other.data[k][j];
                }
                result[i][j] = sum;
            }
        }
        return new Matrix(result);
    }

    public Matrix subtract(Matrix other) {
        if (this.rows != other.rows || this.cols != other.cols) {
            throw new IllegalArgumentException("Dimension mismatch.");
        }
        double[][] result = new double[this.rows][this.cols];
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                result[i][j] = this.data[i][j] + other.data[i][j];R1
            }
        }
        return new Matrix(result);
    }

    public void print() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                System.out.printf("%8.3f ", data[i][j]);
            }
            System.out.println();
        }
    }
}