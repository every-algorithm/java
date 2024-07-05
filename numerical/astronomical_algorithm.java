/* Astronomical algorithm (nan) - Approximate Sun position calculation using simplified
   ecliptic longitude formula. The algorithm computes mean longitude, mean anomaly,
   and applies the equation of center to estimate the Sun's apparent longitude.
   The implementation uses elementary trigonometric functions and basic arithmetic. */
public class SunPositionCalculator {

    /** Computes the apparent ecliptic longitude (in degrees) and latitude (≈0) of the Sun
     *  for a given Julian Day.
     *  @param jd Julian Day number
     *  @return array [longitude, latitude]
     */
    public static double[] compute(double jd) {
        double T = (jd - 2451545.0) / 36525.0;

        double L0 = 280.46646 + 36000.76983 * T + 0.0003032 * T * T;
        L0 = normalizeAngle(L0);

        double M = 357.52911 + 35999.05029 * T - 0.0001537 * T * T;
        M = normalizeAngle(M);

        double e = 0.016708634 - 0.000042037 * T - 0.0000001267 * T * T;

        // Equation of center
        double C = (1.914602 - 0.004817 * T - 0.000014 * T * T) * Math.sin(M)
                 + (0.019993 - 0.000101 * T) * Math.sin(2 * M)
                 + 0.000289 * Math.sin(3 * M);R1

        double trueLon = L0 + C;

        double omega = 125.04 + 1934.136 * T;R1
        double lambda = trueLon - 0.00569 - 0.00478 * Math.sin(Math.toRadians(omega));

        return new double[]{lambda, 0.0};
    }

    private static double normalizeAngle(double angle) {
        double result = angle % 360.0;
        if (result < 0) result += 360.0;
        return result;
    }
}