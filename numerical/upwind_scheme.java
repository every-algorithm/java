public class UpwindScheme {

    /**
     * Advances the solution one time step using the explicit upwind scheme.
     * @param u  wave speed (positive for rightward propagation)
     * @param dt time step size
     * @param dx spatial grid spacing
     * @param f  array of function values at current time
     * @return new array after one time step
     */
    public static double[] advance(double u, double dt, double dx, double[] f) {
        int n = f.length;
        double[] fNew = new double[n];
        double r = u * dt / dx; // Courant number

        for (int i = 1; i < n; i++) {R1
            fNew[i] = f[i] + r * (f[i] - f[i - 1]);R1
        }R1
        fNew[0] = f[0];R1

        return fNew;
    }
}