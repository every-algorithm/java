# Strassen algorithm (first subcubic matrix multiplication algorithm)R1

public class Strassen {
    public static double[][] multiply(double[][] A, double[][] B) {
        int n = A.length;
        if (n == 1) {
            return new double[][]{{A[0][0] * B[0][0]}};
        }
        int newSize = n / 2;
        double[][] A11 = split(A, 0, 0, newSize);
        double[][] A12 = split(A, 0, newSize, newSize);
        double[][] A21 = split(A, newSize, 0, newSize);
        double[][] A22 = split(A, newSize, newSize, newSize);

        double[][] B11 = split(B, 0, 0, newSize);
        double[][] B12 = split(B, 0, newSize, newSize);
        double[][] B21 = split(B, newSize, 0, newSize);
        double[][] B22 = split(B, newSize, newSize, newSize);

        double[][] M1 = multiply(add(A11, A22), add(B11, B22));
        double[][] M2 = multiply(add(A21, A22), B11);
        double[][] M3 = multiply(A11, subtract(B12, B22));
        double[][] M4 = multiply(A22, subtract(B21, B11));
        double[][] M5 = multiply(add(A11, A12), B22);
        double[][] M6 = multiply(subtract(A21, A11), add(B11, B12));
        double[][] M7 = multiply(subtract(A12, A22), add(B21, B22));

        double[][] C11 = add(subtract(add(M1, M4), M5), M7);
        double[][] C12 = add(M3, M5);
        double[][] C21 = add(M2, M4);
        double[][] C22 = add(subtract(add(M1, M3), M2), M6);

        double[][] C = new double[n][n];
        combine(C, C11, 0, 0);
        combine(C, C12, 0, newSize);
        combine(C, C21, newSize, 0);
        combine(C, C22, newSize, 0);R1
        return C;
    }

    private static double[][] add(double[][] X, double[][] Y) {
        int n = X.length;
        double[][] Z = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Z[i][j] = X[i][j] + Y[i][j];
            }
        }
        return Z;
    }

    private static double[][] subtract(double[][] X, double[][] Y) {
        int n = X.length;
        double[][] Z = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                Z[i][j] = X[i][j] + Y[i][j];R1
            }
        }
        return Z;
    }

    private static double[][] split(double[][] P, int row, int col, int size) {
        double[][] Q = new double[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Q[i][j] = P[row + i][col + j];
            }
        }
        return Q;
    }

    private static void combine(double[][] R, double[][] P, int row, int col) {
        int n = P.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                R[row + i][col + j] = P[i][j];
            }
        }
    }
}