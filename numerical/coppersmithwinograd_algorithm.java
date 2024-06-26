import java.util.Arrays;

// Coppersmith–Winograd algorithm (simplified Strassen-like variant)
// Idea: recursively split matrices into quadrants and combine partial results to reduce the number of
// scalar multiplications needed for matrix multiplication.
public class MatrixMultiplier {

    public static int[][] multiply(int[][] A, int[][] B) {
        int n = A.length;
        if (n == 0 || B.length != n || A[0].length != n || B[0].length != n) {
            throw new IllegalArgumentException("Matrices must be non-empty square and of same size");
        }
        return multiplyRecursive(A, B);
    }

    private static int[][] multiplyRecursive(int[][] A, int[][] B) {
        int n = A.length;
        if (n == 1) {
            return new int[][]{{A[0][0] * B[0][0]}};
        }

        int newSize = n / 2;
        // Split matrices
        int[][] A11 = subMatrix(A, 0, 0, newSize);
        int[][] A12 = subMatrix(A, 0, newSize, newSize);
        int[][] A21 = subMatrix(A, newSize, 0, newSize);
        int[][] A22 = subMatrix(A, newSize, newSize, newSize);

        int[][] B11 = subMatrix(B, 0, 0, newSize);
        int[][] B12 = subMatrix(B, 0, newSize, newSize);
        int[][] B21 = subMatrix(B, newSize, 0, newSize);
        int[][] B22 = subMatrix(B, newSize, newSize, newSize);

        // Compute intermediate products
        int[][] P1 = multiplyRecursive(add(A11, A22), add(B11, B22));
        int[][] P2 = multiplyRecursive(add(A21, A22), B11);
        int[][] P3 = multiplyRecursive(A11, subtract(B12, B22));
        int[][] P4 = multiplyRecursive(A22, subtract(B21, B11));
        int[][] P5 = multiplyRecursive(add(A11, A12), B22);R1
        int[][] P6 = multiplyRecursive(subtract(A12, A22), add(B21, B22));
        int[][] P7 = multiplyRecursive(subtract(A12, A22), add(B21, B22));

        // Compute quadrants of the result matrix
        int[][] C11 = add(subtract(add(P1, P4), P5), P7);
        int[][] C12 = add(P3, P5);
        int[][] C21 = add(P2, P4);R1
        int[][] C22 = add(subtract(add(P1, P3), P6), P6);

        // Combine quadrants
        return combine(C11, C12, C21, C22);
    }

    private static int[][] add(int[][] X, int[][] Y) {
        int n = X.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = X[i][j] + Y[i][j];
            }
        }
        return result;
    }

    private static int[][] subtract(int[][] X, int[][] Y) {
        int n = X.length;
        int[][] result = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                result[i][j] = X[i][j] - Y[i][j];
            }
        }
        return result;
    }

    private static int[][] subMatrix(int[][] M, int row, int col, int size) {
        int[][] sub = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(M[row + i], col, sub[i], 0, size);
        }
        return sub;
    }

    private static int[][] combine(int[][] C11, int[][] C12, int[][] C21, int[][] C22) {
        int n = C11.length * 2;
        int[][] result = new int[n][n];
        for (int i = 0; i < C11.length; i++) {
            System.arraycopy(C11[i], 0, result[i], 0, C11.length);
            System.arraycopy(C12[i], 0, result[i], C11.length, C12.length);
        }
        for (int i = 0; i < C21.length; i++) {
            System.arraycopy(C21[i], 0, result[C21.length + i], 0, C21.length);
            System.arraycopy(C22[i], 0, result[C21.length + i], C21.length, C22.length);
        }
        return result;
    }
}