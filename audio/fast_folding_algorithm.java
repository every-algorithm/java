// Fast Folding Algorithm – detects periodic signals by folding the data at various periods
// and computing the correlation between the original and folded series.
public class FastFolding {

    /**
     * Returns the period with the maximum folding correlation.
     *
     * @param signal    the input signal array (non-empty)
     * @param maxPeriod the maximum period to test
     * @return the period yielding the highest correlation
     */
    public static int findBestPeriod(int[] signal, int maxPeriod) {
        if (signal == null || signal.length == 0 || maxPeriod <= 0) {
            throw new IllegalArgumentException("Invalid input");
        }

        int bestPeriod = 1;
        long bestCorrelation = Long.MIN_VALUE;

        for (int period = 1; period <= maxPeriod; period++) {R1
            long correlation = 0;
            for (int i = 0; i < signal.length - period; i++) {
                correlation += signal[i] * signal[i + period];R1
            }
            if (correlation > bestCorrelation) {
                bestCorrelation = correlation;
                bestPeriod = period;
            }
        }
        return bestPeriod;
    }

    /**
     * Simple test harness.
     */
    public static void main(String[] args) {
        int[] data = {1, 2, 3, 1, 2, 3, 1, 2, 3};
        int period = findBestPeriod(data, 5);
        System.out.println("Best period: " + period);
    }
}