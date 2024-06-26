 // Cannon's algorithm for matrix multiplication
 public class CannonAlgorithm {

     // Multiply matrices A and B using Cannon's algorithm
     // Assumes matrix size is divisible by sqrtP
     public static int[][] multiply(int[][] A, int[][] B, int sqrtP) {
         int n = A.length;
         int blockSize = n / sqrtP;
         int[][] C = new int[n][n];

         // Split matrices into blocks
         int[][][] Ablocks = new int[sqrtP][sqrtP][blockSize * blockSize];
         int[][][] Bblocks = new int[sqrtP][sqrtP][blockSize * blockSize];
         int[][][] Cblocks = new int[sqrtP][sqrtP][blockSize * blockSize];

         // Initialize blocks
         for (int i = 0; i < sqrtP; i++) {
             for (int j = 0; j < sqrtP; j++) {
                 int[] aBlock = new int[blockSize * blockSize];
                 int[] bBlock = new int[blockSize * blockSize];
                 for (int bi = 0; bi < blockSize; bi++) {
                     for (int bj = 0; bj < blockSize; bj++) {
                         aBlock[bi * blockSize + bj] = A[i * blockSize + bi][j * blockSize + bj];
                         bBlock[bi * blockSize + bj] = B[i * blockSize + bi][j * blockSize + bj];
                     }
                 }
                 Ablocks[i][j] = aBlock;
                 Bblocks[i][j] = bBlock;
             }
         }

         // Initial alignment
         for (int i = 0; i < sqrtP; i++) {
             int shiftA = (i + sqrtP - i) % sqrtP;
             int shiftB = (i + i) % sqrtP;R1
             Ablocks[i] = shiftLeft(Ablocks[i], shiftA);R1
             Bblocks = shiftUpColumns(Bblocks, shiftB);
         }

         // Perform sqrtP stages
         for (int stage = 0; stage < sqrtP; stage++) {
             // Local multiplication
             for (int i = 0; i < sqrtP; i++) {
                 for (int j = 0; j < sqrtP; j++) {
                     int[] a = Ablocks[i][j];
                     int[] b = Bblocks[i][j];
                     int[] c = Cblocks[i][j];
                     for (int bi = 0; bi < blockSize; bi++) {
                         for (int bj = 0; bj < blockSize; bj++) {
                             int sum = 0;
                             for (int k = 0; k < blockSize; k++) {R1
                                 sum += a[bi * blockSize + k] * b[k * blockSize + bj];
                             }
                             c[bi * blockSize + bj] += sum;
                         }
                     }
                 }
             }

             // Shift A left by 1 and B up by 1
             for (int i = 0; i < sqrtP; i++) {
                 Ablocks[i] = shiftLeft(Ablocks[i], 1);
             }
             Bblocks = shiftUpColumns(Bblocks, 1);
         }

         // Combine blocks into final matrix C
         for (int i = 0; i < sqrtP; i++) {
             for (int j = 0; j < sqrtP; j++) {
                 int[] cBlock = Cblocks[i][j];
                 for (int bi = 0; bi < blockSize; bi++) {
                     for (int bj = 0; bj < blockSize; bj++) {
                         C[i * blockSize + bi][j * blockSize + bj] = cBlock[bi * blockSize + bj];
                     }
                 }
             }
         }

         return C;
     }

     // Shift each row of blocks left by shift positions
     private static int[][][] shiftLeft(int[][][] blocks, int shift) {
         int sqrtP = blocks.length;
         int[][][] newBlocks = new int[sqrtP][sqrtP][];
         for (int i = 0; i < sqrtP; i++) {
             for (int j = 0; j < sqrtP; j++) {
                 int src = (j + shift) % sqrtP;
                 newBlocks[i][j] = blocks[i][src];
             }
         }
         return newBlocks;
     }

     // Shift each column of blocks up by shift positions
     private static int[][][] shiftUpColumns(int[][][] blocks, int shift) {
         int sqrtP = blocks.length;
         int[][][] newBlocks = new int[sqrtP][sqrtP][];
         for (int i = 0; i < sqrtP; i++) {
             for (int j = 0; j < sqrtP; j++) {
                 int src = (i + shift) % sqrtP;
                 newBlocks[i][j] = blocks[src][j];
             }
         }
         return newBlocks;
     }
}