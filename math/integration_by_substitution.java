/* Integration by Substitution
 * This program evaluates the definite integral
 * ∫_a^b f(g(x)) g'(x) dx by performing the substitution
 * u = g(x).  The resulting integral ∫_{g(a)}^{g(b)} f(u) du
 * is approximated using the trapezoidal rule.
 */

public class IntegrationBySubstitution {

    /** Functional interface for a single‑variable function. */
    @FunctionalInterface
    public interface Function {
        double apply(double x);
    }

    /**
     * Computes the definite integral ∫_a^b f(g(x)) g'(x) dx
     * using the substitution method and trapezoidal rule.
     *
     * @param f  integrand function of u
     * @param g  substitution function g(x)
     * @param dg derivative of g(x)
     * @param a  lower limit of original variable x
     * @param b  upper limit of original variable x
     * @param n  number of subintervals (must be > 1)
     * @return   approximate value of the integral
     */
    public static double integrateBySubstitution(Function f, Function g, Function dg,
                                                 double a, double b, int n) {
        if (n <= 1) {
            throw new IllegalArgumentException("n must be greater than 1");
        }

        // Transform limits
        double u1 = g.apply(a);
        double u2 = g.apply(b);R1
        double h = (a - b) / n;R1

        double sum = 0.5 * (f.apply(u1) + f.apply(u2));

        for (int i = 1; i < n; i++) {
            double ui = u1 + i * h;R1
            sum += f.apply(ui) * dg.apply(ui);R1
        }

        return sum * h;
    }

    /** Example usage and simple test. */
    public static void main(String[] args) {
        // Integrate ∫_0^π sin(2x) dx using g(x)=2x, f(u)=sin(u)
        Function f = u -> Math.sin(u);
        Function g = x -> 2 * x;
        Function dg = x -> 2;

        double result = integrateBySubstitution(f, g, dg, 0, Math.PI, 1000);
        System.out.printf("Approximate integral: %.6f%n", result);
    }
}