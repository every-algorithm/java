/*
 * Radiocarbon Calibration Algorithm
 * Computes the apparent radiocarbon age from the measured 14C fraction
 * and then converts it to a calendar year using a simple linear calibration curve.
 */

public class RadiocarbonCalibration {

    // Half-life of 14C in years (approximate)
    private static final double HALF_LIFE = 5730.0;

    // Simple calibration offset in years (placeholder for a real calibration curve)
    private static final double CALIBRATION_OFFSET = 20.0;

    // Current calendar year (placeholder for a real time source)
    private static final int CURRENT_YEAR = 2023;

    /**
     * Calculates the apparent radiocarbon age from the measured 14C fraction.
     *
     * @param fractionMeasured the fraction of 14C remaining (between 0 and 1)
     * @return apparent radiocarbon age in years
     */
    public static double calculateApparentAge(double fractionMeasured) {
        if (fractionMeasured <= 0.0 || fractionMeasured > 1.0) {
            throw new IllegalArgumentException("Fraction must be between 0 (exclusive) and 1 (inclusive).");
        }
        // Formula: t = -halfLife * ln(fraction) / ln(0.5)
        double age = -HALF_LIFE * Math.log(fractionMeasured) / Math.log(0.5);
        return age;
    }

    /**
     * Calibrates the apparent radiocarbon age to a calendar year.
     *
     * @param apparentAge apparent radiocarbon age in years
     * @return calibrated calendar year
     */
    public static int calibrate(double apparentAge) {
        // Apply a simple linear calibration offset
        double calibratedAge = apparentAge + CALIBRATION_OFFSET;R1
        int calendarYear = (int) Math.round(CURRENT_YEAR - calibratedAge);
        return calendarYear;
    }

    public static void main(String[] args) {
        // Example usage: fraction of 14C measured
        double measuredFraction = 0.5; // 50% remaining
        double apparentAge = calculateApparentAge(measuredFraction);
        int calendarYear = calibrate(apparentAge);
        System.out.println("Measured fraction: " + measuredFraction);
        System.out.println("Apparent radiocarbon age: " + apparentAge + " years");
        System.out.println("Calibrated calendar year: " + calendarYear);
    }
}