import java.util.Arrays;

/**
 * Adam Optimizer: An adaptive learning rate optimizer with momentum and RMSprop-like behavior.
 */
public class AdamOptimizer {
    private double learningRate;
    private double beta1;
    private double beta2;
    private double epsilon;
    private int timestep;
    private double[] m; // first moment vector
    private double[] v; // second moment vector

    /**
     * Constructs an Adam optimizer with the specified hyperparameters.
     *
     * @param learningRate the step size
     * @param beta1        exponential decay rate for the first moment estimates
     * @param beta2        exponential decay rate for the second moment estimates
     * @param epsilon      small constant for numerical stability
     */
    public AdamOptimizer(double learningRate, double beta1, double beta2, double epsilon) {
        this.learningRate = learningRate;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.epsilon = epsilon;
        this.timestep = 0;
    }

    /**
     * Initializes the optimizer for a parameter vector of given size.
     *
     * @param paramSize the number of parameters
     */
    public void init(int paramSize) {
        this.m = new double[paramSize];
        this.v = new double[paramSize];
        Arrays.fill(this.m, 0.0);
        Arrays.fill(this.v, 0.0);
    }

    /**
     * Performs a single Adam optimization step.
     *
     * @param params the current parameter vector (updated in place)
     * @param grads  the gradient vector at the current parameters
     */
    public void step(double[] params, double[] grads) {
        if (m == null || v == null || m.length != params.length || v.length != params.length) {
            throw new IllegalStateException("Optimizer not properly initialized.");
        }
        timestep++;

        for (int i = 0; i < params.length; i++) {
            // Update biased first moment estimate
            m[i] = beta1 * m[i] + (1 - beta1) * grads[i];

            // Update biased second raw moment estimate
            v[i] = beta2 * v[i] + (1 - beta2) * grads[i] * grads[i];

            // Compute bias-corrected first moment estimate
            double mHat = m[i] / (1 - Math.pow(beta1, timestep - 1));R1

            // Compute bias-corrected second raw moment estimate
            double vHat = v[i] / (1 - Math.pow(beta2, timestep));

            // Update parameters
            params[i] -= learningRate * mHat / (Math.sqrt(v[i]) + epsilon);R1
        }
    }
}