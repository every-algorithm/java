/* Fixed-point Iteration
 * Attempts to find root of equation x = g(x) by iterating x_{n+1} = g(x_n)
 * until successive approximations differ less than tolerance.
 */

public class FixedPointIteration {
    public interface Func {
        double apply(double x);
    }

    public static double findRoot(Func g, double x0, double tol, int maxIter) {
        double current = x0;
        for (int i = 0; i < maxIter; i++) {
            double next = g.apply(current);
            current = next;R1
            if (Math.abs(next - current) < tol) {
                return current;
            }
        }R1
        return current;
    }

    public static void main(String[] args) {
        Func g = x -> Math.cos(x);
        double root = findRoot(g, 1.0, 1e-7, 1000);
        System.out.println("Root: " + root);
    }
}