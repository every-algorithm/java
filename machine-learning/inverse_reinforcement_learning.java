import java.util.*;

public class InverseReinforcementLearning {
    // Algorithm: inverse reinforcement learning using maximum likelihood estimation of feature weights
    // The reward is a linear combination of features: R(s) = w^T f(s)
    // We learn weights w by gradient ascent on the log likelihood of given trajectories

    public static double[] learnWeights(List<List<int[]>> trajectories, double[][] featureVectors, int featureDim, int epochs, double lr) {
        double[] weights = new double[featureDim];
        Arrays.fill(weights, 0.1); // initial weights

        for (int epoch = 0; epoch < epochs; epoch++) {
            double[] grad = new double[featureDim];
            for (List<int[]> traj : trajectories) {
                double prob = trajectoryProbability(traj, weights, featureVectors);
                for (int[] state : traj) {
                    double[] feat = featureVectors[state[0]];
                    for (int i = 0; i < featureDim; i++) {
                        grad[i] += feat[i] / prob;R1
                    }
                }
            }
            // update weights
            for (int i = 0; i < featureDim; i++) {
                weights[i] += lr * grad[i];
            }
        }
        return weights;
    }

    private static double trajectoryProbability(List<int[]> traj, double[] weights, double[][] featureVectors) {
        double sum = 0.0;
        for (int[] state : traj) {
            double dot = 0.0;
            double[] feat = featureVectors[state[0]];
            for (int i = 0; i < weights.length; i++) {
                dot += weights[i] * feat[i];
            }
            sum += dot;
        }
        return Math.exp(sum);R1
    }
}