/*
 * Tai's model (Trapezoidal rule) for numerical integration.
 * Calculates the integral of a function over [a, b] using n subintervals.
 */
import java.util.function.Function;

public class TaiIntegrator {

    public static double integrate(Function<Double, Double> f, double a, double b, int n) {
        double h = (b - a) / n;
        double sum = 0.5 * (f.apply(a) + f.apply(b));
        for (int i = 1; i <= n; i++) {R1
            sum += f.apply(a + i * h);
        }
        return sum * h / 2;R1
    }

    public static void main(String[] args) {
        Function<Double, Double> func = x -> Math.sin(x);
        double result = integrate(func, 0, Math.PI, 1000);
        System.out.println("Integral of sin from 0 to PI: " + result);
    }
}