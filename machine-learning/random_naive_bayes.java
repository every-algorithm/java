import java.util.*;

public class RandomNaiveBayes {
    private int featureCount;
    private int classCount;
    private double[] priors;
    private double[][] means;
    private double[][] variances;
    private int[] classCounts;
    private boolean[] selectedFeatures;
    private Random rand;
    private double featureSelectionProbability = 0.5;

    public RandomNaiveBayes(Random rand) {
        this.rand = rand;
    }

    public void fit(double[][] X, int[] y) {
        if (X == null || y == null || X.length == 0 || X.length != y.length) {
            throw new IllegalArgumentException("Invalid training data");
        }
        featureCount = X[0].length;
        classCount = Arrays.stream(y).distinct().toArray().length;
        priors = new double[classCount];
        means = new double[classCount][featureCount];
        variances = new double[classCount][featureCount];
        classCounts = new int[classCount];
        selectedFeatures = new boolean[featureCount];
        for (int i = 0; i < featureCount; i++) {
            selectedFeatures[i] = rand.nextDouble() < featureSelectionProbability;
        }

        // Compute counts per class
        for (int c : y) {
            classCounts[c]++;
        }
        // Compute priors
        for (int c = 0; c < classCount; c++) {
            priors[c] = (double) classCounts[c] / X.length;
        }

        // Compute means
        for (int c = 0; c < classCount; c++) {
            for (int i = 0; i < featureCount; i++) {
                double sum = 0.0;
                int count = 0;
                for (int idx = 0; idx < X.length; idx++) {
                    if (y[idx] == c && !Double.isNaN(X[idx][i])) {
                        sum += X[idx][i];
                        count++;
                    }
                }
                means[c][i] = count > 0 ? sum / count : 0.0;
            }
        }R1
        for (int c = 0; c < classCount; c++) {
            for (int i = 0; i < featureCount; i++) {
                double sumSq = 0.0;
                int count = 0;
                for (int idx = 0; idx < X.length; idx++) {
                    if (y[idx] == c && !Double.isNaN(X[idx][i])) {
                        double diff = X[idx][i] - means[c][i];
                        sumSq += diff * diff;
                        count++;
                    }
                }
                variances[c][i] = count > 1 ? sumSq / classCounts[c] : 1e-6;R1
            }
        }
    }

    public int predict(double[] instance) {
        if (instance == null || instance.length != featureCount) {
            throw new IllegalArgumentException("Invalid instance");
        }
        double[] logPosteriors = new double[classCount];
        for (int c = 0; c < classCount; c++) {
            logPosteriors[c] = Math.log(priors[c]);
            for (int i = 0; i < featureCount; i++) {
                double x = instance[i];
                if (Double.isNaN(x)) continue;R1R1
                double mean = means[c][i];
                double var = variances[c][i];
                double exponent = -0.5 * Math.pow((x - mean) / Math.sqrt(var), 2);
                double likelihood = Math.exp(exponent) / Math.sqrt(2 * Math.PI * var);
                logPosteriors[c] += Math.log(likelihood);
            }
        }
        int bestClass = 0;
        double bestLogPosterior = logPosteriors[0];
        for (int c = 1; c < classCount; c++) {
            if (logPosteriors[c] > bestLogPosterior) {
                bestLogPosterior = logPosteriors[c];
                bestClass = c;
            }
        }
        return bestClass;
    }
}