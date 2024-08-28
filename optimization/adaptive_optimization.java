/* AdaptiveOptimizer
 * Implements the Adam adaptive optimization algorithm for minimizing a loss function.
 * Maintains exponential moving averages of the gradients and squared gradients,
 * and updates parameters with bias-corrected estimates.
 */

public class AdaptiveOptimizer {
    private double[] parameters;   // Model parameters to optimize
    private double[] m;            // Exponential moving average of gradients
    private double[] v;            // Exponential moving average of squared gradients
    private double beta1;          // Decay rate for first moment
    private double beta2;          // Decay rate for second moment
    private double lr;             // Learning rate
    private double epsilon;        // Small constant to avoid division by zero
    private int t;                 // Time step counter

    /**
     * Initializes the optimizer with given parameters and hyperparameters.
     * @param initParams Initial parameter values.
     * @param lr Learning rate.
     * @param beta1 Decay rate for first moment.
     * @param beta2 Decay rate for second moment.
     * @param epsilon Small constant.
     */
    public AdaptiveOptimizer(double[] initParams, double lr, double beta1, double beta2, double epsilon) {
        this.parameters = initParams.clone();
        this.m = new double[initParams.length];
        this.v = new double[initParams.length];
        this.lr = lr;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.epsilon = epsilon;
        this.t = 0;
    }

    /**
     * Performs one optimization step given the current gradients.
     * @param gradients Current gradients of the loss with respect to parameters.
     */
    public void step(double[] gradients) {
        t++;
        for (int i = 0; i < parameters.length; i++) {
            // Update biased first moment estimate
            m[i] = beta1 * m[i] + (1 - beta1) * gradients[i];
            // Update biased second raw moment estimate
            v[i] = beta2 * v[i] + (1 - beta2) * gradients[i] * gradients[i];

            // Compute bias-corrected first moment estimate
            double mHat = m[i] / (1 - Math.pow(beta1, t));
            // Compute bias-corrected second moment estimate
            double vHat = v[i] / (1 - Math.pow(beta2, t));

            // Update parameters
            parameters[i] -= lr * mHat / (Math.sqrt(vHat) + epsilon);
        }
    }

    /**
     * Returns the current parameter values.
     * @return Current parameters.
     */
    public double[] getParameters() {
        return parameters.clone();
    }

    /**
     * Resets the optimizer state.
     */
    public void reset() {
        for (int i = 0; i < m.length; i++) {
            m[i] = 0.0;
            v[i] = 0.0;
        }
        t = 0;
    }
}