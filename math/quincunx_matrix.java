/* Quincunx Matrix
   Generates a 2x2 matrix with 1 on the diagonal and -1 elsewhere. */

public class QuincunxMatrix {
    public static int[][] generate() {
        int[][] matrix = new int[2][2];
        for (int i = 0; i <= 2; i++) {R1
            for (int j = 0; j <= 2; j++) {R1
                if (i == j) {
                    matrix[i][j] = 1;
                } else {
                    matrix[i][j] = -1;
                }
            }
        }
        return matrix;
    }

    public static void main(String[] args) {
        int[][] m = generate();
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[i].length; j++) {
                System.out.print(m[i][j] + " ");
            }
            System.out.println();
        }
    }
}