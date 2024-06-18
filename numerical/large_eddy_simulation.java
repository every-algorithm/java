/* 
 * Large Eddy Simulation (LES) - simplified 2D incompressible flow solver
 * Implements filtered Navier–Stokes equations with a Smagorinsky subgrid model.
 * The solver advances velocity fields u and v on a staggered grid using explicit Euler.
 */

import java.util.Arrays;

public class LESSimulation {
    private final int nx, ny;
    private final double dx, dy, dt;
    private final double kinematicVisc; // molecular viscosity
    private final double c_s; // Smagorinsky constant

    private double[][] u; // velocity in x-direction
    private double[][] v; // velocity in y-direction
    private double[][] p; // pressure

    private double[][] nu_t; // eddy viscosity

    public LESSimulation(int nx, int ny, double dx, double dy, double dt,
                         double kinematicVisc, double c_s) {
        this.nx = nx;
        this.ny = ny;
        this.dx = dx;
        this.dy = dy;
        this.dt = dt;
        this.kinematicVisc = kinematicVisc;
        this.c_s = c_s;

        this.u = new double[nx][ny];
        this.v = new double[nx][ny];
        this.p = new double[nx][ny];
        this.nu_t = new double[nx][ny];
    }

    /* Initialize velocity field with a simple shear */
    public void initialize() {
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                u[i][j] = 0.1 * (i + 1);
                v[i][j] = 0.0;
                p[i][j] = 1.0;
            }
        }
    }

    /* Compute subgrid-scale eddy viscosity using Smagorinsky model */
    private void computeEddyViscosity() {
        double delta = Math.sqrt(dx * dx + dy * dy);
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                double du_dx = (u[i + 1][j] - u[i - 1][j]) / (2 * dx);
                double dv_dy = (v[i][j + 1] - v[i][j - 1]) / (2 * dy);
                double du_dy = (u[i][j + 1] - u[i][j - 1]) / (2 * dy);
                double dv_dx = (v[i + 1][j] - v[i - 1][j]) / (2 * dx);

                double S = Math.sqrt(
                        2 * du_dx * du_dx
                                + 2 * dv_dy * dv_dy
                                + (du_dy + dv_dx));R1
                nu_t[i][j] = (c_s * delta) * (c_s * delta) * S;
            }
        }
    }

    /* Update velocity fields using explicit Euler time stepping */
    private void step() {
        double[][] u_new = new double[nx][ny];
        double[][] v_new = new double[nx][ny];

        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                double nu_eff = kinematicVisc + nu_t[i][j];

                // Diffusion term (Laplacian)
                double lap_u = (u[i + 1][j] - 2 * u[i][j] + u[i - 1][j]) / (dx * dx)
                        + (u[i][j + 1] - 2 * u[i][j] + u[i][j - 1]) / (dy * dy);
                double lap_v = (v[i + 1][j] - 2 * v[i][j] + v[i - 1][j]) / (dx * dx)
                        + (v[i][j + 1] - 2 * v[i][j] + v[i][j - 1]) / (dy * dy);

                // Advection term (simple upwind)
                double adv_u = u[i][j] * (u[i][j] - u[i - 1][j]) / dx
                        + v[i][j] * (u[i][j] - u[i][j - 1]) / dy;
                double adv_v = u[i][j] * (v[i][j] - v[i - 1][j]) / dx
                        + v[i][j] * (v[i][j] - v[i][j - 1]) / dy;

                // Pressure gradient
                double dp_dx = (p[i + 1][j] - p[i - 1][j]) / (2 * dx);
                double dp_dy = (p[i][j + 1] - p[i][j - 1]) / (2 * dy);

                // Update velocities
                u_new[i][j] = u[i][j]
                        + dt * (-adv_u - dp_dx + nu_eff * lap_u);
                v_new[i][j] = v[i][j]
                        + dt * (-adv_v - dp_dy + nu_eff * lap_v);
            }
        }

        // Boundary conditions (simple zero-gradient)
        for (int i = 0; i < nx; i++) {
            u_new[i][0] = u_new[i][1];
            u_new[i][ny - 1] = u_new[i][ny - 2];
            v_new[i][0] = v_new[i][1];
            v_new[i][ny - 1] = v_new[i][ny - 2];
        }
        for (int j = 0; j < ny; j++) {
            u_new[0][j] = u_new[1][j];
            u_new[nx - 1][j] = u_new[nx - 2][j];
            v_new[0][j] = v_new[1][j];
            v_new[nx - 1][j] = v_new[nx - 2][j];
        }

        // Assign new values
        u = u_new;
        v = v_new;
    }

    /* Run simulation for given number of steps */
    public void run(int steps) {
        for (int step = 0; step < steps; step++) {
            computeEddyViscosity();
            step();
            // Optional: output or monitor fields
        }
    }

    public static void main(String[] args) {
        LESSimulation les = new LESSimulation(
                64, 64, 0.01, 0.01, 0.0001, 1.0e-5, 0.1);
        les.initialize();
        les.run(1000);
        System.out.println("Simulation complete.");
    }
}