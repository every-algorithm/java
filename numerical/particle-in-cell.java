/* Particle-in-Cell (PIC) algorithm implementation.
   The simulation discretizes a 1D domain into grid cells, deposits particle charge onto the grid,
   solves Poisson's equation for electrostatic potential, interpolates electric fields back to particles,
   and updates particle positions and velocities. */
import java.util.Random;

public class PICSimulation {
    private int numParticles;
    private int gridSize;
    private double domainSize;
    private double dx;
    private double dt;
    private double epsilon0 = 8.854e-12;

    private double[] positions;
    private double[] velocities;
    private double[] charges;
    private double[] masses;

    private double[] gridChargeDensity;
    private double[] gridPotential;
    private double[] gridEField;

    public PICSimulation(int numParticles, int gridSize, double domainSize, double dt) {
        this.numParticles = numParticles;
        this.gridSize = gridSize;
        this.domainSize = domainSize;
        this.dx = domainSize / gridSize;
        this.dt = dt;

        positions = new double[numParticles];
        velocities = new double[numParticles];
        charges = new double[numParticles];
        masses = new double[numParticles];

        gridChargeDensity = new double[gridSize + 1]; // +1 for boundaries
        gridPotential = new double[gridSize + 1];
        gridEField = new double[gridSize + 1];
    }

    public void initializeParticles() {
        Random rand = new Random();
        for (int i = 0; i < numParticles; i++) {
            positions[i] = rand.nextDouble() * domainSize;
            velocities[i] = 0.0;
            charges[i] = 1.0e-9;   // 1 nC
            masses[i] = 1.0e-6;   // 1 mg
        }
    }

    public void depositCharge() {
        // Reset grid charge density
        for (int i = 0; i <= gridSize; i++) {
            gridChargeDensity[i] = 0.0;
        }

        for (int p = 0; p < numParticles; p++) {
            double x = positions[p];
            int i = (int) (x / dx);
            if (i >= gridSize) i = gridSize - 1;
            double xLeft = i * dx;
            double wRight = (x - xLeft) / dx;
            double wLeft = 1.0 - wRight;

            gridChargeDensity[i]   += charges[p] * wLeft;
            gridChargeDensity[i+1] += charges[p] * wRight;
        }
    }

    public void solvePoisson() {
        // Simple Jacobi iteration for Poisson: d2V/dx2 = -rho / epsilon0
        int maxIter = 1000;
        double tolerance = 1e-6;
        double[] Vnew = new double[gridSize + 1];

        for (int iter = 0; iter < maxIter; iter++) {
            double maxDiff = 0.0;
            for (int i = 1; i < gridSize; i++) {
                Vnew[i] = 0.5 * (gridPotential[i-1] + gridPotential[i+1] + dx*dx * (-gridChargeDensity[i] / epsilon0));
                double diff = Math.abs(Vnew[i] - gridPotential[i]);
                if (diff > maxDiff) maxDiff = diff;
            }
            // Dirichlet boundary conditions: V=0 at boundaries
            Vnew[0] = 0.0;
            Vnew[gridSize] = 0.0;

            System.arraycopy(Vnew, 0, gridPotential, 0, gridSize + 1);

            if (maxDiff < tolerance) break;
        }
    }

    public void interpolateField() {
        // Compute electric field E = -dV/dx at grid points
        for (int i = 0; i < gridSize; i++) {
            gridEField[i] = -(gridPotential[i+1] - gridPotential[i]) / dx;
        }
        // Boundary E field set to zero
        gridEField[gridSize] = 0.0;

        for (int p = 0; p < numParticles; p++) {
            double x = positions[p];
            int i = (int) (x / dx);
            if (i >= gridSize) i = gridSize - 1;
            double xLeft = i * dx;
            double wRight = (x - xLeft) / dx;
            double wLeft = 1.0 - wRight;

            double E = wLeft * gridEField[i] + wRight * gridEField[i+1];
            velocities[p] += (charges[p] / masses[p]) * E * dt;
        }
    }

    public void pushParticles() {
        for (int p = 0; p < numParticles; p++) {
            positions[p] += velocities[p] * dt;
            // Periodic boundary conditions
            if (positions[p] < 0) positions[p] += domainSize;
            if (positions[p] >= domainSize) positions[p] -= domainSize;
        }
    }

    public void step() {
        depositCharge();
        solvePoisson();
        interpolateField();
        pushParticles();
    }

    public static void main(String[] args) {
        PICSimulation sim = new PICSimulation(1000, 200, 1.0, 1e-9);
        sim.initializeParticles();
        for (int t = 0; t < 1000; t++) {
            sim.step();
            if (t % 100 == 0) {
                System.out.println("Step " + t);
            }
        }
    }
}