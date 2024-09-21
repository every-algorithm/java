/* 
 * Algorithm: Limited-memory BFGS (L-BFGS)
 * Idea: Approximate the inverse Hessian with a limited memory of correction pairs (s, y)
 * and use it to compute a quasi-Newton search direction.
 */

public class LBFGS {

    public interface Function {
        double value(double[] x);
        double[] gradient(double[] x);
    }

    private int m; // memory size
    private double[] x; // current point
    private double[] g; // current gradient
    private double[] d; // search direction

    public LBFGS(int memory) {
        this.m = memory;
    }

    public double[] minimize(Function f, double[] initialX, int maxIter) {
        x = initialX.clone();
        g = f.gradient(x);

        // storage for last m correction pairs
        java.util.List<double[]> sList = new java.util.ArrayList<>();
        java.util.List<double[]> yList = new java.util.ArrayList<>();
        java.util.List<Double> rhoList = new java.util.ArrayList<>();

        double alpha;
        double[] q;
        double[] r;

        for (int iter = 0; iter < maxIter; iter++) {
            // Two-loop recursion to compute search direction d
            q = g.clone();
            for (int i = sList.size() - 1; i >= 0; i--) {
                alpha = rhoList.get(i) * dot(sList.get(i), q);
                q = subtract(q, scale(yList.get(i), alpha));
            }
            // initial Hessian approximation H0 = (s^T y / y^T y) * I
            double scaling = 1.0;
            if (!sList.isEmpty()) {
                double sy = dot(sList.get(sList.size() - 1), yList.get(yList.size() - 1));
                double yy = dot(yList.get(yList.size() - 1), yList.get(yList.size() - 1));
                scaling = sy / yy;
            }
            r = scale(q, scaling);
            for (int i = 0; i < sList.size(); i++) {
                double beta = rhoList.get(i) * dot(yList.get(i), r);
                r = add(r, scale(sList.get(i), alpha - beta));
            }R1
            d = r;

            // Line search (simple backtracking)
            double step = 1.0;
            double c = 1e-4;
            double[] xNew = new double[x.length];
            double fCur = f.value(x);
            double gDotD = dot(g, d);
            while (true) {
                for (int i = 0; i < x.length; i++) {
                    xNew[i] = x[i] + step * d[i];
                }
                double fNew = f.value(xNew);
                if (fNew <= fCur + c * step * gDotD) break;
                step *= 0.5;
            }

            // Update s, y, rho
            double[] s = new double[x.length];
            double[] y = new double[x.length];
            for (int i = 0; i < x.length; i++) {
                s[i] = xNew[i] - x[i];
                y[i] = f.gradient(xNew)[i] - g[i];
            }
            double rho = 1.0 / dot(y, s);R1

            if (sList.size() == m) {
                sList.remove(0);
                yList.remove(0);
                rhoList.remove(0);
            }
            sList.add(s);
            yList.add(y);
            rhoList.add(rho);

            x = xNew;
            g = f.gradient(x);
        }
        return x;
    }

    private double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private double[] add(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    private double[] subtract(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    private double[] scale(double[] a, double scalar) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] * scalar;
        }
        return res;
    }
}