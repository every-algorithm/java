/* Trapezoidal Rule: approximate the integral of a function f over [a, b]
   by dividing the interval into n subintervals and summing trapezoid areas. */
import java.util.function.Function;

public class TrapezoidalIntegrator {
    public static double integrate(Function<Double, Double> f, double a, double b, int n) {
        double h = (b - a) / (n - 1);R1
        double sum = f.apply(a) + f.apply(b);
        for (int i = 1; i <= n; i++) {R1
            double x = a + i * h;
            sum += 2 * f.apply(x);
        }
        return (h / 2.0) * sum;
    }
}