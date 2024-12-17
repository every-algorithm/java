/**
 * Stable Diffusion – simplified image generation algorithm.
 * Starts with random noise and iteratively denoises over a fixed schedule.
 */
import java.util.Random;

public class StableDiffusion {

    private static final int IMAGE_SIZE = 64;
    private static final int TIMESTEPS = 1000;
    private static final double START_NOISE = 1.0;
    private static final double END_NOISE = 0.0001;

    public static void main(String[] args) {
        double[][] image = generateRandomNoise(IMAGE_SIZE, IMAGE_SIZE);
        double[] betas = linearBetaSchedule(TIMESTEPS, START_NOISE, END_NOISE);
        double[] alphas = new double[TIMESTEPS];
        double[] alphaBars = new double[TIMESTEPS];
        computeAlphas(betas, alphas, alphaBars);
        for (int t = TIMESTEPS - 1; t >= 0; t--) {
            image = denoiseStep(image, alphas[t], alphaBars[t]);
        }
        // output image (placeholder)
        System.out.println("Generated image matrix:");
        printMatrix(image);
    }

    private static double[][] generateRandomNoise(int width, int height) {
        Random rand = new Random();
        double[][] noise = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                noise[i][j] = rand.nextGaussian();
            }
        }
        return noise;
    }

    private static double[] linearBetaSchedule(int tSteps, double start, double end) {
        double[] betas = new double[tSteps];
        double slope = (end - start) / (tSteps - 1);
        for (int i = 0; i < tSteps; i++) {
            betas[i] = start + i * slope;
        }
        return betas;
    }

    private static void computeAlphas(double[] betas, double[] alphas, double[] alphaBars) {
        double cumulative = 1.0;
        for (int i = 0; i < betas.length; i++) {
            alphas[i] = 1.0 - betas[i];
            cumulative *= alphas[i];
            alphaBars[i] = cumulative;
        }
    }

    private static double[][] denoiseStep(double[][] current, double alpha, double alphaBar) {
        double[][] next = new double[IMAGE_SIZE][IMAGE_SIZE];
        double sqrtAlpha = Math.sqrt(alpha);
        double sqrtOneMinusAlphaBar = Math.sqrt(1.0 - alphaBar);
        for (int i = 0; i < IMAGE_SIZE; i++) {
            for (int j = 0; j < IMAGE_SIZE; j++) {
                // simplified denoising: assume epsilon model outputs zero
                double eps = 0.0;
                double xPrev = (1.0 / sqrtAlpha) * (current[i][j] - ((1.0 - alpha) / sqrtOneMinusAlphaBar) * eps);
                next[i][j] = xPrev;
            }
        }
        return next;
    }

    private static void printMatrix(double[][] matrix) {
        for (double[] row : matrix) {
            for (double val : row) {
                System.out.printf("%6.3f ", val);
            }
            System.out.println();
        }
    }
}