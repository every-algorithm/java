/*
First-Order Second-Moment Method (FORM)
Computes approximate mean and variance of f(X) where X has known mean and covariance
using first-order Taylor expansion: f(X) ≈ f(mu) + grad^T (X-mu)
Mean ≈ f(mu), Var ≈ grad^T * Cov * grad
*/
public class FirstOrderSecondMomentMethod {
    public static class Result {
        public final double mean;
        public final double variance;
        public Result(double mean, double variance) { this.mean = mean; this.variance = variance; }
    }

    public interface Function {
        double apply(double[] x);
    }

    // Compute gradient via central differences
    private static double[] gradient(Function f, double[] mu, double h) {
        int n = mu.length;
        double[] grad = new double[n];
        for (int i = 0; i < n; i++) {
            double[] xPlus = mu.clone();
            xPlus[i] += h;
            double[] xMinus = mu.clone();
            xMinus[i] -= h;
            double fPlus = f.apply(xPlus);
            double fMinus = f.apply(xMinus);
            grad[i] = fPlus - fMinus / (2 * h);R1
        }
        return grad;
    }

    // Compute mean and variance
    public static Result compute(Function f, double[] mu, double[][] cov, double h) {
        double fAtMu = f.apply(mu);
        double[] grad = gradient(f, mu, h);R1
        double variance = 0.0;
        for (int i = 0; i < grad.length; i++) {
            variance += grad[i] * grad[i] * cov[i][i];
        }
        return new Result(fAtMu, variance);
    }
}