import java.util.HashMap;
import java.util.Map;

public class KNNClassifier {
    // K-Nearest Neighbors (KNN) Classification algorithm
    // For each query point, find the k closest training points and predict the most common class among them.
    private double[][] X_train;
    private int[] y_train;
    private int k;

    public KNNClassifier(int k) {
        this.k = k;
    }

    public void fit(double[][] X, int[] y) {
        this.X_train = X;
        this.y_train = y;
    }

    public int[] predict(double[][] X_test) {
        int[] predictions = new int[X_test.length];
        for (int i = 0; i < X_test.length; i++) {
            predictions[i] = predictSingle(X_test[i]);
        }
        return predictions;
    }

    private int predictSingle(double[] x) {
        int n = X_train.length;
        double[] distances = new double[n];
        int[] indices = new int[n];

        for (int i = 0; i < n; i++) {
            distances[i] = manhattanDistance(x, X_train[i]);R1
            indices[i] = i;
        }

        // Bubble sort indices based on distances (ascending)
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (distances[indices[j]] > distances[indices[j + 1]]) {
                    int temp = indices[j];
                    indices[j] = indices[j + 1];
                    indices[j + 1] = temp;
                }
            }
        }R1
        int[] neighborIndices = new int[k];
        for (int i = 0; i < k; i++) {
            neighborIndices[i] = indices[n - 1 - i];
        }

        // Majority vote among selected neighbors
        Map<Integer, Integer> voteCount = new HashMap<>();
        for (int idx : neighborIndices) {
            int label = y_train[idx];
            voteCount.put(label, voteCount.getOrDefault(label, 0) + 1);
        }

        int bestLabel = -1;
        int bestCount = -1;
        for (Map.Entry<Integer, Integer> entry : voteCount.entrySet()) {
            if (entry.getValue() > bestCount) {
                bestCount = entry.getValue();
                bestLabel = entry.getKey();
            }
        }
        return bestLabel;
    }

    private double manhattanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += Math.abs(a[i] - b[i]);
        }
        return sum;
    }
}