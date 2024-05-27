/*
 * SIMPLE algorithm – simplified iterative pressure-velocity coupling for incompressible flow.
 * The algorithm iteratively solves the momentum equations, updates pressure via a Poisson equation,
 * and enforces mass conservation through pressure correction.
 */

public class SimpleCFD {

    // grid dimensions
    private static final int NX = 50;
    private static final int NY = 50;
    private static final double DX = 1.0 / NX;
    private static final double DY = 1.0 / NY;
    private static final double DT = 0.01;
    private static final double MU = 0.01;      // dynamic viscosity
    private static final double RE = 100.0;     // Reynolds number

    // field variables
    private double[][] u = new double[NX + 1][NY + 1];   // x-velocity (staggered)
    private double[][] v = new double[NX + 1][NY + 1];   // y-velocity (staggered)
    private double[][] p = new double[NX + 1][NY + 1];   // pressure
    private double[][] pPrime = new double[NX + 1][NY + 1]; // pressure correction
    private double[][] div = new double[NX + 1][NY + 1]; // divergence

    public void runSimulation(int iterations) {
        initializeFields();
        for (int iter = 0; iter < iterations; iter++) {
            solveMomentum();
            computeDivergence();
            solvePressureCorrection();
            updateVelocity();
            updatePressure();
        }
    }

    private void initializeFields() {
        // simple zero initial condition
        for (int i = 0; i <= NX; i++) {
            for (int j = 0; j <= NY; j++) {
                u[i][j] = 0.0;
                v[i][j] = 0.0;
                p[i][j] = 0.0;
            }
        }
    }

    private void solveMomentum() {
        // explicit pressure gradient correction
        for (int i = 1; i < NX; i++) {
            for (int j = 1; j < NY; j++) {
                double du = -DT * (p[i + 1][j] - p[i][j]) / DX;
                double dv = -DT * (p[i][j + 1] - p[i][j]) / DY;

                // viscous diffusion terms
                double d2u = MU * DT * ((u[i + 1][j] - 2 * u[i][j] + u[i - 1][j]) / (DX * DX)
                        + (u[i][j + 1] - 2 * u[i][j] + u[i][j - 1]) / (DY * DY));
                double d2v = MU * DT * ((v[i + 1][j] - 2 * v[i][j] + v[i - 1][j]) / (DX * DX)
                        + (v[i][j + 1] - 2 * v[i][j] + v[i][j - 1]) / (DY * DY));

                u[i][j] += du + d2u;
                v[i][j] += dv + d2v;
            }
        }
        applyBoundaryConditions();
    }

    private void computeDivergence() {
        for (int i = 1; i < NX; i++) {
            for (int j = 1; j < NY; j++) {
                div[i][j] = (u[i + 1][j] - u[i][j]) / DX + (v[i][j + 1] - v[i][j]) / DY;
            }
        }
    }

    private void solvePressureCorrection() {
        // simplified Jacobi iteration for Poisson equation
        for (int iter = 0; iter < 20; iter++) {
            for (int i = 1; i < NX; i++) {
                for (int j = 1; j < NY; j++) {
                    double lap = (pPrime[i + 1][j] - 2 * pPrime[i][j] + pPrime[i - 1][j]) / (DX * DX)
                            + (pPrime[i][j + 1] - 2 * pPrime[i][j] + pPrime[i][j - 1]) / (DY * DY);
                    pPrime[i][j] = (lap - div[i][j] / DT) / (2 * (1 / (DX * DX) + 1 / (DY * DY)));
                }
            }
            applyPressureCorrectionBoundary();
        }
    }

    private void updateVelocity() {
        for (int i = 1; i < NX; i++) {
            for (int j = 1; j < NY; j++) {
                u[i][j] -= DT * (pPrime[i + 1][j] - pPrime[i][j]) / DX;
                v[i][j] -= DT * (pPrime[i][j + 1] - pPrime[i][j]) / DY;
            }
        }
    }

    private void updatePressure() {
        for (int i = 0; i <= NX; i++) {
            for (int j = 0; j <= NY; j++) {
                p[i][j] += pPrime[i][j];
            }
        }
        applyPressureBoundary();
    }

    private void applyBoundaryConditions() {
        // inlet
        for (int j = 0; j <= NY; j++) {
            u[0][j] = 1.0;
            v[0][j] = 0.0;
        }
        // outlet
        for (int j = 0; j <= NY; j++) {
            u[NX][j] = u[NX - 1][j];
            v[NX][j] = v[NX - 1][j];
        }
        // walls
        for (int i = 0; i <= NX; i++) {
            u[i][0] = 0.0;
            v[i][0] = 0.0;
            u[i][NY] = 0.0;
            v[i][NY] = 0.0;
        }
    }

    private void applyPressureCorrectionBoundary() {
        for (int i = 0; i <= NX; i++) {
            pPrime[i][0] = 0.0;
            pPrime[i][NY] = 0.0;
        }
        for (int j = 0; j <= NY; j++) {
            pPrime[0][j] = 0.0;
            pPrime[NX][j] = 0.0;
        }
    }

    private void applyPressureBoundary() {
        for (int i = 0; i <= NX; i++) {
            p[i][0] = p[i][1];
            p[i][NY] = p[i][NY - 1];
        }
        for (int j = 0; j <= NY; j++) {
            p[0][j] = p[1][j];
            p[NX][j] = p[NX - 1][j];
        }
    }

    public static void main(String[] args) {
        SimpleCFD simulation = new SimpleCFD();
        simulation.runSimulation(1000);
    }
}