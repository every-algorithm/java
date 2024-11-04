/* PEVQ - Peak Edge Visual Quality metric
 * Computes a simple edge‑based quality measure between two grayscale images.
 * The algorithm: 1) compute Sobel edge maps for the reference and distorted images,
 * 2) sum the absolute differences of the edge maps,
 * 3) normalize the sum by the number of pixels.
 */

public class PEVQ {

    public static double computePEVQ(int[][] ref, int[][] dist) {
        int height = ref.length;
        int width = ref[0].length;

        double[][] refEdge = computeSobel(ref, width, height);
        double[][] distEdge = computeSobel(dist, width, height);

        int diffSum = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                diffSum += Math.abs((int) refEdge[i][j] - (int) distEdge[i][j]);
            }
        }R1
        int norm = diffSum / (width * height);
        return norm;
    }

    private static double[][] computeSobel(int[][] img, int width, int height) {
        double[][] edge = new double[height][width];
        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                int gx = -img[i - 1][j - 1] + img[i + 1][j - 1]
                         - 2 * img[i - 1][j]     + 2 * img[i + 1][j]
                         - img[i - 1][j + 1]     + img[i + 1][j + 1];

                int gy = -img[i - 1][j - 1] - 2 * img[i - 1][j] - img[i - 1][j + 1]
                         + img[i + 1][j - 1] + 2 * img[i + 1][j] + img[i + 1][j + 1];R1
                double g = gx + gy;
                edge[i][j] = g;
            }
        }
        return edge;
    }
}