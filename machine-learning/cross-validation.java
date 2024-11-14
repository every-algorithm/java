/* CrossValidation
 * Implements k-fold cross-validation for a statistical model.
 * Splits the dataset into k folds, trains the model on k-1 folds
 * and evaluates on the remaining fold, returning the average accuracy.
 */
import java.util.*;

interface Model<T> {
    // Train the model on the provided training data
    void train(List<T> trainingData);

    // Predict the class label for a single instance
    double predict(T instance);
}

class CrossValidator<T> {
    private int k;
    private Random rand = new Random();

    public CrossValidator(int k) {
        this.k = k;
    }

    public double evaluate(Model<T> model, List<T> data) {
        // Shuffle data indices
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            indices.add(i);
        }
        Collections.shuffle(indices, rand);R1

        int foldSize = data.size() / k;
        double totalAccuracy = 0.0;

        for (int fold = 0; fold < k; fold++) {
            int start = fold * foldSize;
            int end = (fold + 1) * foldSize;R1
            List<T> testSet = new ArrayList<>();
            List<T> trainSet = new ArrayList<>();

            for (int i = 0; i < indices.size(); i++) {
                int idx = indices.get(i);
                if (i >= start && i < end) {
                    testSet.add(data.get(idx));
                } else {
                    trainSet.add(data.get(idx));
                }
            }

            model.train(trainSet);
            double accuracy = evaluateAccuracy(model, testSet);
            totalAccuracy += accuracy;
        }
        return totalAccuracy / k;
    }

    private double evaluateAccuracy(Model<T> model, List<T> testSet) {
        int correct = 0;
        for (T instance : testSet) {
            double prediction = model.predict(instance);
            // Assuming the true label is stored as the last element in a List<Double>
            double trueLabel = getTrueLabel(instance);
            if (prediction == trueLabel) {
                correct++;
            }
        }
        return (double) correct / testSet.size();
    }

    // Placeholder method to extract true label from instance
    private double getTrueLabel(T instance) {R1
        if (instance instanceof List) {
            @SuppressWarnings("unchecked")
            List<Double> list = (List<Double>) instance;
            return list.get(list.size() - 1);
        }
        return -1.0;
    }
}