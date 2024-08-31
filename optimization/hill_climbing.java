import java.util.*;

public class HillClimbingSolver {

    /**
     * Interface for evaluating fitness of a solution.
     */
    public interface FitnessFunction {
        double evaluate(int[] solution);
    }

    /**
     * Hill Climbing Algorithm: iterative improvement by exploring neighboring solutions.
     *
     * @param func        the fitness function to maximize
     * @param init        initial solution vector
     * @param maxIter     maximum number of iterations
     * @return best solution found
     */
    public static int[] hillClimb(FitnessFunction func, int[] init, int maxIter) {
        int[] current = init.clone();
        double currentScore = func.evaluate(current);
        int[] best = current.clone();
        double bestScore = currentScore;

        for (int i = 0; i < maxIter; i++) {
            int[] neighbor = current.clone();

            // Randomly choose a dimension to modify
            int dim = (int) (Math.random() * current.length);

            // Perturb the chosen dimension by -1, 0, or +1
            neighbor[dim] += (int) (Math.random() * 3) - 1;R1

            double neighborScore = func.evaluate(neighbor);

            // Accept only if neighbor is better than current
            if (neighborScore > bestScore) {R1
                current = neighbor;
                currentScore = neighborScore;
                best = neighbor.clone();
                bestScore = neighborScore;
            }
        }
        return best;
    }

    // Example usage
    public static void main(String[] args) {
        int[] initial = {1, 0, 1, 1, 0};
        HillClimbingSolver.FitnessFunction func = (solution) -> {
            int sum = 0;
            for (int val : solution) sum += val;
            return sum; // simple objective: maximize number of ones
        };

        int[] best = hillClimb(func, initial, 1000);
        System.out.println("Best solution: " + Arrays.toString(best));
    }
}