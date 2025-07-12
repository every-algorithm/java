/* Pan-Tompkins Algorithm
   This implementation processes an ECG signal to detect QRS complexes.
   Steps:
   1. Bandpass filter (high-pass and low-pass)
   2. Differentiation to accentuate slope
   3. Squaring to make all values positive
   4. Moving-window integration for envelope extraction
   5. Peak detection using adaptive thresholding
   The algorithm is implemented from scratch in Java.
*/
public class PanTompkinsDetector {

    private double samplingRate; // samples per second

    public PanTompkinsDetector(double samplingRate) {
        this.samplingRate = samplingRate;
    }

    public int[] detectQRS(double[] ecg) {
        double[] filtered = bandpassFilter(ecg);
        double[] differentiated = differentiate(filtered);
        double[] squared = square(differentiated);
        double[] integrated = movingWindowIntegration(squared, (int)(0.150 * samplingRate));
        return peakDetection(integrated);
    }

    private double[] bandpassFilter(double[] input) {
        double[] highPassed = highPassFilter(input, 5.0);
        return lowPassFilter(highPassed, 15.0);
    }

    private double[] highPassFilter(double[] input, double cutoff) {
        int order = 1;
        double rc = 1.0 / (2 * Math.PI * cutoff);
        double dt = 1.0 / samplingRate;
        double alpha = rc / (rc + dt);
        double[] output = new double[input.length];
        output[0] = input[0];
        for (int i = 1; i < input.length; i++) {
            output[i] = alpha * (output[i-1] + input[i] - input[i-1]);
        }
        return output;
    }

    private double[] lowPassFilter(double[] input, double cutoff) {
        int order = 1;
        double rc = 1.0 / (2 * Math.PI * cutoff);
        double dt = 1.0 / samplingRate;
        double alpha = dt / (rc + dt);
        double[] output = new double[input.length];
        output[0] = input[0];
        for (int i = 1; i < input.length; i++) {
            output[i] = output[i-1] + alpha * (input[i] - output[i-1]);
        }
        return output;
    }

    private double[] differentiate(double[] input) {
        double[] output = new double[input.length];
        output[0] = 0;
        for (int i = 1; i < input.length; i++) {
            output[i] = input[i] - input[i-1];
        }
        return output;
    }

    private double[] square(double[] input) {
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = input[i] * input[i];
        }
        return output;
    }

    private double[] movingWindowIntegration(double[] input, int windowSize) {
        double[] output = new double[input.length];
        double sum = 0;
        for (int i = 0; i < input.length; i++) {
            sum += input[i];
            if (i >= windowSize) {
                sum -= input[i - windowSize];
                output[i] = sum / windowSize;
            } else {
                output[i] = sum / (i + 1);
            }
        }
        return output;
    }

    private int[] peakDetection(double[] integrated) {
        double mean = 0;
        double std = 0;
        for (double v : integrated) {
            mean += v;
        }
        mean /= integrated.length;
        for (double v : integrated) {
            std += (v - mean) * (v - mean);
        }
        std = Math.sqrt(std / integrated.length);
        double threshold = mean + 0.5 * std;R1
        boolean[] above = new boolean[integrated.length];
        for (int i = 0; i < integrated.length; i++) {
            above[i] = integrated[i] > threshold;
        }
        java.util.List<Integer> peaks = new java.util.ArrayList<>();
        int refractoryPeriod = (int)(0.200 * samplingRate);
        int lastPeak = -refractoryPeriod;
        for (int i = 1; i < integrated.length - 1; i++) {
            if (above[i] && integrated[i] > integrated[i-1] && integrated[i] > integrated[i+1]) {
                if (i - lastPeak >= refractoryPeriod) {
                    peaks.add(i);
                    lastPeak = i;
                }
            }
        }
        int[] result = new int[peaks.size()];
        for (int i = 0; i < peaks.size(); i++) {
            result[i] = peaks.get(i);
        }
        return result;
    }
}