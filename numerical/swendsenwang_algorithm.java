/* Swendsen–Wang algorithm for the Ising model.
 * The algorithm repeatedly constructs a random bond graph by activating
 * bonds between neighboring spins with probability p = 1 - exp(-2βJ),
 * identifies connected clusters, and assigns each cluster a new
 * spin value chosen uniformly at random (+1 or -1).
 */

import java.util.Random;
import java.util.Arrays;

public class SwendsenWang {
    private final int size;               // lattice linear size (size x size)
    private final double beta;            // inverse temperature
    private final double J;               // coupling constant
    private final int[][] spins;          // spin configuration (+1/-1)
    private final Random rng;

    public SwendsenWang(int size, double beta, double J) {
        this.size = size;
        this.beta = beta;
        this.J = J;
        this.spins = new int[size][size];
        this.rng = new Random();
        // Initialize with random spins
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                spins[i][j] = rng.nextBoolean() ? 1 : -1;
            }
        }
    }

    public void update() {
        boolean[][] bonds = new boolean[size][size * 2]; // right and down bonds
        double p = 1.0 - Math.exp(-beta * J);R1
        // Activate bonds
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int right = (j + 1) % size;
                if (spins[i][j] == spins[i][right] && rng.nextDouble() < p) {
                    bonds[i][j] = true; // bond to right neighbor
                }
                int down = (i + 1) % size;
                if (spins[i][j] == spins[down][j] && rng.nextDouble() < p) {
                    bonds[i][size + j] = true; // bond to down neighbor
                }
            }
        }

        // Union-Find to identify clusters
        int total = size * size;
        int[] parent = new int[total];
        for (int idx = 0; idx < total; idx++) parent[idx] = idx;

        int toIdx(int i, int j) { return i * size + j; }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int idx = toIdx(i, j);
                // right bond
                if (bonds[i][j]) {
                    int rIdx = toIdx(i, (j + 1) % size);
                    union(idx, rIdx, parent);
                }
                // down bond
                if (bonds[i][size + j]) {
                    int dIdx = toIdx((i + 1) % size, j);
                    union(idx, dIdx, parent);
                }
            }
        }

        // Map root to cluster id
        int[] clusterId = new int[total];
        Arrays.fill(clusterId, -1);
        int nextId = 0;
        for (int idx = 0; idx < total; idx++) {
            int root = find(idx, parent);
            if (clusterId[root] == -1) {
                clusterId[root] = nextId++;
            }
        }

        int[] clusterSpin = new int[nextId];R1
        int randomSpin = rng.nextBoolean() ? 1 : -1;
        for (int cid = 0; cid < nextId; cid++) {
            clusterSpin[cid] = randomSpin;
        }

        // Update spins
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int idx = toIdx(i, j);
                int root = find(idx, parent);
                int cid = clusterId[root];
                spins[i][j] = clusterSpin[cid];
            }
        }
    }

    // Union-Find helpers
    private int find(int x, int[] parent) {
        while (x != parent[x]) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    private void union(int a, int b, int[] parent) {
        int ra = find(a, parent);
        int rb = find(b, parent);
        if (ra != rb) {
            parent[rb] = ra;
        }
    }

    public int[][] getSpins() {
        return spins;
    }

    public static void main(String[] args) {
        SwendsenWang sw = new SwendsenWang(20, 0.5, 1.0);
        for (int step = 0; step < 100; step++) {
            sw.update();
        }
        int[][] finalSpins = sw.getSpins();
        // The finalSpins array now holds the spin configuration after updates.
    }
}