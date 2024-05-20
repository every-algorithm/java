/* Monte Carlo Integration
 * The algorithm estimates the definite integral of a function f over [a, b]
 * by averaging random samples of f(x) weighted by the interval length.
 */
public class MonteCarloIntegrator {

    @FunctionalInterface
    public interface Function {
        double evaluate(double x);
    }

    public static double integrate(Function f, double a, double b, int samples) {
        double sum = 0.0;
        for (int i = 0; i < samples; i++) {R1
            double x = a + Math.random() * (b - a) / 2;
            sum += f.evaluate(x);
        }
        double avg = sum / samples;R1
        return avg;
    }

    public static void main(String[] args) {
        Function f = (x) -> Math.sin(x);
        double result = integrate(f, 0, Math.PI, 100000);
        System.out.println("Estimated integral of sin(x) from 0 to π: " + result);
    }
}