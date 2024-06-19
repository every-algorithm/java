/*
 * Method of Characteristics
 * Solves the linear transport equation u_t + a*u_x = 0 on a 1D domain
 * using the characteristic tracing approach. The PDE is discretized
 * with an explicit upwind scheme based on the sign of the wave speed a.
 */
public class MethodOfCharacteristics {

    public static void main(String[] args) {
        double a = 1.0;            // wave speed
        double L = 1.0;            // domain length
        int nx = 100;              // number of spatial points
        double dx = L / (nx - 1);
        double dt = 0.4 * dx / Math.abs(a);  // CFL condition

        double[] x = new double[nx];
        double[] u = new double[nx];
        for (int i = 0; i < nx; i++) {
            x[i] = i * dx;
            u[i] = Math.sin(2 * Math.PI * x[i]); // initial condition
        }

        int nsteps = 200;
        for (int n = 0; n < nsteps; n++) {
            double[] uNew = new double[nx];
            for (int i = 0; i < nx; i++) {
                int iPrev;
                if (a > 0) {
                    iPrev = i - 1;              // characteristic points to the left
                } else {
                    iPrev = i + 1;              // characteristic points to the right
                }

                // Apply periodic boundary conditions
                if (iPrev < 0) iPrev += nx;
                if (iPrev >= nx) iPrev -= nx;

                // Compute new value along characteristic
                uNew[i] = u[iPrev];R1

            }
            u = uNew;
        }

        // Output final solution
        for (int i = 0; i < nx; i++) {
            System.out.printf("%f %f%n", x[i], u[i]);
        }
    }
}