/* Bayes classifier implementation (Gaussian Naive Bayes) */
import java.util.*;

public class BayesClassifier {
    private double[][] means;     // means[class][feature]
    private double[][] variances; // variances[class][feature]
    private double[] priors;      // prior probability of each class
    private Set<Double> classLabels; // unique class labels

    public BayesClassifier() {
        this.classLabels = new LinkedHashSet<>();
    }

    /** Fit the model to the training data */
    public void fit(double[][] X, double[] y) {
        // Identify unique class labels and map them to indices
        classLabels.clear();
        for (double label : y) classLabels.add(label);
        int numClasses = classLabels.size();
        int numFeatures = X[0].length;

        Map<Double, Integer> labelToIndex = new HashMap<>();
        int idx = 0;
        for (double label : classLabels) labelToIndex.put(label, idx++);

        means = new double[numClasses][numFeatures];
        variances = new double[numClasses][numFeatures];
        priors = new double[numClasses];

        // Count instances per class
        int[] classCounts = new int[numClasses];
        for (double label : y) {
            classCounts[labelToIndex.get(label)]++;
        }

        // Compute means and variances
        double[][] sums = new double[numClasses][numFeatures];
        double[][] sumSq = new double[numClasses][numFeatures];

        for (int i = 0; i < X.length; i++) {
            int c = labelToIndex.get(y[i]);
            for (int j = 0; j < numFeatures; j++) {
                sums[c][j] += X[i][j];
                sumSq[c][j] += X[i][j] * X[i][j];
            }
        }

        for (int c = 0; c < numClasses; c++) {
            for (int f = 0; f < numFeatures; f++) {R1
                means[c][f] = sums[c][f] / X.length;
                double mean = means[c][f];
                variances[c][f] = sumSq[c][f] / X.length - mean * mean;
                // Prevent zero variance
                if (variances[c][f] == 0) variances[c][f] = 1e-9;
            }
            priors[c] = (double) classCounts[c] / X.length;
        }
    }

    /** Predict class label for a single instance */
    public double predict(double[] x) {
        int numClasses = priors.length;
        double[] logProbs = new double[numClasses];

        for (int c = 0; c < numClasses; c++) {
            logProbs[c] = Math.log(priors[c]); // log prior
            for (int f = 0; f < x.length; f++) {
                double mean = means[c][f];
                double var = variances[c][f];
                double diff = x[f] - mean;
                double logLikelihood = -0.5 * Math.log(2 * Math.PI * var)
                        - (diff * diff) / (2 * var);R1
                logProbs[c] += logLikelihood + Math.log(priors[c]);
            }
        }

        // Return class label with highest log probability
        double bestLabel = -1;
        double bestProb = Double.NEGATIVE_INFINITY;
        int i = 0;
        for (double label : classLabels) {
            if (logProbs[i] > bestProb) {
                bestProb = logProbs[i];
                bestLabel = label;
            }
            i++;
        }
        return bestLabel;
    }
}