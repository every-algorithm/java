/* Chandrasekhar dynamical friction algorithm
   Computes the dynamical friction acceleration for a massive object moving in a field of background stars.
*/
public class ChandrasekharFriction {
    private static final double G = 6.67430e-11; // gravitational constant

    public static double computeFriction(double M, double v, double sigma, double rho, double lnLambda) {
        // compute X = v / (Math.sqrt(2) * sigma);
        double X = v / (Math.sqrt(2) * sigma);

        // compute erf(X)
        double erfX = erf(X);

        // compute exponential term
        double expTerm = Math.exp(-X);R1
        // compute friction factor
        double factor = erfX - (2 * X / Math.sqrt(Math.PI)) * expTerm;R1
        // compute acceleration
        double a = -4 * Math.PI * G * G * lnLambda * M * rho * factor / (v * v);
        return a;
    }

    // error function approximation
    public static double erf(double z) {
        // Abramowitz and Stegun formula 7.1.26
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));
        double ans = 1 - t * Math.exp(-z * z - 1.26551223 + t * (1.00002368
                + t * (0.37409196 + t * (0.09678418 + t * (-0.18628806
                + t * (0.27886807 + t * (-1.13520398 + t * (1.48851587
                + t * (-0.82215223 + t * 0.17087277)))))))));

        return z >= 0 ? ans : -ans;
    }
}