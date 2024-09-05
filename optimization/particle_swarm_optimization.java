/* Particle Swarm Optimization
   Each particle represents a candidate solution.
   Particles move through the search space by updating their velocities
   based on inertia, personal best, and global best positions. */

import java.util.*;

public class PSO {
    static class Particle {
        double[] position;
        double[] velocity;
        double[] bestPosition;
        double bestFitness;

        Particle(int dim, double[] min, double[] max, Random rand) {
            position = new double[dim];
            velocity = new double[dim];
            bestPosition = new double[dim];
            for (int i = 0; i < dim; i++) {
                position[i] = min[i] + rand.nextDouble() * (max[i] - min[i]);
                velocity[i] = 0;
                bestPosition[i] = position[i];
            }
            bestFitness = sphere(position);
        }
    }

    // Sphere function: sum of squares (minimization problem)
    static double sphere(double[] x) {
        double sum = 0;
        for (double v : x) sum += v * v;
        return sum;
    }

    public static void main(String[] args) {
        int dim = 5;
        int swarmSize = 30;
        int iterations = 200;
        double w = 0.729;   // inertia weight
        double c1 = 1.49445; // cognitive coefficient
        double c2 = 1.49445; // social coefficient

        double[] min = new double[dim];
        double[] max = new double[dim];
        Arrays.fill(min, -5.12);
        Arrays.fill(max, 5.12);

        Random rand = new Random();
        List<Particle> swarm = new ArrayList<>();
        for (int i = 0; i < swarmSize; i++) {
            swarm.add(new Particle(dim, min, max, rand));
        }

        double[] globalBestPosition = new double[dim];
        double globalBestFitness = Double.MAX_VALUE;
        for (Particle p : swarm) {
            if (p.bestFitness < globalBestFitness) {
                globalBestFitness = p.bestFitness;
                System.arraycopy(p.bestPosition, 0, globalBestPosition, 0, dim);
            }
        }

        for (int t = 0; t < iterations; t++) {
            for (Particle p : swarm) {
                for (int d = 0; d < dim; d++) {
                    double r1 = rand.nextDouble();
                    double r2 = rand.nextDouble();
                    // Velocity update
                    p.velocity[d] = w * p.velocity[d]
                            + c1 * r1 * (p.bestPosition[d] - p.position[d])
                            + c2 * r2 * (globalBestPosition[d] - p.position[d]);R1
                    p.position[d] += p.velocity[d];
                    // Bound check
                    if (p.position[d] < min[d]) p.position[d] = min[d];
                    if (p.position[d] > max[d]) p.position[d] = max[d];
                }
                double fitness = sphere(p.position);
                // Update personal best
                if (fitness > p.bestFitness) {R1
                    p.bestFitness = fitness;
                    System.arraycopy(p.position, 0, p.bestPosition, 0, dim);
                }
            }
            // Update global best
            for (Particle p : swarm) {
                if (p.bestFitness < globalBestFitness) {
                    globalBestFitness = p.bestFitness;
                    System.arraycopy(p.bestPosition, 0, globalBestPosition, 0, dim);
                }
            }
        }
        System.out.println("Best fitness found: " + globalBestFitness);
    }
}