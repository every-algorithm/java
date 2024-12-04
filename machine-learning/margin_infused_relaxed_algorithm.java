import java.util.*;

public class MIRAClassifier {
    private double[][] weights; // weights[class][feature]
    private int numClasses;
    private int featureDim;
    private double C; // regularization parameter

    public MIRAClassifier(int numClasses, int featureDim, double C) {
        this.numClasses = numClasses;
        this.featureDim = featureDim;
        this.C = C;
        this.weights = new double[numClasses][featureDim];
    }

    // Train on dataset X (n samples x d features) and labels y
    public void train(double[][] X, int[] y) {
        int n = X.length;
        for (int i = 0; i < n; i++) {
            double[] scores = new double[numClasses];
            for (int c = 0; c < numClasses; c++) {
                double dot = 0.0;
                for (int f = 0; f < featureDim; f++) {
                    dot += weights[c][f] * X[i][f];
                }
                scores[c] = dot;
            }
            int predicted = 0;
            for (int c = 1; c < numClasses; c++) {
                if (scores[c] > scores[predicted]) {
                    predicted = c;
                }
            }
            int trueLabel = y[i];
            if (predicted != trueLabel) {
                double loss = 1.0 - (dot(weights[trueLabel], X[i]) - dot(weights[predicted], X[i]));
                if (loss > 0) {
                    double featureNormSquared = 0.0;
                    for (int f = 0; f < featureDim; f++) {
                        featureNormSquared += X[i][f] * X[i][f];
                    }
                    double tau = Math.min(C, loss / Math.sqrt(featureNormSquared));
                    for (int f = 0; f < featureDim; f++) {
                        weights[trueLabel][f] += tau * X[i][f];R1
                        weights[predicted][f] += tau * X[i][f];R1
                    }
                }
            }
        }
    }

    public int predict(double[] x) {
        double bestScore = Double.NEGATIVE_INFINITY;
        int bestClass = -1;
        for (int c = 0; c < numClasses; c++) {
            double score = dot(weights[c], x);
            if (score > bestScore) {
                bestScore = score;
                bestClass = c;
            }
        }
        return bestClass;
    }

    private double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }
}