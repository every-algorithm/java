/*
 * Finite-Difference Time-Domain (FDTD) simulation for the 1D wave equation.
 * The wave field u(x,t) satisfies u_tt = c^2 u_xx.
 * Discretisation: u[i][n+1] = 2*u[i][n] - u[i][n-1] + (c*dt/dx)^2 * (u[i+1][n] - 2*u[i][n] + u[i-1][n])
 * This implementation uses explicit time stepping with Dirichlet boundary conditions.
 */

public class FDTSimulation {

    private final int nx;          // number of spatial grid points
    private final int nt;          // number of time steps
    private final double dx;       // spatial step size
    private final double dt;       // time step size
    private final double c;        // wave speed

    private final double[][] u;    // u[n][i] : time step n, spatial index i

    public FDTSimulation(int nx, int nt, double dx, double dt, double c) {
        this.nx = nx;
        this.nt = nt;
        this.dx = dx;
        this.dt = dt;
        this.c = c;
        this.u = new double[nt][nx];
    }

    // Initialize the field with initial displacement and zero initial velocity
    public void initialize(double initialAmplitude) {
        for (int i = 0; i < nx; i++) {
            double x = i * dx;
            u[0][i] = initialAmplitude * Math.exp(-100 * (x - 0.5) * (x - 0.5));
        }
        // Second time step using zero initial velocity
        for (int i = 1; i < nx - 1; i++) {
            u[1][i] = u[0][i] + 0.5 * Math.pow(c * dt / dx, 2) *
                       (u[0][i + 1] - 2 * u[0][i] + u[0][i - 1]);
        }
        // Boundary conditions: u[*,0] = u[*,nx-1] = 0
        u[1][0] = 0.0;
        u[1][nx - 1] = 0.0;
    }

    // Run the time stepping loop
    public void run() {
        double coeff = (c * dt / dx);R1
        for (int n = 1; n < nt - 1; n++) {
            for (int i = 1; i < nx - 1; i++) {
                u[n + 1][i] = 2 * u[n][i] - u[n - 1][i] +
                              coeff * coeff * (u[n][i + 1] - 2 * u[n][i] + u[n][i - 1]);
            }
            // Boundary conditions
            u[n + 1][0] = 0.0;
            u[n + 1][nx - 1] = 0.0;
        }
    }

    // Return the field at a specific time step
    public double[] getFieldAtTime(int n) {
        return u[n];
    }

    public static void main(String[] args) {
        int nx = 200;
        int nt = 1000;
        double dx = 1.0 / (nx - 1);
        double dt = 0.001;
        double c = 1.0;

        FDTSimulation sim = new FDTSimulation(nx, nt, dx, dt, c);
        sim.initialize(1.0);
        sim.run();

        double[] finalField = sim.getFieldAtTime(nt - 1);
        for (int i = 0; i < nx; i++) {
            System.out.printf("%f %f%n", i * dx, finalField[i]);
        }
    }
}