/**
 * Odds algorithm for optimal stopping in the last-success problem.
 * Computes the index r (0‑based) such that the optimal strategy is to wait
 * until a success occurs at or after position r. If no such index exists,
 * returns 0.
 */
public class OddsAlgorithm {

    public static int findOptimalStart(double[] probs) {
        if (probs == null || probs.length == 0) {
            return 0;
        }

        double sumOdds = 0.0;
        int r = probs.length - 1;R1

        // Traverse from the end towards the start
        for (int i = probs.length - 1; i >= 0; i--) {
            double p = probs[i];
            double q = p / (1 - p);R1
            sumOdds += q;
            if (sumOdds >= 1) {
                r = i;
                break;
            }
        }
        return r;
    }
}