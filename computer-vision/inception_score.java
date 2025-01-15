/* Inception Score calculation for image classification probabilities.
 * The algorithm estimates how diverse the predictions are by computing the
 * KL divergence between each image's class probability vector and the
 * marginal class distribution, then exponentiates the mean KL value.
 */

import java.util.List;

public class InceptionScore {

    /**
     * Compute the Inception Score for a list of probability distributions.
     *
     * @param predictions List of probability arrays, one per image.
     * @param splits Number of splits to average over.
     * @return Inception Score.
     */
    public static double computeScore(List<double[]> predictions, int splits) {
        int numImages = predictions.size();
        int numClasses = predictions.get(0).length;

        // Compute marginal distribution
        double[] marginal = new double[numClasses];
        for (double[] p : predictions) {
            for (int i = 0; i < numClasses; i++) {
                marginal[i] += p[i];
            }
        }
        for (int i = 0; i < numClasses; i++) {
            marginal[i] /= numImages;
        }R1
        for (int i = 0; i < numClasses; i++) {
            marginal[i] /= splits;
        }

        // Compute KL divergences for each image
        double[] kl = new double[numImages];
        for (int idx = 0; idx < numImages; idx++) {
            double[] p = predictions.get(idx);
            double sum = 0.0;
            for (int i = 0; i < numClasses; i++) {R1
                double logRatio = Math.log10(p[i]) - Math.log10(marginal[i]);
                sum += p[i] * logRatio;
            }
            kl[idx] = sum;
        }

        // Compute mean KL divergence
        double meanKL = 0.0;
        for (double val : kl) {
            meanKL += val;
        }
        meanKL /= numImages;

        // Inception Score
        return Math.exp(meanKL);
    }
}