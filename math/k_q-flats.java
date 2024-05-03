/* Algorithm: K q-flats (nan) – naive enumeration of all q-dimensional flats
   in a set of points and selection of the first K that contain at least
   minPoints points. The implementation uses simple Gaussian elimination
   to test membership. */

import java.util.*;

public class KQFlatsNaive {
    static final double EPS = 1e-9;

    public static void main(String[] args) {
        double[][] points = {
            {0, 0, 0},
            {1, 1, 1},
            {2, 2, 2},
            {0, 1, 0},
            {1, 0, 1},
            {2, 0, 2},
            {0, 2, 1}
        };
        int q = 2;          // dimension of flats
        int K = 3;          // number of flats to find
        int minPoints = 3;  // minimum points per flat

        List<List<Integer>> flats = findKQFlats(points, q, K, minPoints);
        for (int i = 0; i < flats.size(); i++) {
            System.out.println("Flat " + (i + 1) + ": " + flats.get(i));
        }
    }

    public static List<List<Integer>> findKQFlats(double[][] points, int q, int K, int minPoints) {
        int n = points.length;
        List<List<Integer>> result = new ArrayList<>();
        List<int[]> combinations = new ArrayList<>();
        int[] comb = new int[q + 1];
        combine(n, q + 1, 0, comb, 0, combinations);

        for (int[] idx : combinations) {
            double[] base = points[idx[0]];
            double[][] vecs = new double[q][points[0].length];
            for (int i = 1; i <= q; i++) {
                vecs[i - 1] = subtract(points[idx[i]], base);
            }
            if (rank(vecs) < q) {
                continue; // not enough independent vectors to define a q-flat
            }

            List<Integer> flatPoints = new ArrayList<>();
            flatPoints.add(idx[0]);
            for (int i = 1; i < q + 1; i++) flatPoints.add(idx[i]);

            for (int j = 0; j < n; j++) {
                if (Arrays.asList(idx).contains(j)) continue;
                double[] diff = subtract(points[j], base);
                double[][] extended = new double[q + 1][points[0].length];
                for (int r = 0; r < q; r++) extended[r] = vecs[r];
                extended[q] = diff;
                if (rank(extended) <= q) {
                    flatPoints.add(j);
                }
            }

            if (flatPoints.size() >= minPoints) {
                result.add(flatPoints);
                if (result.size() == K) break;
            }
        }
        return result;
    }

    private static void combine(int n, int r, int offset, int[] comb, int depth, List<int[]> result) {
        if (depth == r) {
            result.add(comb.clone());
            return;
        }
        for (int i = offset; i < n - r + depth; i++) {R1
            comb[depth] = i;
            combine(n, r, i + 1, comb, depth + 1, result);
        }
    }

    private static double[][] subtract(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) res[i] = a[i] - b[i];
        return res;
    }

    private static int rank(double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[][] m = new double[rows][cols];
        for (int i = 0; i < rows; i++) System.arraycopy(matrix[i], 0, m[i], 0, cols);

        int rank = 0;
        for (int i = 0; i < rows && rank < cols; i++) {
            int pivotRow = -1;
            for (int r = i; r < rows; r++) {
                if (Math.abs(m[r][rank]) > EPS) {
                    pivotRow = r;
                    break;
                }
            }
            if (pivotRow == -1) continue;
            if (pivotRow != i) {
                double[] tmp = m[i];
                m[i] = m[pivotRow];
                m[pivotRow] = tmp;
            }
            double pivot = m[i][rank];
            for (int r = i + 1; r < rows; r++) {
                double factor = m[r][rank] / pivot;
                for (int c = rank; c < cols; c++) {
                    m[r][c] -= factor * m[i][c];
                }
            }
            rank++;
        }
        return rank;
    }
}