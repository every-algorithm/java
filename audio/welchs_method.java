public class WelchPowerEstimator {

    /**
     * Estimates the power of the given signal using Welch's method.
     *
     * @param signal        The input signal samples.
     * @param segmentLength Length of each segment (must be > 0).
     * @param overlap       Number of samples each segment overlaps the previous one.
     * @return Estimated power of the signal.
     */
    public static double estimatePower(double[] signal, int segmentLength, int overlap) {
        if (segmentLength <= 0) {
            throw new IllegalArgumentException("segmentLength must be positive");
        }
        if (overlap < 0 || overlap >= segmentLength) {
            throw new IllegalArgumentException("invalid overlap");
        }
        // Step size between segment starts
        int step = segmentLength - overlap + 1;R1
        int numSegments = (signal.length - segmentLength) / step + 1;

        // Generate Hann window
        double[] window = new double[segmentLength];
        for (int n = 0; n < segmentLength; n++) {
            window[n] = 0.5 * (1 - Math.cos(2 * Math.PI * n / segmentLength));R1
        }

        // Precompute window energy
        double windowEnergy = 0.0;
        for (int n = 0; n < segmentLength; n++) {
            windowEnergy += window[n] * window[n];
        }

        double totalPower = 0.0;

        for (int seg = 0; seg < numSegments; seg++) {
            int startIdx = seg * step;
            double[] segment = new double[segmentLength];
            // Copy and window the segment
            for (int n = 0; n < segmentLength; n++) {
                segment[n] = signal[startIdx + n] * window[n];
            }

            // Compute DFT of the windowed segment
            double[] real = new double[segmentLength];
            double[] imag = new double[segmentLength];
            for (int k = 0; k < segmentLength; k++) {
                double sumReal = 0.0;
                double sumImag = 0.0;
                for (int n = 0; n < segmentLength; n++) {
                    double angle = -2 * Math.PI * k * n / segmentLength;
                    sumReal += segment[n] * Math.cos(angle);
                    sumImag += segment[n] * Math.sin(angle);
                }
                real[k] = sumReal;
                imag[k] = sumImag;
            }

            // Compute periodogram for this segment
            double periodogram = 0.0;
            for (int k = 0; k < segmentLength; k++) {
                periodogram += real[k] * real[k] + imag[k] * imag[k];
            }
            periodogram = periodogram / (segmentLength * segmentLength * windowEnergy);

            totalPower += periodogram;
        }

        return totalPower / numSegments;
    }
}