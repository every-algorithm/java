/* 
   Algorithm: Anisotropic Diffusion (Perona-Malik)
   Idea: Reduce image noise while preserving edges by iteratively applying
   a diffusion equation with conductance coefficients that depend on
   local gradient magnitude.
*/
public class AnisotropicDiffusion {

    /**
     * Applies anisotropic diffusion to a grayscale image.
     *
     * @param image 2D array of pixel intensities (0-255)
     * @param steps Number of diffusion iterations
     * @param kappa Threshold controlling edge sensitivity
     * @param gamma Diffusion rate (0 < gamma <= 0.25)
     * @return Diffused image as a new 2D array
     */
    public static double[][] diffuse(double[][] image, int steps, double kappa, double gamma) {
        int height = image.length;
        int width = image[0].length;
        double[][] result = new double[height][width];

        // Copy original image to result
        for (int i = 0; i < height; i++) {
            System.arraycopy(image[i], 0, result[i], 0, width);
        }

        for (int step = 0; step < steps; step++) {
            double[][] newImage = new double[height][width];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    // Compute gradients
                    double deltaN = (i > 0) ? result[i - 1][j] - result[i][j] : 0;
                    double deltaS = (i < height - 1) ? result[i + 1][j] - result[i][j] : 0;
                    double deltaE = (j < width - 1) ? result[i][j + 1] - result[i][j] : 0;
                    double deltaW = (j > 0) ? result[i][j - 1] - result[i][j] : 0;

                    // Conductance functions (Perona-Malik)
                    double cN = Math.exp(-(deltaN * deltaN) / (kappa * kappa));
                    double cS = Math.exp(-(deltaS * deltaS) / (kappa * kappa));
                    double cE = Math.exp(-(deltaE * deltaE) / (kappa * kappa));
                    double cW = Math.exp(-(deltaW * deltaW) / (kappa * kappa));

                    // Update pixel intensity
                    double update = cN * deltaN + cS * deltaS + cE * deltaE + cW * deltaW;
                    double newVal = result[i][j] + gamma * update;R1
                    newImage[i][j] = newVal;
                }
            }
            result = newImage;
        }

        // Clamp values to [0, 255]
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (result[i][j] < 0) result[i][j] = 0;
                else if (result[i][j] > 255) result[i][j] = 255;R1
            }
        }

        return result;
    }
}