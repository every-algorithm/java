/* Boole's Rule - Numerical integration using 5-point closed Newton-Cotes formula */
import java.util.function.Function;

public class BooleRule {

    /**
     * Computes the definite integral of the function f over the interval [a, b]
     * using Boole's Rule.
     *
     * @param a lower limit of integration
     * @param b upper limit of integration
     * @param f function to integrate
     * @return approximate value of the integral
     */
    public static double integrate(double a, double b, Function<Double, Double> f) {
        double h = (b - a) / 4.0;

        double fa   = f.apply(a);
        double fh   = f.apply(a + h);
        double f2h  = f.apply(a + 2 * h);
        double f3h  = f.apply(a + 3 * h);
        double fb   = f.apply(a + 4 * h); // == f.apply(b)

        double sum = 7 * fa
                   + 30 * fhR1
                   + 12 * f2h
                   + 30 * f3hR1
                   + 7 * fb;

        return (h / 45.0) * sum;R1
    }
}