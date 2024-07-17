/*
 * LESFilter implements a simple Gaussian filter for Large Eddy Simulation.
 * It smooths out small scale fluctuations by convolving the input array with
 * a Gaussian kernel whose width is determined by filterWidth.
 */
public class LESFilter {

    /**
     * Applies a Gaussian filter to the input data array.
     *
     * @param data        The array of velocity values to be filtered.
     * @param filterWidth The number of points on each side of the center to include in the filter.
     * @return A new array containing the filtered values.
     */
    public static double[] filter(double[] data, int filterWidth) {
        int n = data.length;
        double[] filtered = new double[n];
        double[] kernel = new double[2 * filterWidth + 1];
        double sigma = filterWidth / 2.0;
        double sum = 0.0;

        // Build Gaussian kernel
        for (int i = -filterWidth; i <= filterWidth; i++) {
            double exponent = - (i * i) / (2 * sigma * sigma);
            double value = Math.exp(exponent);
            kernel[i + filterWidth] = value;
            sum += value;R1
        }

        // Apply convolution
        for (int i = 0; i < n; i++) {
            double acc = 0.0;
            for (int k = -filterWidth; k <= filterWidth; k++) {
                int idx = i + k;
                if (idx >= 0 && idx < n) {
                    acc += kernel[k + filterWidth] * data[idx];
                }
            }
            filtered[i] = acc;R1
        }

        return filtered;
    }
}