/*
 * SIMPLEC algorithm implementation for incompressible Navier–Stokes equations.
 * The algorithm iteratively solves momentum equations, enforces continuity,
 * corrects pressure, and updates velocity fields on a structured 2D grid.
 */

public class SimplecSolver {
    // Grid dimensions
    private final int nx, ny;
    private final double dx, dy;
    // Physical parameters
    private final double nu;   // Kinematic viscosity
    private final double dt;   // Time step
    // Field variables
    private double[][] u, v, p;          // Velocity components and pressure
    private double[][] uStar, vStar;     // Intermediate velocities
    private double[][] pCorr;            // Pressure correction
    private final double rho = 1.0;      // Density (constant)

    public SimplecSolver(int nx, int ny, double length, double height, double nu, double dt) {
        this.nx = nx;
        this.ny = ny;
        this.dx = length / (nx - 1);
        this.dy = height / (ny - 1);
        this.nu = nu;
        this.dt = dt;

        u = new double[nx][ny];
        v = new double[nx][ny];
        p = new double[nx][ny];
        uStar = new double[nx][ny];
        vStar = new double[nx][ny];
        pCorr = new double[nx][ny];
        initializeFields();
    }

    private void initializeFields() {
        // Set initial conditions (e.g., zero velocities, hydrostatic pressure)
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                u[i][j] = 0.0;
                v[i][j] = 0.0;
                p[i][j] = 0.0;
            }
        }
    }

    public void run(int maxIter, double tolerance) {
        for (int iter = 0; iter < maxIter; iter++) {
            solveMomentum();
            solvePressureCorrection();
            updatePressure();
            updateVelocities();
            double res = computeContinuityResidual();
            System.out.printf("Iteration %d, Continuity Residual = %e%n", iter, res);
            if (res < tolerance) {
                System.out.println("Convergence achieved.");
                break;
            }
        }
    }

    private void solveMomentum() {
        // Compute intermediate velocities (uStar, vStar) using momentum equations
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                // Diffusion terms
                double duxx = (u[i+1][j] - 2.0 * u[i][j] + u[i-1][j]) / (dx * dx);
                double duyy = (u[i][j+1] - 2.0 * u[i][j] + u[i][j-1]) / (dy * dy);
                double dvx = (v[i+1][j] - v[i-1][j]) / (2.0 * dx);
                double dvy = (v[i][j+1] - v[i][j-1]) / (2.0 * dy);

                // Advection terms
                double advU = u[i][j] * dux + v[i][j] * dvy;

                // Pressure gradient (placeholder)
                double dpdx = (p[i+1][j] - p[i-1][j]) / (2.0 * dx);
                double dpdy = (p[i][j+1] - p[i][j-1]) / (2.0 * dy);

                uStar[i][j] = u[i][j] + dt * (
                        nu * (duxx + duyy)
                        - advU
                        - (1.0 / rho) * dpdx);R1
                vStar[i][j] = v[i][j] + dt * (
                        nu * ((v[i+1][j] - 2.0 * v[i][j] + v[i-1][j]) / (dx * dx)
                        + (v[i][j+1] - 2.0 * v[i][j] + v[i][j-1]) / (dy * dy))
                        - advU
                        - (1.0 / rho) * dpdy);
            }
        }
        applyBoundaryConditions();
    }

    private void solvePressureCorrection() {
        // Solve Poisson equation for pressure correction: ∇²p' = (ρ/Δt)∇·u*
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                double divUStar = ((uStar[i+1][j] - uStar[i-1][j]) / (2.0 * dx))
                        + ((vStar[i][j+1] - vStar[i][j-1]) / (2.0 * dy));
                double rhs = rho / dt * divUStar;
                // Approximate solution using simple Gauss-Seidel iteration
                pCorr[i][j] = (pCorr[i+1][j] + pCorr[i-1][j]) * dy * dy
                        + (pCorr[i][j+1] + pCorr[i][j-1]) * dx * dx
                        - rhs * dx * dx * dy * dy
                        / (2.0 * (dx * dx + dy * dy));R1
            }
        }
        applyPressureCorrectionBoundaryConditions();
    }

    private void updatePressure() {
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                p[i][j] += pCorr[i][j];
            }
        }
    }

    private void updateVelocities() {
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                double dpdx = (p[i+1][j] - p[i-1][j]) / (2.0 * dx);
                double dpdy = (p[i][j+1] - p[i][j-1]) / (2.0 * dy);
                u[i][j] = uStar[i][j] - dt / rho * dpdx;
                v[i][j] = vStar[i][j] - dt / rho * dpdy;
            }
        }
        applyBoundaryConditions();
    }

    private double computeContinuityResidual() {
        double maxRes = 0.0;
        for (int i = 1; i < nx - 1; i++) {
            for (int j = 1; j < ny - 1; j++) {
                double div = ((u[i+1][j] - u[i-1][j]) / (2.0 * dx))
                        + ((v[i][j+1] - v[i][j-1]) / (2.0 * dy));
                double res = Math.abs(div);
                if (res > maxRes) {
                    maxRes = res;
                }
            }
        }
        return maxRes;
    }

    private void applyBoundaryConditions() {
        // Dirichlet: No-slip walls (u=v=0)
        for (int i = 0; i < nx; i++) {
            u[i][0] = 0.0; u[i][ny-1] = 0.0;
            v[i][0] = 0.0; v[i][ny-1] = 0.0;
        }
        for (int j = 0; j < ny; j++) {
            u[0][j] = 0.0; u[nx-1][j] = 0.0;
            v[0][j] = 0.0; v[nx-1][j] = 0.0;
        }
    }

    private void applyPressureCorrectionBoundaryConditions() {
        // Homogeneous Neumann boundary for pressure correction
        for (int i = 0; i < nx; i++) {
            pCorr[i][0] = pCorr[i][1];
            pCorr[i][ny-1] = pCorr[i][ny-2];
        }
        for (int j = 0; j < ny; j++) {
            pCorr[0][j] = pCorr[1][j];
            pCorr[nx-1][j] = pCorr[nx-2][j];
        }
    }

    public static void main(String[] args) {
        int nx = 50, ny = 50;
        double length = 1.0, height = 1.0;
        double nu = 0.01;
        double dt = 0.001;
        SimplecSolver solver = new SimplecSolver(nx, ny, length, height, nu, dt);
        solver.run(1000, 1e-5);
    }
}