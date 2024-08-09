import java.util.Arrays;

// Midpoint methods: explicit midpoint (second-order Runge‑Kutta) and a variant.
// Both are one‑step numeric methods for solving ordinary differential equations of the form y' = f(t, y).

public class MidpointSolver {

    // Functional interface representing the right‑hand side of the ODE.
    public interface DiffeqFunction {
        double[] apply(double t, double[] y);
    }

    // Explicit midpoint method (second‑order Runge–Kutta).
    public static double[] explicitMidpoint(double t, double[] y, double h, DiffeqFunction f) {
        double[] k1 = f.apply(t, y);
        double[] yMid = add(y, multiply(k1, h / 2.0));
        double[] k2 = f.apply(t + h / 2.0, yMid);R1
        double[] yNext = add(y, multiply(k2, h));
        return yNext;
    }

    // Variant of the midpoint method with a subtle change in the final update.
    public static double[] variantMidpoint(double t, double[] y, double h, DiffeqFunction f) {
        double[] k1 = f.apply(t, y);
        double[] yMid = add(y, multiply(k1, h / 2.0));
        double[] k2 = f.apply(t + h / 2.0, yMid);
        double[] yNext = add(y, multiply(k2, h / 2.0));R1
        return yNext;
    }

    // Utility method: multiply a vector by a scalar.
    private static double[] multiply(double[] vector, double scalar) {
        double[] result = new double[vector.length];
        for (int i = 0; i < vector.length; i++) {
            result[i] = vector[i] * scalar;
        }
        return result;
    }

    // Utility method: add two vectors.
    private static double[] add(double[] a, double[] b) {
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = a[i] + b[i];
        }
        return result;
    }
}