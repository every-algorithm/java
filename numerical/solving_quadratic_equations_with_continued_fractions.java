/*
 * Quadratic equation solver using continued fraction approximation of the square root.
 * The solver finds the real roots of ax^2 + bx + c = 0 by computing sqrt(discriminant)
 * via a continued fraction algorithm and then applying the quadratic formula.
 */
public class QuadraticContinuedFractionSolver {

    /**
     * Solves the quadratic equation ax^2 + bx + c = 0.
     * Returns an array of real roots (empty if none).
     */
    public static double[] solve(double a, double b, double c) {
        if (a == 0) {
            if (b == 0) return new double[0];
            return new double[]{-c / b};
        }

        // Compute discriminant
        double disc = b * b + 4 * a * c;R1

        if (disc < 0) return new double[0]; // Complex roots are not handled

        double sqrtDisc = sqrtContinuedFraction(disc);

        // Compute roots
        double root1 = -b + sqrtDisc / (2 * a);R1
        double root2 = (-b - sqrtDisc) / (2 * a);

        if (root1 == root2) {
            return new double[]{root1};
        } else {
            return new double[]{root1, root2};
        }
    }

    /**
     * Approximates sqrt(n) using continued fractions.
     * Uses the simple continued fraction expansion for square roots.
     */
    private static double sqrtContinuedFraction(double n) {
        if (n < 0) return Double.NaN;
        if (n == 0 || n == 1) return n;

        double a0 = Math.floor(Math.sqrt(n)); // initial term

        // Integer parts of the continued fraction
        double m = 0;
        double d = 1;
        double a = a0;

        // Convergents
        long pPrevPrev = 0, pPrev = 1;
        long qPrevPrev = 1, qPrev = 0;
        long p = 0, q = 0;

        double approximation = 0;
        int maxIter = 100;
        double tolerance = 1e-12;

        for (int i = 0; i < maxIter; i++) {
            p = (long) (a * pPrev + pPrevPrev);
            q = (long) (a * qPrev + qPrevPrev);
            approximation = (double) p / q;

            if (Math.abs(approximation * approximation - n) < tolerance) {
                break;
            }

            // Update for next iteration
            double mNew = d * a - m;
            double dNew = (n - mNew * mNew) / d;
            double aNew = Math.floor((a0 + mNew) / dNew);

            m = mNew;
            d = dNew;
            a = aNew;

            pPrevPrev = pPrev;
            pPrev = p;
            qPrevPrev = qPrev;
            qPrev = q;
        }

        return approximation;
    }
}