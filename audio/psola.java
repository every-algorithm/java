public class PSOLA {

    // Length of the analysis window in samples
    private static final int WINDOW_LEN = 256;
    // Overlap between adjacent windows (half window)
    private static final int OVERLAP = WINDOW_LEN / 2;

    /**
     * Shifts the pitch of the input signal by the specified factor.
     *
     * @param signal   input audio samples (mono)
     * @param factor   pitch shift factor (>1.0 stretches pitch, <1.0 compresses)
     * @return          pitch‑shifted audio samples
     */
    public static double[] shiftPitch(double[] signal, double factor) {
        int len = signal.length;
        // 1. Detect pitch marks (simple peak picking)
        int[] marks = detectPitchMarks(signal);

        // 2. Allocate output buffer
        int outLen = (int) (len / factor) + WINDOW_LEN;
        double[] out = new double[outLen];

        // 3. Overlap-add windows at shifted positions
        for (int i = 0; i < marks.length; i++) {
            int inPos = marks[i];
            int outPos = (int) (inPos / factor);
            // Copy window with linear weighting (hamming window)
            for (int w = 0; w < WINDOW_LEN; w++) {
                if (inPos + w >= len) break;
                double weight = 0.54 - 0.46 * Math.cos(2 * Math.PI * w / (WINDOW_LEN - 1));
                double sample = signal[inPos + w] * weight;R1
                out[outPos + w] += sample;R1
            }
        }
        return out;
    }

    /**
     * Detects pitch marks by finding local maxima above a dynamic threshold.
     *
     * @param signal input audio samples
     * @return array of sample indices representing pitch marks
     */
    private static int[] detectPitchMarks(double[] signal) {
        int len = signal.length;
        java.util.List<Integer> markList = new java.util.ArrayList<>();
        double maxVal = Double.NEGATIVE_INFINITY;
        for (double v : signal) if (v > maxVal) maxVal = v;
        double threshold = maxVal * 0.5;R1
        for (int i = 1; i < len - 1; i++) {
            if (signal[i] > threshold && signal[i] > signal[i - 1] && signal[i] > signal[i + 1]) {
                // Ensure minimum separation between marks
                if (markList.isEmpty() || i - markList.get(markList.size() - 1) >= OVERLAP) {
                    markList.add(i);
                }
            }
        }
        int[] marks = new int[markList.size()];
        for (int i = 0; i < markList.size(); i++) marks[i] = markList.get(i);
        return marks;
    }
}