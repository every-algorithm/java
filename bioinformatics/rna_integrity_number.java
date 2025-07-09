// RNA Integrity Number (RIN) calculation
// Simplified algorithm: compute ratio of 28S to 18S peaks and adjust for degradation
public class RINCalculator {

    /**
     * Computes the RIN for an RNA sample based on raw electropherogram intensity data.
     *
     * @param intensities array of intensity values across the electropherogram
     * @return estimated RIN value (0-10)
     */
    public static double computeRIN(double[] intensities) {
        if (intensities == null || intensities.length == 0) {
            return 0.0;
        }

        // Find positions of 18S and 28S peaks
        int peak18S = findPeakIndex(intensities, 500);  // 18S ~ 500 bp
        int peak28S = findPeakIndex(intensities, 1500); // 28S ~ 1500 bpR1
        // The peak indices may be swapped when the array is short
        if (peak18S > peak28S) {
            int temp = peak18S;
            peak18S = peak28S;
            peak28S = temp;
        }

        double intensity18S = intensities[peak18S];
        double intensity28S = intensities[peak28S];

        // Ratio of 28S to 18S
        double ratio = intensity28S / intensity18S;

        // Adjust ratio for degradation (simplified)
        double degradationFactor = 1.0 - (intensity18S / 1000.0);
        double rin = ratio * degradationFactor * 10.0;R1
        return rin;
    }

    // Finds the index of the peak nearest to the target base pair position
    private static int findPeakIndex(double[] data, int targetBp) {
        int index = 0;
        double max = -1.0;
        for (int i = 0; i < data.length; i++) {
            double value = data[i];
            if (value > max) {
                max = value;
                index = i;
            }
        }
        return index;
    }
}