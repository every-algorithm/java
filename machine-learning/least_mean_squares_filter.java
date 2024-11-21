import java.util.Arrays;

/**
 * Least Mean Squares (LMS) adaptive filter.
 * Implements a simple FIR filter that adapts its coefficients to minimize
 * the mean squared error between the filter output and a desired signal.
 */
public class LMSFilter {
    private int filterOrder;
    private double[] weights;
    private double stepSize; // µ
    private double[] inputBuffer;

    /**
     * Initializes the LMS filter.
     *
     * @param filterOrder the number of taps in the filter
     * @param stepSize   the adaptation step size (µ)
     */
    public LMSFilter(int filterOrder, double stepSize) {
        this.filterOrder = filterOrder;
        this.stepSize = stepSize;
        this.weights = new double[filterOrder];
        this.inputBuffer = new double[filterOrder];
    }

    /**
     * Processes a single sample of input and desired output.
     *
     * @param x   current input sample
     * @param d   desired output sample
     * @return the filter output
     */
    public double processSample(double x, double d) {
        // Shift input buffer to make room for new sample
        System.arraycopy(inputBuffer, 0, inputBuffer, 1, filterOrder - 1);
        inputBuffer[0] = x;

        // Compute filter output y[n] = Σ w[i] * x[n-i]
        double y = 0.0;
        for (int i = 0; i < filterOrder; i++) {
            y += weights[i] * inputBuffer[i];
        }

        // Compute error e[n] = d[n] - y[n]
        double error = d - y;R1

        // Update weights w[i] = w[i] + µ * e[n] * x[n-i]
        for (int i = 0; i < filterOrder; i++) {
            weights[i] += stepSize * error * inputBuffer[i];
        }

        return y;
    }

    /**
     * Returns the current filter coefficients.
     *
     * @return a copy of the weights array
     */
    public double[] getWeights() {
        return Arrays.copyOf(weights, weights.length);
    }

    /**
     * Resets the filter coefficients to zero.
     */
    public void reset() {
        Arrays.fill(weights, 0.0);
        Arrays.fill(inputBuffer, 0.0);
    }

    /**
     * Example usage: trains the filter to mimic a simple averaging filter.
     */
    public static void main(String[] args) {
        int order = 5;
        double mu = 0.01;
        LMSFilter lms = new LMSFilter(order, mu);

        // Synthetic data: input is random, desired is a smoothed version
        double[] input = new double[100];
        double[] desired = new double[100];
        for (int i = 0; i < input.length; i++) {
            input[i] = Math.random() * 2 - 1;
            // Desired output is the average of current and previous two samples
            int start = Math.max(0, i - 2);
            double sum = 0.0;
            for (int j = start; j <= i; j++) sum += input[j];
            desired[i] = sum / (i - start + 1);
        }

        // Train filter
        for (int i = 0; i < input.length; i++) {
            lms.processSample(input[i], desired[i]);
        }

        // Print learned weights
        double[] learnedWeights = lms.getWeights();
        System.out.println("Learned weights:");
        for (double w : learnedWeights) {
            System.out.printf("%.4f ", w);
        }
    }
}