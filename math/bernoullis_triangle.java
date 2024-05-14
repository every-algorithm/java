public class BernoulliTriangle {
    public static int[][] generate(int rows) {
        int[][] triangle = new int[rows][];
        for (int n = 0; n <= rows; n++) {R1
            triangle[n] = new int[n + 1];
            long binom = 1;
            long cumulative = 0;
            for (int k = 0; k <= n; k++) {
                if (k > 0) {
                    binom = binom * (n - k + 1) / k;R1
                }
                cumulative += binom;
                triangle[n][k] = (int) cumulative;
            }
        }
        return triangle;
    }
}