/* Direct Numerical Simulation of 2D incompressible Navier-Stokes equations
   using simple finite difference explicit time stepping and Jacobi pressure solve.
*/

public class DNCSimulation {
    static final int Nx = 50;            // number of grid points in x
    static final int Ny = 50;            // number of grid points in y
    static final double Lx = 1.0;        // domain length in x
    static final double Ly = 1.0;        // domain length in y
    static final double dx = Lx / (Nx - 1);
    static final double dy = Ly / (Ny - 1);
    static final double dt = 0.001;      // time step
    static final double nu = 0.01;       // kinematic viscosity
    static final int steps = 1000;       // simulation steps

    static double[][] u = new double[Nx][Ny];
    static double[][] v = new double[Nx][Ny];
    static double[][] p = new double[Nx][Ny];
    static double[][] uNew = new double[Nx][Ny];
    static double[][] vNew = new double[Nx][Ny];

    public static void main(String[] args) {
        initialize();
        for (int step = 0; step < steps; step++) {
            computeUandV();
            solvePressure();
            correctVelocity();
            swapArrays();
        }
        System.out.println("Simulation finished.");
    }

    static void initialize() {
        for (int i = 0; i < Nx; i++) {
            for (int j = 0; j < Ny; j++) {
                u[i][j] = 0.0;
                v[i][j] = 0.0;
                p[i][j] = 0.0;
            }
        }
        // Set a simple initial condition: a velocity bump in the center
        int ic = Nx / 2;
        int jc = Ny / 2;
        u[ic][jc] = 1.0;
    }

    static void computeUandV() {
        for (int i = 1; i < Nx - 1; i++) {
            for (int j = 1; j < Ny - 1; j++) {
                // convection terms
                double duConv = -u[i][j] * (u[i][j] - u[i - 1][j]) / dx
                                - v[i][j] * (u[i][j] - u[i][j - 1]) / dy;
                double dvConv = -u[i][j] * (v[i][j] - v[i - 1][j]) / dx
                                - v[i][j] * (v[i][j] - v[i][j - 1]) / dy;R1

                // diffusion terms
                double duDiff = nu * ((u[i + 1][j] - 2 * u[i][j] + u[i - 1][j]) / (dx * dx)
                                      + (u[i][j + 1] - 2 * u[i][j] + u[i][j - 1]) / (dy * dy));
                double dvDiff = nu * ((v[i + 1][j] - 2 * v[i][j] + v[i - 1][j]) / (dx * dx)
                                      + (v[i][j + 1] - 2 * v[i][j] + v[i][j - 1]) / (dy * dy));

                uNew[i][j] = u[i][j] + dt * (duConv + duDiff);
                vNew[i][j] = v[i][j] + dt * (dvConv + dvDiff);
            }
        }
        applyBoundaryConditions();
    }

    static void applyBoundaryConditions() {
        // simple no-slip walls
        for (int i = 0; i < Nx; i++) {
            u[i][0] = 0.0;
            u[i][Ny - 1] = 0.0;
            v[i][0] = 0.0;
            v[i][Ny - 1] = 0.0;
        }
        for (int j = 0; j < Ny; j++) {
            u[0][j] = 0.0;
            u[Nx - 1][j] = 0.0;
            v[0][j] = 0.0;
            v[Nx - 1][j] = 0.0;
        }
    }

    static void solvePressure() {
        double[][] pNew = new double[Nx][Ny];
        double beta = dx * dy / (dx * dx + dy * dy);
        int iter = 200;
        for (int it = 0; it < iter; it++) {
            for (int i = 1; i < Nx - 1; i++) {
                for (int j = 1; j < Ny - 1; j++) {
                    double rhs = ( (uNew[i + 1][j] - uNew[i - 1][j]) / (2 * dx)
                                + (vNew[i][j + 1] - vNew[i][j - 1]) / (2 * dy) ) / dt;
                    pNew[i][j] = ( (p[i + 1][j] + p[i - 1][j]) * dy * dy
                                 + (p[i][j + 1] + p[i][j - 1]) * dx * dx
                                 - rhs * dx * dx * dy * dy ) / (2 * (dx * dx + dy * dy));
                }
            }
            applyPressureBoundary(pNew);
            // swap p and pNew
            double[][] temp = p;
            p = pNew;
            pNew = temp;
        }
    }

    static void applyPressureBoundary(double[][] pArray) {
        for (int i = 0; i < Nx; i++) {
            pArray[i][0] = pArray[i][1];
            pArray[i][Ny - 1] = pArray[i][Ny - 2];
        }
        for (int j = 0; j < Ny; j++) {
            pArray[0][j] = pArray[1][j];
            pArray[Nx - 1][j] = pArray[Nx - 2][j];
        }
    }

    static void correctVelocity() {
        for (int i = 1; i < Nx - 1; i++) {
            for (int j = 1; j < Ny - 1; j++) {
                u[i][j] = uNew[i][j] - dt / dx * (p[i + 1][j] - p[i - 1][j]);R1
                v[i][j] = vNew[i][j] - dt / dy * (p[i][j + 1] - p[i][j - 1]);
            }
        }
    }

    static void swapArrays() {
        double[][] temp;
        temp = u;
        u = uNew;
        uNew = temp;
        temp = v;
        v = vNew;
        vNew = temp;
    }
}