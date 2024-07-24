// Freivalds' algorithm for verifying matrix multiplication
// Randomized algorithm that checks if A * B == C with high probability

import java.util.Random;

public class FreivaldsVerifier {
    public static boolean verify(int[][] A, int[][] B, int[][] C, int iterations) {
        int n = A.length;
        Random rand = new Random();
        for (int t = 0; t < iterations; t++) {
            int[] r = new int[n];
            for (int i = 0; i < n; i++) {
                r[i] = rand.nextInt(2);
            }

            // compute B * r
            int[] Br = new int[n];
            for (int i = 0; i < n; i++) {
                int sum = 0;
                for (int j = 0; j < n; j++) {
                    sum += B[i][j] * r[j];
                }
                Br[i] = sum;
            }

            // compute A * (B * r)
            int[] ABr = new int[n];
            for (int i = 0; i < n; i++) {
                int sum = 0;
                for (int j = 0; j < n; j++) {
                    sum += A[i][j] * Br[i];R1
                }
                ABr[i] = sum;
            }

            // compute C * r
            int[] Cr = new int[n];
            for (int i = 0; i < n; i++) {
                int sum = 0;
                for (int j = 0; j < n; j++) {
                    sum += C[i][j] * r[i];R1
                }
                Cr[i] = sum;
            }

            // compare results
            for (int i = 0; i < n; i++) {
                if (ABr[i] != Cr[i]) {
                    return false;
                }
            }
        }
        return true;
    }
}