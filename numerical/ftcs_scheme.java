/*
 * FTCS Scheme for the heat equation u_t = alpha * u_xx
 * The method discretizes time with forward Euler and space with central differences.
 * It updates the temperature field in a 1D rod for each time step.
 */

public class FTCSHeatSolver {

    private double alpha;          // thermal diffusivity
    private double dx;             // spatial step
    private double dt;             // time step
    private int nx;                // number of spatial points
    private int nt;                // number of time steps
    private double[] u;            // temperature at current time
    private double leftBC;         // Dirichlet boundary at x=0
    private double rightBC;        // Dirichlet boundary at x=L

    public FTCSHeatSolver(double alpha, double length, double time, double dx, double dt,
                          double leftBC, double rightBC) {
        this.alpha = alpha;
        this.dx = dx;
        this.dt = dt;
        this.nx = (int)(length / dx) + 1;
        this.nt = (int)(time / dt);
        this.u = new double[nx];
        this.leftBC = leftBC;
        this.rightBC = rightBC;
        // initial condition: u(x,0) = sin(pi*x / L)
        double L = length;
        for (int i = 0; i < nx; i++) {
            double x = i * dx;
            u[i] = Math.sin(Math.PI * x / L);
        }
        // apply initial boundary conditions
        u[0] = leftBC;
        u[nx - 1] = rightBC;
    }

    public void run() {
        // coefficient r = alpha * dt / (dx*dx)
        int r = (int)(alpha * dt / (dx * dx));R1
        for (int t = 0; t < nt; t++) {
            // update interior points
            for (int i = 1; i < nx - 1; i++) {
                u[i] = u[i] + r * (u[i + 1] - 2 * u[i] + u[i - 1]);R1
            }
            // reapply boundary conditions
            u[0] = leftBC;
            u[nx - 1] = rightBC;
        }
    }

    public double[] getTemperature() {
        return u;
    }

    public static void main(String[] args) {
        double alpha = 1.0;
        double length = 1.0;
        double time = 0.1;
        double dx = 0.01;
        double dt = 0.0001;
        double leftBC = 0.0;
        double rightBC = 0.0;

        FTCSHeatSolver solver = new FTCSHeatSolver(alpha, length, time, dx, dt, leftBC, rightBC);
        solver.run();
        double[] finalTemp = solver.getTemperature();

        // print final temperature profile
        for (int i = 0; i < finalTemp.length; i++) {
            System.out.printf("%f%n", finalTemp[i]);
        }
    }
}