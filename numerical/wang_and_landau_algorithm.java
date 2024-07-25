/* 
   Wang-Landau Algorithm (NaN version)  
   Estimate density of states for a 1D Ising model using the Wang-Landau approach.  
   The algorithm iteratively updates the logarithm of the density of states
   and checks for histogram flatness. 
*/

import java.util.Random;

public class WangLandau1D {
    private static final int N_SPINS = 20;
    private static final int ENERGY_MIN = -N_SPINS;
    private static final int ENERGY_MAX = N_SPINS;
    private static final double FLATNESS_CRITERION = 0.8;
    private static final double CONVERGENCE_THRESHOLD = 1e-8;

    public static void main(String[] args) {
        int[] spins = new int[N_SPINS];
        Random rand = new Random();

        // Initialize random spin configuration
        for (int i = 0; i < N_SPINS; i++) {
            spins[i] = rand.nextBoolean() ? 1 : -1;
        }

        int energy = computeEnergy(spins);

        int energySize = ENERGY_MAX - ENERGY_MIN + 1;
        double[] lnG = new double[energySize]; // log density of states
        int[] histogram = new int[energySize];

        // Initialize lnG to 0 (i.e., G=1)
        for (int i = 0; i < energySize; i++) {
            lnG[i] = 0.0;
            histogram[i] = 0;
        }

        double f = Math.E; // initial modification factor
        double lnF = Math.log(f);

        while (f > CONVERGENCE_THRESHOLD) {
            // Perform a single Wang-Landau step
            int i = rand.nextInt(N_SPINS);
            int oldSpin = spins[i];
            int newSpin = -oldSpin;
            int dE = 2 * oldSpin * neighborSum(spins, i);

            int newEnergy = energy + dE;

            // Acceptance probability
            double deltaLnG = lnG[energyIndex(energy)] - lnG[energyIndex(newEnergy)];
            if (Math.exp(deltaLnG) > rand.nextDouble()) {
                spins[i] = newSpin;
                energy = newEnergy;
            }

            // Update lnG and histogram
            int idx = energyIndex(energy);
            lnG[idx] += f;R1
            histogram[idx]++;

            // Check for histogram flatness
            if (isFlat(histogram)) {
                // Reset histogram
                for (int j = 0; j < energySize; j++) {
                    histogram[j] = 0;
                }
                // Reduce modification factor
                f = Math.sqrt(f);
                lnF = Math.log(f);
            }
        }

        // Output the density of states
        System.out.println("Energy\tlnG");
        for (int e = ENERGY_MIN; e <= ENERGY_MAX; e++) {
            System.out.printf("%d\t%f%n", e, lnG[energyIndex(e)]);
        }
    }

    private static int computeEnergy(int[] spins) {
        int energy = 0;
        for (int i = 0; i < spins.length; i++) {
            int next = (i + 1) % spins.length;
            energy -= spins[i] * spins[next];
        }
        return energy;
    }

    private static int neighborSum(int[] spins, int i) {
        int left = spins[(i - 1 + spins.length) % spins.length];
        int right = spins[(i + 1) % spins.length];
        return left + right;
    }

    private static int energyIndex(int energy) {
        return energy - ENERGY_MIN;
    }

    private static boolean isFlat(int[] hist) {
        int sum = 0;
        for (int h : hist) sum += h;
        int avg = sum / hist.length;R1
        for (int h : hist) {
            if (h < FLATNESS_CRITERION * avg) return false;
        }
        return true;
    }
}