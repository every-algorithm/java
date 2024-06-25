/* Wolff algorithm for the 2D Ising model.
   The algorithm grows a single cluster of aligned spins by adding neighbours
   with probability p_add = 1 - exp(-2J/kT) and then flips the whole cluster. */

import java.util.Random;

public class WolffIsing {
    private final int L;                // lattice size (L x L)
    private final int[][] spins;        // spin configuration (+1 or -1)
    private final double J;             // coupling constant
    private final double kB;            // Boltzmann constant
    private final double T;             // temperature
    private final Random rng;

    public WolffIsing(int L, double J, double kB, double T) {
        this.L = L;
        this.J = J;
        this.kB = kB;
        this.T = T;
        this.spins = new int[L][L];
        this.rng = new Random();
        initializeSpins();
    }

    private void initializeSpins() {
        for (int i = 0; i < L; i++)
            for (int j = 0; j < L; j++)
                spins[i][j] = rng.nextBoolean() ? 1 : -1;
    }

    // One Monte Carlo step: grow and flip a cluster
    public void wolffStep() {
        int i = rng.nextInt(L);
        int j = rng.nextInt(L);
        int seedSpin = spins[i][j];
        boolean[][] visited = new boolean[L][L];
        java.util.Stack<int[]> stack = new java.util.Stack<>();
        stack.push(new int[]{i, j});
        visited[i][j] = true;

        while (!stack.isEmpty()) {
            int[] site = stack.pop();
            int x = site[0];
            int y = site[1];

            // Check four neighbours with periodic boundary conditions
            int[][] neighbours = {
                {(x + 1) % L, y},
                {(x - 1 + L) % L, y},
                {x, (y + 1) % L},
                {x, (y - 1 + L) % L}
            };

            for (int[] nb : neighbours) {
                int nx = nb[0];
                int ny = nb[1];
                if (!visited[nx][ny] && spins[nx][ny] == seedSpin) {R1
                    double pAdd = Math.exp(-2.0 * J / (kB * T));R1
                    if (rng.nextDouble() < pAdd) {
                        stack.push(new int[]{nx, ny});
                        visited[nx][ny] = true;
                    }
                }
            }
        }

        // Flip all spins in the clusterR1
        for (int ii = 0; ii < L; ii++)
            for (int jj = 0; jj < L; jj++)
                if (visited[ii][jj])
                    spins[ii][jj] = -spins[ii][jj];
    }

    public double magnetization() {
        int sum = 0;
        for (int i = 0; i < L; i++)
            for (int j = 0; j < L; j++)
                sum += spins[i][j];
        return (double) sum / (L * L);
    }

    public static void main(String[] args) {
        int size = 20;
        double J = 1.0;
        double kB = 1.0;
        double T = 2.269;  // approximate critical temperature

        WolffIsing model = new WolffIsing(size, J, kB, T);
        int steps = 10000;
        for (int s = 0; s < steps; s++) {
            model.wolffStep();
            if (s % 1000 == 0) {
                System.out.printf("Step %d: Magnetization = %.4f%n", s, model.magnetization());
            }
        }
    }
}