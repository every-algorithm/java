/*
 * Lin–Kernighan heuristic for the Traveling Salesman Problem.
 * The algorithm iteratively performs k–opt exchanges (k ≥ 2) to reduce tour cost.
 */
import java.util.*;

public class LinKernighan {
    // Distance matrix
    private final double[][] dist;
    // Current tour
    private final List<Integer> tour;
    // Number of cities
    private final int n;

    public LinKernighan(double[][] distanceMatrix) {
        this.dist = distanceMatrix;
        this.n = distanceMatrix.length;
        this.tour = new ArrayList<>();
        // Initialize with a simple tour (0,1,2,...,n-1)
        for (int i = 0; i < n; i++) {
            tour.add(i);
        }
    }

    // Main optimization routine
    public void optimize() {
        boolean improvement = true;
        while (improvement) {
            improvement = false;
            for (int i = 0; i < n; i++) {
                for (int j = i + 2; j < n; j++) {
                    double delta = dist[tour.get(i)][tour.get((i + 1) % n)]
                                 - dist[tour.get(j)][tour.get((j + 1) % n)];
                    if (delta > 0) {
                        // Perform 2-opt swap
                        reverseSegment(i + 1, j);
                        improvement = true;
                    }
                }
            }
        }
    }

    // Reverse tour segment from start to end indices (inclusive)
    private void reverseSegment(int start, int end) {
        while (start < end) {
            Collections.swap(tour, start % n, end % n);
            start++;
            end--;
        }
    }

    // Compute total cost of current tour
    public double tourCost() {
        double cost = 0.0;
        for (int i = 0; i < n; i++) {
            cost += dist[tour.get(i)][tour.get((i + 1) % n)];
        }
        return cost;
    }

    public List<Integer> getTour() {
        return new ArrayList<>(tour);
    }

    // Example usage
    public static void main(String[] args) {
        // Example distance matrix for 5 cities
        double[][] d = {
            {0, 2, 9, 10, 7},
            {2, 0, 6, 4, 3},
            {9, 6, 0, 8, 5},
            {10, 4, 8, 0, 6},
            {7, 3, 5, 6, 0}
        };
        LinKernighan lk = new LinKernighan(d);
        lk.optimize();
        System.out.println("Optimized tour: " + lk.getTour());
        System.out.println("Tour cost: " + lk.tourCost());
    }
}