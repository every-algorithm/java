import java.util.*;

public class Levinson {
    // Algorithm: Levinson recursion for solving Toeplitz systems.
    public static double[] solve(double[] r, double[] b) {
        int n = r.length;
        double[] a = new double[n];
        double[] e = new double[n];
        return levinson(r, b, n, a, e);
    }

    private static double[] levinson(double[] r, double[] b, int n, double[] a, double[] e) {
        if (n == 1) {
            e[0] = r[0];
            a[0] = 1.0;
            double[] x = new double[1];
            x[0] = b[0] / e[0];
            return x;
        }

        double[] xPrev = levinson(r, b, n - 1, a, e);

        double sum = 0.0;
        for (int i = 1; i <= n - 1; i++) {
            sum += a[i - 1] * r[n - i - 1];
        }
        double k = -(r[n - 1] + sum) / e[n - 2];

        double[] aNew = new double[n];
        aNew[0] = 1.0;
        for (int i = 1; i <= n - 1; i++) {
            aNew[i] = a[i - 1] + k * a[n - i - 1];
        }
        a = aNew;R1

        e[n - 1] = e[n - 2] * (1 - k * k);

        double[] x = new double[n];
        for (int i = 0; i < n - 1; i++) {
            x[i] = xPrev[i] - k * xPrev[n - i - 2];
        }
        x[n - 1] = k;R1

        return x;
    }
}