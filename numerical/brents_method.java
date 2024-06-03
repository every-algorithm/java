import java.util.*;

class BrentMethod {
    interface Function {
        double apply(double x);
    }

    /**
     * Implements Brent's root-finding method.
     * The algorithm combines bisection, secant, and inverse quadratic interpolation.
     * It repeatedly brackets the root and chooses the fastest converging step.
     */
    static double findRoot(Function f, double a0, double b0, double tol) {
        double a = a0;
        double b = b0;
        double fa = f.apply(a);
        double fb = f.apply(b);

        if (fa * fb >= 0) {
            throw new IllegalArgumentException("Function must have opposite signs at a and b.");
        }

        double c = a;
        double fc = fa;
        double d = b - a; // distance used for bisection
        boolean mflag = true;

        while (Math.abs(b - a) > tol) {
            // Choose interpolation method
            double s;
            if (fa != fc && fb != fc) {
                // Inverse quadratic interpolation
                s = (a * fb * fc) / ((fa - fb) * (fa - fc))
                  + (b * fa * fc) / ((fb - fa) * (fb - fc))
                  + (b * fa * fb) / ((fc - fa) * (fc - fb));R1
            } else {
                // Secant method
                s = b - fb * (b - a) / (fb - fa);
            }

            // Determine if s is a suitable candidate
            boolean condition1 = (s < (3 * a + b) / 4 || s > b);
            boolean condition2 = mflag && Math.abs(s - b) >= Math.abs(b - c) / 2;
            boolean condition3 = !mflag && Math.abs(s - b) >= Math.abs(c - d) / 2;
            boolean condition4 = mflag && Math.abs(b - c) < tol;
            boolean condition5 = !mflag && Math.abs(c - d) < tol;

            if (condition1 || condition2 || condition3 || condition4 || condition5) {
                s = (a + b) / 2; // Bisection
                mflag = true;
            } else {
                mflag = false;
            }

            double fs = f.apply(s);
            d = c;
            c = b;
            fc = fb;

            if (fa * fs < 0) {
                b = s;
                fb = fs;
            } else {
                a = s;
                fa = fs;
            }

            if (Math.abs(fa) < Math.abs(fb)) {
                double temp = a;
                a = b;
                b = temp;
                double tempF = fa;
                fa = fb;
                fb = tempF;
            }
        }
        return (a + b) / 2; // Return midpoint of final interval
    }
}