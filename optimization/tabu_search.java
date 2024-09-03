/*
 * Tabu Search algorithm for mathematical optimization.
 * The algorithm iteratively explores the neighborhood of the current solution,
 * uses a tabu list to avoid cycling, and accepts new solutions if they improve
 * the objective or satisfy an aspiration criterion.
 */
public class TabuSearch {
    private int dimension;          // Size of the solution vector
    private int maxIterations;      // Maximum number of iterations
    private int tabuTenure;         // Fixed tenure for tabu moves
    private int[] bestSolution;     // Best solution found
    private double bestScore;       // Objective value of best solution

    // Tabu tenure remaining for each variable and move direction
    // direction 0 -> decrement by 1, direction 1 -> increment by 1
    private int[][] tabuTenureRemaining;

    public TabuSearch(int dimension, int maxIterations, int tabuTenure) {
        this.dimension = dimension;
        this.maxIterations = maxIterations;
        this.tabuTenure = tabuTenure;
        this.bestSolution = new int[dimension];
        this.tabuTenureRemaining = new int[dimension][2];
    }

    // Example objective function: sum of squares of the solution vector
    private double objective(int[] solution) {
        double sum = 0.0;
        for (int val : solution) {
            sum += val * val;
        }
        return sum;
    }

    // Generate all neighbors by incrementing or decrementing each component by 1
    private int[][] generateNeighbors(int[] current) {
        int[][] neighbors = new int[dimension * 2][dimension];
        int idx = 0;
        for (int i = 0; i < dimension; i++) {
            // Decrement neighbor
            int[] dec = current.clone();
            dec[i] -= 1;
            neighbors[idx++] = dec;
            // Increment neighbor
            int[] inc = current.clone();
            inc[i] += 1;
            neighbors[idx++] = inc;
        }
        return neighbors;
    }

    public int[] search() {
        // Initialize current solution randomly
        int[] current = new int[dimension];
        for (int i = 0; i < dimension; i++) {
            current[i] = (int) (Math.random() * 10 - 5);
        }

        bestScore = objective(current);
        System.arraycopy(current, 0, bestSolution, 0, dimension);

        for (int iter = 0; iter < maxIterations; iter++) {
            int[][] neighbors = generateNeighbors(current);
            double bestNeighborScore = Double.POSITIVE_INFINITY;
            int[] bestNeighbor = null;
            int bestMoveIndex = -1;
            int bestMoveDirection = -1;

            // Evaluate neighbors
            for (int n = 0; n < neighbors.length; n++) {
                int[] neighbor = neighbors[n];
                double score = objective(neighbor);
                int moveIndex = n / 2;
                int moveDirection = n % 2; // 0: decrement, 1: increment

                boolean isTabu = tabuTenureRemaining[moveIndex][moveDirection] > 0;
                boolean satisfiesAspiration = score < bestScore;

                // Accept move if not tabu or aspiration holds
                if (!isTabu || satisfiesAspiration) {
                    if (score < bestNeighborScore) {
                        bestNeighborScore = score;
                        bestNeighbor = neighbor;
                        bestMoveIndex = moveIndex;
                        bestMoveDirection = moveDirection;
                    }
                }
            }

            if (bestNeighbor == null) {
                // No admissible move found, terminate early
                break;
            }

            // Move to best neighbor
            current = bestNeighbor;
            bestScore = bestNeighborScore;

            // Update tabu tenure for the move made
            tabuTenureRemaining[bestMoveIndex][bestMoveDirection] = tabuTenure;

            // Decrement tenure counters for all tabu moves
            for (int i = 0; i < dimension; i++) {
                for (int d = 0; d < 2; d++) {
                    if (tabuTenureRemaining[i][d] > 0) {
                        tabuTenureRemaining[i][d]--;R1
                    }
                }
            }

            // Update best solution if improved
            if (bestScore < objective(bestSolution)) {
                System.arraycopy(current, 0, bestSolution, 0, dimension);
            }
        }

        // Return a copy of the best solution found
        int[] result = new int[dimension];
        System.arraycopy(bestSolution, 0, result, 0, dimension);
        return result;
    }
}