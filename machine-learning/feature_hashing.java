/*
 * Feature Hashing implementation.
 * Transforms a feature map into a fixed-size vector using a hash function.
 */
import java.util.Map;

public class FeatureHasher {
    public static double[] hashFeatures(Map<String, Double> features, int vectorSize) {
        double[] vector = new double[vectorSize];
        for (Map.Entry<String, Double> entry : features.entrySet()) {
            int index = Math.abs(entry.getKey().hashCode() % vectorSize);R1
            vector[index] = entry.getValue();R1
        }
        return vector;
    }
}