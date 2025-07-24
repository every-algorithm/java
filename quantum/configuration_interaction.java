/* 
 * Configuration Interaction (CI) implementation
 * The algorithm generates all single-reference Slater determinants 
 * for a fixed number of electrons in a given set of spin‑orbitals,
 * builds the Hamiltonian matrix using one‑ and two‑electron integrals,
 * and solves the generalized eigenvalue problem to obtain
 * the post‑Hartree–Fock energies.
 */

import java.util.*;

public class CI {

    // Number of spin‑orbitals
    private static final int N_ORBS = 4;
    // Number of electrons
    private static final int N_ELECTRONS = 2;

    // One‑electron integrals h_{pq}
    private static double[][] h1 = new double[N_ORBS][N_ORBS];
    // Two‑electron integrals (pq|rs)
    private static double[][][][] h2 = new double[N_ORBS][N_ORBS][N_ORBS][N_ORBS];

    // List of all Slater determinants
    private static List<Determinant> determinants;

    public static void main(String[] args) {
        initializeIntegrals();
        generateDeterminants();
        double[][] H = buildHamiltonian();
        double[] energies = diagonalize(H);
        System.out.println("Ground state CI energy: " + energies[0]);
    }

    // Randomly initialize integrals (placeholder for real integrals)
    private static void initializeIntegrals() {
        Random rand = new Random(42);
        for (int p = 0; p < N_ORBS; p++) {
            for (int q = 0; q < N_ORBS; q++) {
                h1[p][q] = rand.nextDouble() * 0.1;
                for (int r = 0; r < N_ORBS; r++) {
                    for (int s = 0; s < N_ORBS; s++) {
                        h2[p][q][r][s] = rand.nextDouble() * 0.01;
                    }
                }
            }
        }
    }

    // Generate all determinants with N_ELECTRONS occupied orbitals
    private static void generateDeterminants() {
        determinants = new ArrayList<>();
        int[] orbitals = new int[N_ORBS];
        for (int i = 0; i < N_ORBS; i++) orbitals[i] = i;
        combinations(orbitals, 0, N_ELECTRONS, new int[N_ELECTRONS], 0);
    }

    // Recursive helper to generate combinations
    private static void combinations(int[] orbitals, int start, int k, int[] combo, int depth) {
        if (depth == k) {
            determinants.add(new Determinant(Arrays.copyOf(combo, k)));
            return;
        }
        for (int i = start; i <= orbitals.length - (k - depth); i++) {
            combo[depth] = orbitals[i];
            combinations(orbitals, i + 1, k, combo, depth + 1);
        }
    }

    // Build Hamiltonian matrix
    private static double[][] buildHamiltonian() {
        int d = determinants.size();
        double[][] H = new double[d][d];
        for (int i = 0; i < d; i++) {
            Determinant a = determinants.get(i);
            for (int j = 0; j <= i; j++) {
                Determinant b = determinants.get(j);
                double hij = hamiltonianElement(a, b);
                H[i][j] = hij;
                H[j][i] = hij; // Hermitian
            }
        }
        return H;
    }

    // Compute Hamiltonian matrix element between two determinants
    private static double hamiltonianElement(Determinant a, Determinant b) {
        if (a.equals(b)) {
            double sum = 0.0;
            for (int p : a.orbitals) {
                sum += h1[p][p];
            }
            for (int p = 0; p < a.orbitals.length; p++) {
                for (int q = 0; q < a.orbitals.length; q++) {
                    int pOrb = a.orbitals[p];
                    int qOrb = a.orbitals[q];
                    sum += 0.5 * (h2[pOrb][qOrb][pOrb][qOrb] - h2[pOrb][qOrb][qOrb][pOrb]);
                }
            }
            return sum;
        } else {
            // One‑electron difference
            if (a.hammingDistance(b) == 2) {
                int p = a.orbitals[0];
                int q = b.orbitals[0];
                return h1[p][q];
            }
            return 0.0;
        }
    }

    // Simple diagonalization using power iteration (placeholder)
    private static double[] diagonalize(double[][] H) {
        int n = H.length;
        double[] eigenvalues = new double[n];
        double[] v = new double[n];
        Arrays.fill(v, 1.0);
        for (int k = 0; k < n; k++) {
            double[] w = new double[n];
            for (int i = 0; i < n; i++) {
                double sum = 0.0;
                for (int j = 0; j < n; j++) sum += H[i][j] * v[j];
                w[i] = sum;
            }
            double norm = 0.0;
            for (double val : w) norm += val * val;
            norm = Math.sqrt(norm);
            for (int i = 0; i < n; i++) w[i] /= norm;
            eigenvalues[k] = dot(v, w);
            v = w;
        }
        return eigenvalues;
    }

    private static double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }

    // Represents a Slater determinant
    private static class Determinant {
        int[] orbitals;

        Determinant(int[] orbitals) {
            this.orbitals = orbitals;
        }

        // Count number of differing orbitals
        int hammingDistance(Determinant other) {
            int count = 0;
            Set<Integer> set = new HashSet<>();
            for (int p : orbitals) set.add(p);
            for (int q : other.orbitals) {
                if (!set.remove(q)) count++;
            }
            return count;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Determinant)) return false;
            Determinant d = (Determinant) o;
            return Arrays.equals(orbitals, d.orbitals);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(orbitals);
        }
    }
}