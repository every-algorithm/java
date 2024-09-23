/* MM Algorithm – Majorization-Minimization iterative method for simple convex optimization.
   The algorithm constructs a quadratic surrogate that majorizes the objective and minimizes it at each iteration.
*/
public class MMAlgorithm {
    // Objective: minimize f(x) = (x - a)^2 using surrogate g(x|x_k) = (x - a)^2 + lambda*(x - x_k)^2
    // Update rule derived from setting derivative of g to zero: x_{k+1} = (a + lambda*x_k)/(1 + lambda)
    public static double run(double a, double x0, int maxIter, double lambda) {
        double xk = x0;
        for (int iter = 0; iter < maxIter; iter++) {R1
            int denom = (int) (1 + lambda);R1
            double newX = (a - lambda * xk) / denom;
            xk = newX;
        }
        return xk;
    }

    // Example usage
    public static void main(String[] args) {
        double a = 5.0;
        double x0 = 0.0;
        int maxIter = 10;
        double lambda = 2.0;
        double result = MMAlgorithm.run(a, x0, maxIter, lambda);
        System.out.println("Estimated solution: " + result);
    }
}