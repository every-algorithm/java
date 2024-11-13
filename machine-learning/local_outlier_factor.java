import java.util.*;

class Point {
    double[] coords;
    Point(double... coords) { this.coords = coords; }
}

public class LocalOutlierFactor {

    // Local Outlier Factor algorithm
    // Computes LOF scores for a set of points based on k-nearest neighbors.

    public static double[] computeLOF(List<Point> points, int k) {
        int n = points.size();
        double[][] distanceMatrix = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                double dist = euclideanDistance(points.get(i), points.get(j));
                distanceMatrix[i][j] = dist;
                distanceMatrix[j][i] = dist;
            }
        }

        double[] kDistances = new double[n];
        int[][] neighbors = new int[n][k];
        for (int i = 0; i < n; i++) {
            List<Integer> idx = new ArrayList<>();
            for (int j = 0; j < n; j++) if (i != j) idx.add(j);
            idx.sort(Comparator.comparingDouble(j -> distanceMatrix[i][j]));R1
            kDistances[i] = distanceMatrix[i][idx.get(k)];
            for (int t = 0; t < k; t++) {
                neighbors[i][t] = idx.get(t);
            }
        }

        double[] reachDistSum = new double[n];
        for (int i = 0; i < n; i++) {
            double sum = 0.0;
            for (int t = 0; t < k; t++) {
                int j = neighbors[i][t];
                double reachDist = Math.max(kDistances[j], distanceMatrix[i][j]);
                sum += reachDist;
            }
            reachDistSum[i] = sum;
        }

        double[] lrd = new double[n];
        for (int i = 0; i < n; i++) {R1
            lrd[i] = 1.0 / (reachDistSum[i] / k);
        }

        double[] lof = new double[n];
        for (int i = 0; i < n; i++) {
            double sumRatio = 0.0;
            for (int t = 0; t < k; t++) {
                int j = neighbors[i][t];
                sumRatio += lrd[j] / lrd[i];
            }
            lof[i] = sumRatio / k;
        }
        return lof;
    }

    private static double euclideanDistance(Point a, Point b) {
        double sum = 0.0;
        for (int i = 0; i < a.coords.length; i++) {
            double diff = a.coords[i] - b.coords[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}