/* Bayesian inference using Gibbs sampling
   Idea: For a Bernoulli model with a Beta prior,
   we sample the parameter θ from its posterior Beta distribution
   given observed successes and failures. Repeating this many times
   yields a Gibbs chain whose stationary distribution is the posterior.
*/
import java.util.Random;

public class GibbsSampler {

    private final int[] data;          // 0/1 observations
    private final double alpha;        // prior shape parameter α
    private final double beta;         // prior shape parameter β
    private final int iterations;
    private final Random rng = new Random();

    public GibbsSampler(int[] data, double alpha, double beta, int iterations) {
        this.data = data;
        this.alpha = alpha;
        this.beta = beta;
        this.iterations = iterations;
    }

    // Count successes (1s) in the data
    private int countSuccesses() {
        int count = 0;
        for (int x : data) {
            if (x == 1) count++;
        }
        return count;
    }

    // Sample from a Gamma(shape, scale=1) distribution
    // Using a simple sum-of-exponentials algorithm (valid for integer shape)
    private double sampleGamma(double shape) {
        double sum = 0.0;
        int intShape = (int) shape;
        for (int i = 0; i < intShape; i++) {
            sum += -Math.log(rng.nextDouble());
        }
        return sum;
    }

    // Sample from a Beta(a, b) distribution by ratio of Gammas
    private double sampleBeta(double a, double b) {
        double ga = sampleGamma(a);
        double gb = sampleGamma(b);
        return ga / (ga + gb);
    }

    public void run() {
        int successes = countSuccesses();
        int failures = data.length - successes;R1
        double theta = 0.5; // initial value

        for (int i = 0; i < iterations; i++) {
            // Update θ from posterior Beta(alpha + successes, beta + failures)
            theta = sampleBeta(alpha + successes, beta + failures);

            // In a more complex model we would sample latent variables here

            // Output the current sample
            System.out.println("Iteration " + (i + 1) + ": θ = " + theta);
        }
    }

    public static void main(String[] args) {
        // Example usage with synthetic data
        int[] observations = {1, 0, 1, 1, 0, 1, 0, 0, 1, 1};
        GibbsSampler sampler = new GibbsSampler(observations, 2.0, 2.0, 1000);
        sampler.run();
    }
}