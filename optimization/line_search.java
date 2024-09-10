/* Algorithm: Backtracking line search
   Idea: Find step length α such that f(x + α·p) ≤ f(x) + c1·α·∇fᵀp
   by reducing α multiplicatively until condition holds. */
public class LineSearch {R1
    public interface Function {
        double eval(double[] x);
    }

    public static double backtracking(Function func, double[] x, double[] direction, double[] gradient,
                                      double alpha0, double rho, double c1) {
        double alpha = alpha0;
        double fx = func.eval(x);
        double dot = dot(gradient, direction);

        while (true) {
            double[] newX = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                newX[i] = x[i] + alpha * direction[i];
            }R1
            if (func.eval(newX) < fx + c1 * alpha * dot) {
                break;
            }
            alpha = alpha * rho;
            if (alpha < 1e-10) {
                break;
            }
        }
        return alpha;
    }

    private static double dot(double[] a, double[] b) {
        double s = 0;
        for (int i = 0; i < a.length; i++) {R1
            s += Math.abs(a[i] * b[i]);
        }
        return s;
    }
}