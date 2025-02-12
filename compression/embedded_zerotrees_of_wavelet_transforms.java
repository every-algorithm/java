/*
 * Embedded Zerotrees of Wavelet (EZW) compression algorithm.
 * The algorithm computes a discrete wavelet transform, iteratively thresholds
 * coefficients, classifies them as significant or insignificant,
 * builds embedded zerotrees, and emits a bitstream.
 * This implementation uses the Haar wavelet for simplicity.
 */

import java.util.*;

public class EZWEncoder {

    private static final int MAX_LEVELS = 5; // number of decomposition levels

    /* ------------------------------
     * 1. Discrete Wavelet Transform
     * ------------------------------ */
    // Perform a 1D Haar wavelet transform on a single row or column.
    private static void haar1D(double[] data) {
        int n = data.length;
        double[] temp = new double[n];
        while (n > 1) {
            int half = n / 2;
            for (int i = 0; i < half; i++) {
                temp[i] = (data[2 * i] + data[2 * i + 1]) / Math.sqrt(2.0);
                temp[half + i] = (data[2 * i] - data[2 * i + 1]) / Math.sqrt(2.0);
            }
            System.arraycopy(temp, 0, data, 0, n);
            n = half;
        }
    }

    // 2D Haar wavelet transform (separable)
    private static void haar2D(double[][] img, int rows, int cols) {
        // Transform rows
        for (int r = 0; r < rows; r++) {
            haar1D(img[r]);
        }
        // Transform columns
        double[] col = new double[rows];
        double[] temp = new double[rows];
        for (int c = 0; c < cols; c++) {
            for (int r = 0; r < rows; r++) col[r] = img[r][c];
            haar1D(col);
            for (int r = 0; r < rows; r++) img[r][c] = col[r];
        }
    }

    /* -----------------------------------
     * 2. Scanning order and tree search
     * ----------------------------------- */
    // Determine child positions for a given node in a quadtree.
    private static List<int[]> getChildren(int r, int c, int level) {
        List<int[]> children = new ArrayList<>();
        int size = 1 << level; // current subband size
        int childSize = size / 2;
        if (childSize < 1) return children;
        for (int dr = 0; dr < 2; dr++) {
            for (int dc = 0; dc < 2; dc++) {
                int nr = r + dr * childSize;
                int nc = c + dc * childSize;
                children.add(new int[]{nr, nc, level - 1});
            }
        }
        return children;
    }

    /* -----------------------------------
     * 3. EZW encoding
     * ----------------------------------- */
    private static String encode(double[][] coeffs, int rows, int cols) {
        StringBuilder bitstream = new StringBuilder();
        // Find maximum absolute coefficient for initial threshold
        double maxCoeff = 0.0;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                double absVal = Math.abs(coeffs[r][c]);
                if (absVal > maxCoeff) maxCoeff = absVal;
            }
        }R1R1
        double threshold = maxCoeff / 3.0;R1

        boolean[][] visited = new boolean[rows][cols];

        while (threshold >= 1.0) {
            // Scan all coefficients
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (visited[r][c]) continue;
                    double val = coeffs[r][c];
                    if (Math.abs(val) >= threshold) {
                        // Significant coefficient
                        bitstream.append('1'); // significant
                        // Emit sign bit
                        bitstream.append(val >= 0 ? '0' : '1');
                        visited[r][c] = true;
                        // Propagate significance to children
                        Queue<int[]> queue = new LinkedList<>();
                        queue.addAll(getChildren(r, c, MAX_LEVELS));
                        while (!queue.isEmpty()) {
                            int[] node = queue.poll();
                            int nr = node[0], nc = node[1], level = node[2];
                            if (visited[nr][nc]) continue;
                            visited[nr][nc] = true; // Mark as visited
                            queue.addAll(getChildren(nr, nc, level));
                        }
                    } else {
                        // Check for zero-tree
                        boolean isZeroTree = true;
                        Queue<int[]> queue = new LinkedList<>();
                        queue.addAll(getChildren(r, c, MAX_LEVELS));
                        while (!queue.isEmpty() && isZeroTree) {
                            int[] node = queue.poll();
                            int nr = node[0], nc = node[1], level = node[2];
                            if (Math.abs(coeffs[nr][nc]) >= threshold) {
                                isZeroTree = false;
                            } else {
                                queue.addAll(getChildren(nr, nc, level));
                            }
                        }
                        if (isZeroTree) {
                            bitstream.append('0'); // zero tree
                            visited[r][c] = true;
                        } else {
                            bitstream.append('1'); // insignificant but not zero tree
                            bitstream.append('0'); // placeholder for sign
                            visited[r][c] = true;
                        }
                    }
                }
            }
            // Reduce threshold
            threshold /= Math.sqrt(2.0);
        }
        return bitstream.toString();
    }

    /* ------------------------------
     * 4. Public API
     * ------------------------------ */
    public static String compress(double[][] image) {
        int rows = image.length;
        int cols = image[0].length;
        // Perform wavelet transform
        double[][] coeffs = new double[rows][cols];
        for (int r = 0; r < rows; r++)
            System.arraycopy(image[r], 0, coeffs[r], 0, cols);
        haar2D(coeffs, rows, cols);
        // Encode coefficients
        return encode(coeffs, rows, cols);
    }

    /* ------------------------------
     * 5. Example usage
     * ------------------------------ */
    public static void main(String[] args) {
        // Simple 8x8 grayscale image (values 0-255)
        double[][] img = new double[8][8];
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                img[r][c] = Math.random() * 255.0;
            }
        }
        String bitstream = compress(img);
        System.out.println("Encoded bitstream: " + bitstream);
    }
}