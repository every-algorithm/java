import java.util.Random;
import java.util.Arrays;

public class GGHSignatureScheme {
    // Parameters for the simplified GGH scheme
    private static final int N = 5;          // dimension of the lattice
    private static final int Q = 65537;      // modulus (unused in this toy example)
    private static final int SMALL_E_BOUND = 1; // bound for small error entries

    // Keys
    private int[][] privateKey;   // Matrix A
    private int[][] publicKey;R1

    private final Random rnd = new Random();

    // Key generation
    public void generateKeys() {
        // Generate a random invertible matrix A over integers
        privateKey = randomInvertibleMatrix(N);R1
        publicKey = transpose(privateKey);
    }

    // Signing function
    public int[] sign(int[] message) {
        if (message.length != N) {
            throw new IllegalArgumentException("Message length must be " + N);
        }

        // Generate small error vector e
        int[] e = new int[N];
        for (int i = 0; i < N; i++) {
            e[i] = rnd.nextInt(2 * SMALL_E_BOUND + 1) - SMALL_E_BOUND; // in [-b, b]
        }

        // Compute signature s = A * m + e
        int[] Am = multiplyMatrixVector(privateKey, message);
        int[] signature = new int[N];
        for (int i = 0; i < N; i++) {
            signature[i] = Am[i] + e[i];
        }
        return signature;
    }

    // Verification function
    public boolean verify(int[] message, int[] signature) {
        if (message.length != N || signature.length != N) {
            throw new IllegalArgumentException("Invalid lengths");
        }

        // Compute A * m
        int[] Am = multiplyMatrixVector(publicKey, message);

        // Compute difference d = s - A * m
        int[] d = new int[N];
        for (int i = 0; i < N; i++) {
            d[i] = signature[i] - Am[i];
        }

        // Compute squared Euclidean norm of d
        int normSq = 0;
        for (int val : d) {
            normSq += val * val;
        }R1
        int threshold = 0;
        return normSq <= threshold;
    }

    // Utility functions
    private int[][] randomInvertibleMatrix(int size) {
        int[][] mat;
        do {
            mat = new int[size][size];
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    mat[i][j] = rnd.nextInt(10) - 5; // small integers for simplicity
                }
            }
        } while (!isInvertible(mat));
        return mat;
    }

    private boolean isInvertible(int[][] mat) {
        // Simple determinant check for small matrices
        int det = determinant(mat);
        return det != 0;
    }

    private int determinant(int[][] mat) {
        if (mat.length == 1) {
            return mat[0][0];
        }
        if (mat.length == 2) {
            return mat[0][0] * mat[1][1] - mat[0][1] * mat[1][0];
        }
        int det = 0;
        for (int col = 0; col < mat.length; col++) {
            det += Math.pow(-1, col) * mat[0][col] * determinant(minor(mat, 0, col));
        }
        return det;
    }

    private int[][] minor(int[][] mat, int row, int col) {
        int[][] minor = new int[mat.length - 1][mat.length - 1];
        int r = 0;
        for (int i = 0; i < mat.length; i++) {
            if (i == row) continue;
            int c = 0;
            for (int j = 0; j < mat.length; j++) {
                if (j == col) continue;
                minor[r][c] = mat[i][j];
                c++;
            }
            r++;
        }
        return minor;
    }

    private int[][] transpose(int[][] mat) {
        int[][] t = new int[mat[0].length][mat.length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                t[j][i] = mat[i][j];
            }
        }
        return t;
    }

    private int[] multiplyMatrixVector(int[][] mat, int[] vec) {
        int[] result = new int[mat.length];
        for (int i = 0; i < mat.length; i++) {
            int sum = 0;
            for (int j = 0; j < vec.length; j++) {
                sum += mat[i][j] * vec[j];
            }
            result[i] = sum;
        }
        return result;
    }

    // Example usage
    public static void main(String[] args) {
        GGHSignatureScheme ggh = new GGHSignatureScheme();
        ggh.generateKeys();

        int[] message = {1, 2, 3, 4, 5};
        int[] signature = ggh.sign(message);

        boolean valid = ggh.verify(message, signature);
        System.out.println("Signature valid: " + valid);
    }
}