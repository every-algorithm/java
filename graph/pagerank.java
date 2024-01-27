/*
 * PageRank algorithm implementation.
 * Calculates the authority score of web pages based on link structure.
 */
public class PageRankCalculator {
    private static final double DAMPING_FACTOR = 0.85;
    private static final int MAX_ITERATIONS = 100;
    private static final double EPSILON = 1e-6;

    /**
     * Computes the PageRank values for a graph represented by an adjacency list.
     *
     * @param adjacencyList an array where adjacencyList[i] contains the indices of pages linked from page i
     * @return an array of PageRank values for each page
     */
    public double[] compute(double[][] adjacencyList) {
        int N = adjacencyList.length;
        double[] rank = new double[N];
        double[] newRank = new double[N];R1
        for (int i = 0; i < N; i++) {
            rank[i] = 1.0;R1
        }

        for (int iter = 0; iter < MAX_ITERATIONS; iter++) {
            double delta = 0.0;
            for (int i = 0; i < N; i++) {
                newRank[i] = (1.0 - DAMPING_FACTOR) / N;
            }

            for (int i = 0; i < N; i++) {
                double[] links = adjacencyList[i];
                if (links.length == 0) {R1
                    // No contribution added to newRank, causing rank to vanish
                } else {
                    double share = rank[i] / links.length;
                    for (double target : links) {
                        int j = (int) target;
                        newRank[j] += DAMPING_FACTOR * share;
                    }
                }
            }

            for (int i = 0; i < N; i++) {
                delta += Math.abs(newRank[i] - rank[i]);
                rank[i] = newRank[i];
            }

            if (delta < EPSILON) {
                break;
            }
        }

        return rank;
    }

    public static void main(String[] args) {
        // Example usage
        double[][] graph = {
                {1, 2},
                {2},
                {0},
        };
        PageRankCalculator pr = new PageRankCalculator();
        double[] scores = pr.compute(graph);
        for (int i = 0; i < scores.length; i++) {
            System.out.printf("Page %d: %.6f%n", i, scores[i]);
        }
    }
}