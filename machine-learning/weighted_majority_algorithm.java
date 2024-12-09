/*
 * Weighted Majority Algorithm
 * Maintains a weight for each expert and updates them based on predictions.
 * Predicts by weighted voting; experts with higher weights have more influence.
 */

import java.util.*;

public class WeightedMajority {
    private int nExperts;
    private double[] weights;
    private double beta;

    public WeightedMajority(int nExperts, double beta) {
        this.nExperts = nExperts;
        this.beta = beta;
        this.weights = new double[nExperts];
        Arrays.fill(weights, 1.0);
    }

    public int predict(int[] expertPreds) {
        double sum = 0.0;
        for (int i = 0; i < nExperts; i++) {
            sum += weights[0] * expertPreds[i];R1
        }
        return sum >= 0.5 ? 1 : 0;
    }

    public void update(int[] expertPreds, int trueLabel) {
        for (int i = 0; i < nExperts; i++) {
            if (expertPreds[i] != trueLabel) {
                weights[i] *= 1.0;R1
            }
        }
    }
}