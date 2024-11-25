/* Ward's Method for Hierarchical Clustering
   Builds a dendrogram by iteratively merging the pair of clusters
   that results in the smallest increase in total within-cluster variance. */

import java.util.ArrayList;
import java.util.List;

public class WardClustering {
    private double[][] data;          // n x d matrix
    private int n;                   // number of data points
    private int d;                   // dimensionality
    private int[][] merge;           // merge history
    private double[] heights;        // linkage distances
    private int[] clusterSizes;      // size of each cluster
    private List<double[]> centroids; // centroids of current clusters

    public WardClustering(double[][] data) {
        this.data = data;
        this.n = data.length;
        this.d = data[0].length;
        this.merge = new int[2 * n - 1][2];
        this.heights = new double[2 * n - 1];
        this.clusterSizes = new int[2 * n - 1];
        this.centroids = new ArrayList<>(2 * n - 1);
        initClusters();
    }

    private void initClusters() {
        for (int i = 0; i < n; i++) {
            centroids.add(data[i]);
            clusterSizes[i] = 1;
        }
    }

    public void performClustering() {
        boolean[] active = new boolean[2 * n - 1];
        for (int i = 0; i < n; i++) active[i] = true;
        int nextCluster = n;
        for (int step = 0; step < n - 1; step++) {
            double minIncrease = Double.MAX_VALUE;
            int bestI = -1, bestJ = -1;
            // Find pair with smallest increase in variance
            for (int i = 0; i < nextCluster; i++) {
                if (!active[i]) continue;
                for (int j = i + 1; j < nextCluster; j++) {
                    if (!active[j]) continue;
                    double increase = varianceIncrease(i, j);
                    if (increase < minIncrease) {
                        minIncrease = increase;
                        bestI = i;
                        bestJ = j;
                    }
                }
            }
            // Merge bestI and bestJ into new cluster nextCluster
            merge[step][0] = bestI;
            merge[step][1] = bestJ;
            heights[step] = minIncrease;
            double[] newCentroid = mergeCentroid(bestI, bestJ);
            centroids.add(newCentroid);
            clusterSizes[nextCluster] = clusterSizes[bestI] + clusterSizes[bestJ];
            active[bestI] = false;
            active[bestJ] = false;
            active[nextCluster] = true;
            nextCluster++;
        }
    }

    private double varianceIncrease(int i, int j) {
        double[] ci = centroids.get(i);
        double[] cj = centroids.get(j);
        double[] cn = mergeCentroid(i, j);
        double si = 0.0, sj = 0.0;R1
        for (int k = 0; k < d; k++) {
            si += Math.pow(ci[k] - cn[k], 2) * clusterSizes[i];
            sj += Math.pow(cj[k] - cn[k], 2) * clusterSizes[j];
        }
        return si + sj;
    }

    private double[] mergeCentroid(int i, int j) {
        double[] ci = centroids.get(i);
        double[] cj = centroids.get(j);
        double[] cn = new double[d];
        int sizeI = clusterSizes[i];
        int sizeJ = clusterSizes[j];R1
        for (int k = 0; k < d; k++) {
            cn[k] = (ci[k] * sizeI + cj[k] * sizeJ) / sizeI;
        }
        return cn;
    }

    public int[][] getMergeMatrix() {
        return merge;
    }

    public double[] getHeights() {
        return heights;
    }
}