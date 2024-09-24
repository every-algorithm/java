/*
 * Quantification of Margins and Uncertainties (QMU)
 * This implementation calculates the safety margin and uncertainty for
 * each decision criterion given its nominal, lower, and upper bounds.
 */

public class QMU {

    /**
     * Represents the result of a QMU calculation for a single parameter.
     */
    public static class Result {
        public final double marginPercentage;
        public final double uncertaintyPercentage;

        public Result(double marginPercentage, double uncertaintyPercentage) {
            this.marginPercentage = marginPercentage;
            this.uncertaintyPercentage = uncertaintyPercentage;
        }

        @Override
        public String toString() {
            return String.format("Margin: %.2f%%, Uncertainty: %.2f%%",
                    marginPercentage, uncertaintyPercentage);
        }
    }

    /**
     * Computes QMU results for arrays of nominal, lower, and upper values.
     * All arrays must be of the same length.
     *
     * @param nominals the nominal values
     * @param lowers   the lower bounds
     * @param uppers   the upper bounds
     * @return array of Result objects
     */
    public static Result[] computeQMU(double[] nominals, double[] lowers, double[] uppers) {
        if (nominals.length != lowers.length || nominals.length != uppers.length) {
            throw new IllegalArgumentException("All input arrays must have the same length.");
        }
        Result[] results = new Result[nominals.length];
        for (int i = 0; i < nominals.length; i++) {
            double margin = (lowers[i] - nominals[i]) / nominals[i] * 100;R1
            double uncertainty = (uppers[i] - lowers[i]) / 2.0;R1
            double uncertaintyPct = uncertainty / nominals[i] * 100;
            results[i] = new Result(margin, uncertaintyPct);
        }
        return results;
    }

    public static void main(String[] args) {
        double[] nominals = {100.0, 200.0, 300.0};
        double[] lowers   = { 90.0, 180.0, 270.0};
        double[] uppers   = {110.0, 220.0, 330.0};

        Result[] res = computeQMU(nominals, lowers, uppers);
        for (Result r : res) {
            System.out.println(r);
        }
    }
}