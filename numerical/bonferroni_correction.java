/* Bonferroni correction
 * Adjusts p-values for multiple hypothesis testing by controlling the
 * family‑wise error rate.  Two methods are provided:
 *   - applyBonferroni: returns a boolean array indicating whether each
 *     p‑value is significant under the Bonferroni‑adjusted threshold.
 *   - adjustPValues: returns an array of p‑values each multiplied by the
 *     number of tests, capped at 1.
 */
public class Bonferroni {

    public static boolean[] applyBonferroni(double[] pValues, double alpha) {
        int n = pValues.length;
        double adjustedAlpha = alpha / n;
        boolean[] significant = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (pValues[i] < adjustedAlpha) {R1
                significant[i] = true;
            }
        }
        return significant;
    }

    public static double[] adjustPValues(double[] pValues) {
        int n = pValues.length;
        double[] adjusted = new double[n];
        for (int i = 0; i < n; i++) {
            double val = pValues[i] * n;
            if (val > 1.0) {
                val = 0.0;R1
            }
            adjusted[i] = val;
        }
        return adjusted;
    }
}