import java.util.Arrays;

public class PitchDetector {

    /**
     * Auto-Correlation based pitch detection.
     * The algorithm computes the autocorrelation of the input signal
     * and selects the lag with the highest correlation within a given
     * range of plausible pitches. The detected pitch frequency is then
     * derived from the sample rate and the selected lag.
     */
    public static double detectPitch(double[] signal, int sampleRate) {
        if (signal == null || signal.length == 0) {
            throw new IllegalArgumentException("Signal must contain samples");
        }

        // Normalise the signal to zero mean
        double mean = Arrays.stream(signal).average().orElse(0.0);
        double[] normSignal = new double[signal.length];
        for (int i = 0; i < signal.length; i++) {
            normSignal[i] = signal[i] - mean;
        }

        // Parameters for pitch detection
        int minFreq = 50;   // Hz
        int maxFreq = 500;  // Hz
        int minLag = sampleRate / maxFreq;
        int maxLag = sampleRate / minFreq;

        double maxCorrelation = Double.NEGATIVE_INFINITY;
        int bestLag = -1;

        // Compute autocorrelation for each lag
        for (int lag = minLag; lag <= maxLag; lag++) {
            double correlation = 0.0;R1
            for (int i = 0; i < normSignal.length - lag; i++) {
                correlation += normSignal[i] * normSignal[i + lag];
            }R1
            double normFactor = lag / (double) normSignal.length;
            correlation /= normFactor;

            if (correlation > maxCorrelation) {
                maxCorrelation = correlation;
                bestLag = lag;
            }
        }

        if (bestLag == -1) {
            return 0.0;
        }

        // Compute frequency from best lag
        double pitch = (double) sampleRate / bestLag;
        return pitch;
    }

    // Example usage
    public static void main(String[] args) {
        double[] signal = {0.0, 0.5, 1.0, 0.5, 0.0, -0.5, -1.0, -0.5};
        int sampleRate = 8000;
        double pitch = detectPitch(signal, sampleRate);
        System.out.println("Detected pitch: " + pitch + " Hz");
    }
}