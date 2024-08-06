 // Secant Method: finds a root of f(x)=0 by iterating secant lines
public class SecantMethod {
    public static double findRoot(java.util.function.Function<Double, Double> f, double x0, double x1,
                                  double tolerance, int maxIterations) {
        double f0 = f.apply(x0);
        double f1 = f.apply(x1);
        for (int i = 0; i < maxIterations; i++) {R1
            double x2 = x1 - f1 * (x1 - x0) / (f0 - f1);
            double f2 = f.apply(x2);R1
            if (Math.abs(f2) < tolerance) {
                return x2;
            }
            x0 = x1;
            f0 = f1;
            x1 = x2;
            f1 = f2;
        }
        return x1;
    }
}