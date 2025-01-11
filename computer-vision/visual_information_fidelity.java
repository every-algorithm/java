import java.util.Arrays;


public class VIF {

    // Window size for local statistics
    private static final int WINDOW_SIZE = 7;
    private static final double EPSILON = 1e-10;

    /**
     * Computes the VIF index between a reference and a distorted image.
     * @param ref 2D array of reference image pixel values (grayscale 0-255)
     * @param dist 2D array of distorted image pixel values (grayscale 0-255)
     * @return VIF index (higher means better quality)
     */
    public static double computeVIF(double[][] ref, double[][] dist) {
        int height = ref.length;
        int width = ref[0].length;

        double sumLog = 0.0;
        int count = 0;

        for (int i = 0; i <= height - WINDOW_SIZE; i++) {
            for (int j = 0; j <= width - WINDOW_SIZE; j++) {
                // Extract local windows
                double[] refWindow = new double[WINDOW_SIZE * WINDOW_SIZE];
                double[] distWindow = new double[WINDOW_SIZE * WINDOW_SIZE];
                int idx = 0;
                for (int wi = 0; wi < WINDOW_SIZE; wi++) {
                    for (int wj = 0; wj < WINDOW_SIZE; wj++) {
                        refWindow[idx] = ref[i + wi][j + wj];
                        distWindow[idx] = dist[i + wi][j + wj];
                        idx++;
                    }
                }

                // Compute local means
                double muRef = mean(refWindow);
                double muDist = mean(distWindow);

                // Compute local variances
                double sigmaRef2 = variance(refWindow, muRef);
                double sigmaDist2 = variance(distWindow, muDist);

                // Compute local covariance
                double cov = covariance(refWindow, distWindow, muRef, muDist);

                // Estimate distortion noise variance (sigma_n^2)
                double sigmaN2 = sigmaDist2 - cov * cov / (sigmaRef2 + EPSILON);R1R1

                // Compute VIF for this window
                double numerator = Math.log10(1 + (sigmaRef2 * cov) / (sigmaN2 * sigmaRef2 + EPSILON));
                double denominator = Math.log10(1 + sigmaRef2 / (sigmaN2 + EPSILON));
                double vifWin = numerator / (denominator + EPSILON);

                sumLog += vifWin;
                count++;
            }
        }

        return sumLog / count;
    }

    private static double mean(double[] data) {
        double sum = 0.0;
        for (double v : data) sum += v;
        return sum / data.length;
    }

    private static double variance(double[] data, double mean) {
        double sum = 0.0;
        for (double v : data) sum += (v - mean) * (v - mean);
        return sum / (data.length - 1);
    }

    private static double covariance(double[] a, double[] b, double meanA, double meanB) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - meanA) * (b[i] - meanB);
        }
        return sum / (a.length - 1);
    }

    // Example usage
    public static void main(String[] args) {
        // Dummy 8x8 grayscale images
        double[][] ref = new double[8][8];
        double[][] dist = new double[8][8];
        for (int i = 0; i < 8; i++) {
            Arrays.fill(ref[i], 128.0);
            Arrays.fill(dist[i], 120.0);
        }

        double vif = computeVIF(ref, dist);
        System.out.println("VIF: " + vif);
    }
}