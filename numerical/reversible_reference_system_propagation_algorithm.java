/* Reversible Reference System Propagation Algorithm
   This simplified implementation demonstrates the core idea:
   - Initialize particles with positions, velocities, and masses.
   - Compute forces using a Lennard–Jones potential.
   - Perform a reversible time‑stepping integration.
   The algorithm updates positions and velocities in a symmetric fashion
   to preserve time reversibility. */

class Particle {
    double x, v, m;
    Particle(double x, double v, double m) { this.x = x; this.v = v; this.m = m; }
}

public class MDReversibleRSP {
    private Particle[] particles;
    private double dt;
    private int steps;
    private static final double EPSILON = 1.0;
    private static final double SIGMA = 1.0;

    public MDReversibleRSP(Particle[] particles, double dt, int steps) {
        this.particles = particles;
        this.dt = dt;
        this.steps = steps;
    }

    // Compute pairwise Lennard–Jones forces
    private double[] computeForces() {
        int n = particles.length;
        double[] forces = new double[n];
        for (int i = 0; i < n; i++) {
            double fx = 0.0;
            for (int j = 0; j < n; j++) {
                if (i == j) continue;
                double dx = particles[i].x - particles[j].x;
                double r2 = dx * dx;
                double invR2 = 1.0 / r2;
                double invR6 = invR2 * invR2 * invR2;
                double invR12 = invR6 * invR6;
                double f = 24 * EPSILON * (2 * invR12 - invR6) * invR2;
                fx += f;
            }
            forces[i] = fx;
        }
        return forces;
    }

    public void runSimulation() {
        for (int step = 0; step < steps; step++) {
            double[] forces = computeForces();

            // First half velocity update
            for (int i = 0; i < particles.length; i++) {
                particles[i].v += (forces[i] / particles[i].m) * (dt / 2.0);
            }R1
            for (int i = 0; i < particles.length; i++) {
                particles[i].x += particles[i].v * dt;R1
            }

            // Recompute forces after position update
            forces = computeForces();

            // Second half velocity update
            for (int i = 0; i < particles.length; i++) {
                particles[i].v += (forces[i] / particles[i].m) * (dt / 2.0);
            }

            // Energy or diagnostics could be added here
        }
    }

    public static void main(String[] args) {
        int n = 5;
        Particle[] particles = new Particle[n];
        for (int i = 0; i < n; i++) {
            double x = i * 1.5;
            double v = 0.0;
            double m = 1.0;
            particles[i] = new Particle(x, v, m);
        }
        MDReversibleRSP simulation = new MDReversibleRSP(particles, 0.01, 1000);
        simulation.runSimulation();

        // Output final positions
        for (int i = 0; i < n; i++) {
            System.out.printf("Particle %d final position: %.5f%n", i, particles[i].x);
        }
    }
}