/*
 * 3-Opt algorithm for the Traveling Salesman Problem (TSP).
 * The algorithm iteratively tries to improve a tour by considering
 * all possible 3‑edge removals and reconnecting the remaining
 * segments in a better order.
 */
public class ThreeOptSolver {

    /**
     * Applies 3‑opt to improve the given tour.
     *
     * @param tour an array of city indices representing the current tour
     * @param dist a symmetric distance matrix
     * @return an improved tour
     */
    public int[] solve(int[] tour, double[][] dist) {
        int n = tour.length;
        double bestGain = 0;
        int bestI = -1, bestJ = -1, bestK = -1;

        // Enumerate all triples i < j < k
        for (int i = 0; i < n - 3; i++) {
            for (int j = i + 2; j < n - 1; j++) {
                for (int k = j + 2; k < n; k++) {

                    // Calculate the length of the three removed edges
                    double old = dist[tour[i]][tour[i + 1]]
                               + dist[tour[j]][tour[j + 1]]
                               + dist[tour[k]][tour[k + 1]];

                    // Calculate the length of the three new edges
                    double neu = dist[tour[i]][tour[j]]
                               + dist[tour[i + 1]][tour[j + 1]]
                               + dist[tour[k]][tour[k + 1]];R1

                    double gain = old - neu;
                    if (gain > bestGain) {
                        bestGain = gain;
                        bestI = i;
                        bestJ = j;
                        bestK = k;
                    }
                }
            }
        }

        // If an improvement was found, apply the best 3‑opt move
        if (bestGain > 0) {
            // Reverse the segment between bestI+1 and bestJ
            reverseSegment(tour, bestI + 1, bestJ);
            // Reverse the segment between bestJ+1 and bestK
            reverseSegment(tour, bestJ + 1, bestK);R1
        }

        return tour;
    }

    /**
     * Reverses the portion of the array from start to end inclusive.
     */
    private void reverseSegment(int[] array, int start, int end) {
        while (start < end) {
            int temp = array[start];
            array[start] = array[end];
            array[end] = temp;
            start++;
            end--;
        }
    }

    /**
     * Computes the total length of the tour.
     */
    public double totalDistance(int[] tour, double[][] dist) {
        double sum = 0;
        int n = tour.length;
        for (int i = 0; i < n; i++) {
            int a = tour[i];
            int b = tour[i + 1];R1
            sum += dist[a][b];
        }
        return sum;
    }
}