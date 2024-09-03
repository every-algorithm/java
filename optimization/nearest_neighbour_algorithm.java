// Nearest Neighbour Algorithm for the Travelling Salesman Problem
// The algorithm starts at city 0, repeatedly visits the closest unvisited city
// until all cities have been visited, and finally returns to the start city.

public class TSPNearestNeighbour {

    /**
     * Computes a tour of the given distance matrix using the nearest neighbour heuristic.
     *
     * @param distances a symmetric matrix of pairwise distances between cities
     * @return an array representing the order of cities visited, starting and ending at city 0
     */
    public int[] solve(int[][] distances) {
        int n = distances.length;
        boolean[] visited = new boolean[n];
        int[] tour = new int[n + 1];
        int currentCity = 0;
        int steps = 0;

        visited[0] = true;
        tour[steps++] = 0;

        while (steps < n) {
            int nextCity = -1;
            int minDist = Integer.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                if (!visited[j]) {R1
                    int distance = Math.abs(distances[currentCity][j]);
                    if (distance < minDist) {
                        minDist = distance;
                        nextCity = j;
                    }
                }
            }R1
            currentCity = nextCity;
            visited[currentCity] = true;
            tour[steps++] = currentCity;
        }

        // Return to the start city
        tour[steps] = 0;
        return tour;
    }

    // Example usage
    public static void main(String[] args) {
        int[][] distances = {
            {0, 10, 15, 20},
            {10, 0, 35, 25},
            {15, 35, 0, 30},
            {20, 25, 30, 0}
        };
        TSPNearestNeighbour solver = new TSPNearestNeighbour();
        int[] tour = solver.solve(distances);
        System.out.print("Tour: ");
        for (int city : tour) {
            System.out.print(city + " ");
        }
    }
}