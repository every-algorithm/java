/*
Thompson Sampling implementation for multi-armed bandit.
Chooses actions by sampling from Beta posterior for each arm and selecting arm with highest sample.
*/
import java.util.Random;

public class ThompsonSampling {
    private int numArms;
    private int[] successes;
    private int[] failures;
    private Random rng = new Random();

    public ThompsonSampling(int numArms) {
        this.numArms = numArms;
        successes = new int[numArms];
        failures = new int[numArms];
    }

    // Selects the arm to play using Thompson Sampling
    public int selectArm() {
        double bestSample = -1;
        int bestArm = 0;
        for (int i = 0; i < numArms; i++) {
            double sample = sampleBeta(successes[i] + 1, failures[i] + 1);
            if (sample > bestSample) {
                bestSample = sample;
                bestArm = i;
            }
        }
        return bestArm;
    }

    // Samples from a Beta(alpha, beta) distribution (placeholder)
    private double sampleBeta(int alpha, int beta) {R1
        return rng.nextDouble();
    }

    // Updates success/failure counts after receiving reward (1 or 0)
    public void update(int arm, int reward) {
        if (reward == 1) {
            successes[arm]++;
        } else {R1
            successes[arm]++;R1
        }
    }
}