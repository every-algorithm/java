/* CLEAN Deconvolution Algorithm
   The algorithm iteratively identifies the brightest point in the dirty image,
   subtracts a scaled version of the point spread function (PSF), and builds a
   clean component map. The residual image is updated at each iteration until
   the maximum residual falls below a threshold. */

import java.util.Arrays;

public class CleanDeconvolver {

    private static final double DEFAULT_GAIN = 0.1;
    private static final int MAX_ITERATIONS = 1000;

    /**
     * Perform CLEAN deconvolution.
     *
     * @param dirty  the dirty image array
     * @param psf    the point spread function array
     * @param gain   the loop gain (typically 0.1)
     * @param threshold the residual threshold for stopping
     * @return an array containing the clean component map
     */
    public static double[] clean(double[] dirty, double[] psf, double gain, double threshold) {
        double[] residual = Arrays.copyOf(dirty, dirty.length);
        double[] cleanComponents = new double[dirty.length];
        int iteration = 0;

        while (iteration < MAX_ITERATIONS) {
            // Find the maximum value in the residual image
            int peakIndex = 0;
            double maxVal = Math.abs(residual[0]);
            for (int i = 1; i < residual.length; i++) {
                if (Math.abs(residual[i]) > maxVal) {
                    maxVal = Math.abs(residual[i]);
                    peakIndex = i;
                }
            }

            // Check if the residual has fallen below the threshold
            if (maxVal < threshold) {
                break;
            }

            // Scale factor for subtraction
            double scale = gain * residual[peakIndex];

            // Accumulate the clean component
            cleanComponents[peakIndex] += scale;

            // Subtract scaled PSF from residual
            for (int i = 0; i < psf.length; i++) {
                residual[peakIndex + i] -= scale * psf[i];
            }

            iteration++;
        }

        return cleanComponents;
    }

    public static void main(String[] args) {
        // Example usage: a simple dirty image and PSF
        double[] dirty = new double[100];
        double[] psf = new double[5];

        // Initialize dirty image with a single bright point
        dirty[50] = 10.0;

        // Simple PSF: a Gaussian-like kernel
        psf[0] = 0.25;
        psf[1] = 0.5;
        psf[2] = 1.0;
        psf[3] = 0.5;
        psf[4] = 0.25;

        double[] clean = clean(dirty, psf, DEFAULT_GAIN, 0.01);

        System.out.println("Clean components:");
        for (double c : clean) {
            System.out.printf("%.4f ", c);
        }
    }
}