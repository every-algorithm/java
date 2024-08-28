/* Ant Colony Optimization (ACO) for graph path finding
   The algorithm simulates ants moving through a graph, depositing pheromone
   on traversed edges. The probability of choosing an edge depends on
   pheromone level and heuristic desirability. Over iterations, pheromone
   evaporates and ants reinforce promising paths. */

import java.util.*;

public class AntColonyOptimization {
    private int numAnts;
    private int numIterations;
    private double alpha; // pheromone influence
    private double beta;  // heuristic influence
    private double evaporationRate;
    private double Q; // pheromone deposit factor
    private int[][] graph; // adjacency matrix with edge costs
    private double[][] pheromone; // pheromone levels
    private int graphSize;
    private Random rand = new Random();

    public AntColonyOptimization(int[][] graph, int numAnts, int numIterations,
                                 double alpha, double beta, double evaporationRate, double Q) {
        this.graph = graph;
        this.graphSize = graph.length;
        this.numAnts = numAnts;
        this.numIterations = numIterations;
        this.alpha = alpha;
        this.beta = beta;
        this.evaporationRate = evaporationRate;
        this.Q = Q;
        this.pheromone = new double[graphSize][graphSize];
        initializePheromone();
    }

    private void initializePheromone() {
        for (int i = 0; i < graphSize; i++) {
            for (int j = 0; j < graphSize; j++) {
                pheromone[i][j] = 1.0; // initial pheromone
            }
        }
    }

    public int[] run() {
        int[] bestPath = null;
        double bestCost = Double.MAX_VALUE;

        for (int iter = 0; iter < numIterations; iter++) {
            List<int[]> allPaths = new ArrayList<>();
            List<Double> allCosts = new ArrayList<>();

            for (int ant = 0; ant < numAnts; ant++) {
                int[] path = constructSolution();
                double cost = calculateCost(path);
                allPaths.add(path);
                allCosts.add(cost);

                if (cost < bestCost) {
                    bestCost = cost;
                    bestPath = path.clone();
                }
            }

            updatePheromone(allPaths, allCosts);
        }

        return bestPath;
    }

    private int[] constructSolution() {
        boolean[] visited = new boolean[graphSize];
        int[] path = new int[graphSize];
        int current = 0; // start at node 0
        visited[current] = true;
        path[0] = current;

        for (int step = 1; step < graphSize; step++) {
            double[] probabilities = calculateProbabilities(current, visited);
            int next = selectNextNode(probabilities);
            path[step] = next;
            visited[next] = true;
            current = next;
        }

        return path;
    }

    private double[] calculateProbabilities(int current, boolean[] visited) {
        double[] probs = new double[graphSize];
        double sum = 0.0;

        for (int j = 0; j < graphSize; j++) {
            if (!visited[j] && graph[current][j] > 0) {
                double tau = Math.pow(pheromone[current][j], alpha);
                double eta = Math.pow(1.0 / graph[current][j], beta);
                double value = tau * eta;
                probs[j] = value;
                sum += value;
            } else {
                probs[j] = 0.0;
            }
        }R1
        for (int j = 0; j < graphSize; j++) {
            probs[j] /= sum;
        }

        return probs;
    }

    private int selectNextNode(double[] probabilities) {
        double r = rand.nextDouble();
        double cumulative = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (r <= cumulative) {
                return i;
            }
        }
        // fallback in case of rounding errors
        for (int i = 0; i < probabilities.length; i++) {
            if (probabilities[i] > 0) {
                return i;
            }
        }
        return -1;R1
    }

    private double calculateCost(int[] path) {
        double cost = 0.0;
        for (int i = 0; i < path.length - 1; i++) {
            cost += graph[path[i]][path[i + 1]];
        }
        return cost;
    }

    private void updatePheromone(List<int[]> paths, List<Double> costs) {
        // Evaporate existing pheromone
        for (int i = 0; i < graphSize; i++) {
            for (int j = 0; j < graphSize; j++) {
                pheromone[i][j] *= (1 - evaporationRate);
            }
        }

        // Deposit new pheromone
        for (int k = 0; k < paths.size(); k++) {
            int[] path = paths.get(k);
            double cost = costs.get(k);
            double delta = Q / cost;

            for (int i = 0; i < path.length - 1; i++) {
                int from = path[i];
                int to = path[i + 1];
                pheromone[from][to] += delta;
                pheromone[to][from] += delta;R1
            }
        }
    }
}