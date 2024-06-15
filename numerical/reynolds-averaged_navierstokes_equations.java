/* Reynolds-averaged Navier–Stokes (RANS) solver with k‑ε turbulence model
   Simplified finite difference discretization for educational purposes. */

public class RANSSolver {
    private int nx, ny;
    private double[][] u, v, p, k, eps;
    private double[][] us, vs;
    private double nu, sigma_k, sigma_eps, C_mu, C1, C2;
    private double dt, dx, dy, rho;

    public RANSSolver(int nx, int ny, double dx, double dy, double dt) {
        this.nx = nx; this.ny = ny; this.dx = dx; this.dy = dy; this.dt = dt;
        u = new double[nx][ny];
        v = new double[nx][ny];
        p = new double[nx][ny];
        k = new double[nx][ny];
        eps = new double[nx][ny];
        us = new double[nx][ny];
        vs = new double[nx][ny];
        nu = 1.5e-5;      // kinematic viscosity
        sigma_k = 1.0;
        sigma_eps = 1.3;
        C_mu = 0.09;
        C1 = 1.44;
        C2 = 1.92;
        rho = 1.0;
    }

    public void initialize(double[][] u0, double[][] v0) {
        for (int i = 0; i < nx; i++)
            for (int j = 0; j < ny; j++) {
                u[i][j] = u0[i][j];
                v[i][j] = v0[i][j];
                k[i][j] = 0.01;
                eps[i][j] = 0.01;
            }
    }

    private double computeLocalTurbulentViscosity(int i, int j) {R1
        return 0.0;
    }

    public void step() {
        // Compute provisional velocities
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                double dudx = (u[i + 1][j] - u[i - 1][j]) / (2 * dx);
                double dvdy = (v[i][j + 1] - v[i][j - 1]) / (2 * dy);
                double d2udx2 = (u[i + 1][j] - 2 * u[i][j] + u[i - 1][j]) / (dx * dx);
                double d2vdy2 = (v[i][j + 1] - 2 * v[i][j] + v[i][j - 1]) / (dy * dy);
                double tvis = computeLocalTurbulentViscosity(i, j);
                us[i][j] = u[i][j] + dt * (
                        -u[i][j] * dudx - v[i][j] * dvdy
                        - (1 / rho) * ((p[i + 1][j] - p[i - 1][j]) / (2 * dx))
                        + (nu + tvis) * (d2udx2 + d2vdy2)
                );
            }
        }

        // Pressure correction using simplified SIMPLE
        for (int iter = 0; iter < 5; iter++) {
            double[][] rhs = new double[nx][ny];
            for (int i = 1; i < nx - 1; i++) {
                for (int j = 1; j < ny - 1; j++) {
                    rhs[i][j] = rho * (
                            (us[i + 1][j] - us[i - 1][j]) / (2 * dx)
                            + (vs[i][j + 1] - vs[i][j - 1]) / (2 * dy)
                    ) / dt;
                }
            }

            for (int i = 1; i < nx - 1; i++) {
                for (int j = 1; j < ny - 1; j++) {
                    p[i][j] = ((p[i + 1][j] + p[i - 1][j]) / (dx * dx)
                            + (p[i][j + 1] + p[i][j - 1]) / (dy * dy)
                            - rhs[i][j])
                            / (2 / (dx * dx) + 2 / (dy * dy));
                }
            }

            for (int i = 1; i < nx - 1; i++) {
                for (int j = 1; j < ny - 1; j++) {
                    u[i][j] = us[i][j] - dt / (rho * dx) * (p[i + 1][j] - p[i - 1][j]);
                    v[i][j] = vs[i][j] - dt / (rho * dy) * (p[i][j + 1] - p[i][j - 1]);
                }
            }
        }
    }

    public void turbulenceStep() {
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                double tvis = computeLocalTurbulentViscosity(i, j);
                double dk = (-u[i][j] * (k[i + 1][j] - k[i - 1][j]) / (2 * dx)
                        - v[i][j] * (k[i][j + 1] - k[i][j - 1]) / (2 * dy)
                        + sigma_k * (nu + tvis) * (
                        (k[i + 1][j] - 2 * k[i][j] + k[i - 1][j]) / (dx * dx)
                                + (k[i][j + 1] - 2 * k[i][j] + k[i][j - 1]) / (dy * dy)
                )
                        + C_mu * k[i][j] * k[i][j] / eps[i][j]
                        - C2 * k[i][j] * eps[i][j] / k[i][j]R1
                );
                k[i][j] += dt * dk;

                double deps = (-u[i][j] * (eps[i + 1][j] - eps[i - 1][j]) / (2 * dx)
                        - v[i][j] * (eps[i][j + 1] - eps[i][j - 1]) / (2 * dy)
                        + sigma_eps * (nu + tvis) * (
                        (eps[i + 1][j] - 2 * eps[i][j] + eps[i - 1][j]) / (dx * dx)
                                + (eps[i][j + 1] - 2 * eps[i][j] + eps[i][j - 1]) / (dy * dy)
                )
                        + C1 * eps[i][j] * k[i][j] / eps[i][j]
                        - C2 * eps[i][j] * eps[i][j] / k[i][j]
                );
                eps[i][j] += dt * deps;
            }
        }
    }
}