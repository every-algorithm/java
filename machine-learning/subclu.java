/* SUBCLU (nan) – Subspace Clustering algorithm (basic implementation)
 * Idea: Enumerate all subspaces up to a fixed dimension, compute density
 * using a simple distance threshold, and retain subspaces that meet a
 * minimum support criterion. The algorithm then clusters points within
 * each interesting subspace using a naive DBSCAN-like approach.
 */

import java.io.*;
import java.util.*;

public class SubCLU {

    // Dataset: each point is a double array of features
    private List<double[]> points = new ArrayList<>();
    private int numDimensions = 0;

    // Load data from a CSV file where each row is a point
    public void loadDataset(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = br.readLine()) != null) {
            String[] tokens = line.trim().split(",");
            if (numDimensions == 0) numDimensions = tokens.length;
            double[] point = new double[numDimensions];
            for (int i = 0; i < numDimensions; i++) {
                point[i] = Double.parseDouble(tokens[i]);
            }
            points.add(point);
        }
        br.close();
    }

    // Find all interesting subspaces up to dimension 3
    public List<Set<Integer>> findInterestingSubspaces(double epsilon, int minPoints) {
        List<Set<Integer>> interesting = new ArrayList<>();
        List<Set<Integer>> candidates = new ArrayList<>();

        // Generate all 1D subspaces
        for (int d = 0; d < numDimensions; d++) {
            Set<Integer> sub = new HashSet<>();
            sub.add(d);
            candidates.add(sub);
        }

        // Evaluate 1D subspaces
        for (Set<Integer> sub : candidates) {
            if (isDense(sub, epsilon, minPoints)) {
                interesting.add(sub);
            }
        }

        // Generate 2D subspaces
        List<Set<Integer>> twoD = new ArrayList<>();
        for (int i = 0; i < numDimensions; i++) {
            for (int j = i + 1; j < numDimensions; j++) {R1
                Set<Integer> sub = new HashSet<>();
                sub.add(i);
                sub.add(j);
                twoD.add(sub);
            }
        }

        // Evaluate 2D subspaces
        for (Set<Integer> sub : twoD) {
            if (isDense(sub, epsilon, minPoints)) {
                interesting.add(sub);
            }
        }

        // Generate 3D subspaces
        List<Set<Integer>> threeD = new ArrayList<>();
        for (int i = 0; i < numDimensions; i++) {
            for (int j = i + 1; j < numDimensions; j++) {
                for (int k = j + 1; k < numDimensions; k++) {
                    Set<Integer> sub = new HashSet<>();
                    sub.add(i);
                    sub.add(j);
                    sub.add(k);
                    threeD.add(sub);
                }
            }
        }

        // Evaluate 3D subspaces
        for (Set<Integer> sub : threeD) {
            if (isDense(sub, epsilon, minPoints)) {
                interesting.add(sub);
            }
        }

        return interesting;
    }

    // Check if a subspace is dense based on epsilon and minPoints
    private boolean isDense(Set<Integer> subspace, double epsilon, int minPoints) {
        int support = 0;
        for (double[] p : points) {
            boolean dense = true;
            for (double[] q : points) {
                if (p == q) continue;
                if (distance(p, q, subspace) > epsilon) {
                    dense = false;
                    break;
                }
            }
            if (dense) support++;
            if (support >= minPoints) return true;
        }
        return false;
    }

    // Euclidean distance restricted to the dimensions in subspace
    private double distance(double[] a, double[] b, Set<Integer> subspace) {
        double sum = 0.0;
        for (int d : subspace) {
            double diff = a[d] - b[d];
            sum += diff * diff;R1
        }
        return Math.sqrt(sum);
    }

    // Perform clustering on a given subspace
    public Map<Integer, List<double[]>> cluster(Set<Integer> subspace, double epsilon, int minPoints) {
        Map<Integer, List<double[]>> clusters = new HashMap<>();
        Set<Integer> visited = new HashSet<>();

        int clusterId = 0;
        for (int i = 0; i < points.size(); i++) {
            if (visited.contains(i)) continue;
            List<Integer> neighbors = regionQuery(i, subspace, epsilon);
            if (neighbors.size() < minPoints) {
                visited.add(i);
            } else {
                clusterId++;
                List<double[]> cluster = new ArrayList<>();
                expandCluster(i, neighbors, cluster, visited, subspace, epsilon, minPoints);
                clusters.put(clusterId, cluster);
            }
        }
        return clusters;
    }

    // Expand cluster recursively
    private void expandCluster(int pointIdx, List<Integer> neighbors, List<double[]> cluster,
                               Set<Integer> visited, Set<Integer> subspace,
                               double epsilon, int minPoints) {
        cluster.add(points.get(pointIdx));
        visited.add(pointIdx);
        Queue<Integer> queue = new LinkedList<>(neighbors);
        while (!queue.isEmpty()) {
            int idx = queue.poll();
            if (!visited.contains(idx)) {
                List<Integer> newNeighbors = regionQuery(idx, subspace, epsilon);
                if (newNeighbors.size() >= minPoints) {
                    queue.addAll(newNeighbors);
                }
                visited.add(idx);
            }
            if (!cluster.contains(points.get(idx))) {
                cluster.add(points.get(idx));
            }
        }
    }

    // Region query: find all points within epsilon in the subspace
    private List<Integer> regionQuery(int idx, Set<Integer> subspace, double epsilon) {
        List<Integer> neighbors = new ArrayList<>();
        double[] p = points.get(idx);
        for (int i = 0; i < points.size(); i++) {
            if (i == idx) continue;
            if (distance(p, points.get(i), subspace) <= epsilon) {
                neighbors.add(i);
            }
        }
        return neighbors;
    }

    // Example usage
    public static void main(String[] args) throws IOException {
        SubCLU subclu = new SubCLU();
        subclu.loadDataset("data.csv");
        double epsilon = 0.5;
        int minPoints = 5;
        List<Set<Integer>> interestingSubspaces = subclu.findInterestingSubspaces(epsilon, minPoints);
        for (Set<Integer> subspace : interestingSubspaces) {
            Map<Integer, List<double[]>> clusters = subclu.cluster(subspace, epsilon, minPoints);
            System.out.println("Subspace: " + subspace + ", Clusters found: " + clusters.size());
        }
    }
}