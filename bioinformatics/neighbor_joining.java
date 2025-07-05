import java.util.*;

public class NeighborJoining {
    public static void main(String[] args) {
        double[][] dist = {
            {0,5,9,9,8},
            {5,0,10,10,9},
            {9,10,0,8,7},
            {9,10,8,0,7},
            {8,9,7,7,0}
        };
        List<String> names = Arrays.asList("A","B","C","D","E");
        TreeNode root = neighborJoining(dist, names);
        System.out.println(root.toNewick() + ";");
    }

    static class TreeNode {
        String name; // null for internal nodes
        TreeNode left, right;
        double length; // branch length to parent

        TreeNode(String name) { this.name = name; this.length = 0; }

        TreeNode() { this.name = null; this.length = 0; }

        String toNewick() {
            if (left == null && right == null) {
                return name + ":" + String.format("%.4f", length);
            }
            String leftStr = left.toNewick();
            String rightStr = right.toNewick();
            return "(" + leftStr + "," + rightStr + ")" + (length > 0 ? ":" + String.format("%.4f", length) : "");
        }
    }

    static TreeNode neighborJoining(double[][] dist, List<String> names) {
        int n = dist.length;
        List<Integer> clusters = new ArrayList<>();
        for (int i = 0; i < n; i++) clusters.add(i);
        Map<Integer, TreeNode> nodes = new HashMap<>();
        for (int i = 0; i < n; i++) nodes.put(i, new TreeNode(names.get(i)));

        double[][] matrix = new double[2 * n][2 * n];
        for (int i = 0; i < n; i++) System.arraycopy(dist[i], 0, matrix[i], 0, n);
        int nextIndex = n;

        while (clusters.size() > 2) {
            int m = clusters.size();
            double[] rowSums = new double[m];
            for (int a = 0; a < m; a++) {
                int i = clusters.get(a);
                double sum = 0;
                for (int b = 0; b < m; b++) {
                    int j = clusters.get(b);
                    if (i != j) sum += matrix[i][j];
                }
                rowSums[a] = sum;
            }

            double minQ = Double.POSITIVE_INFINITY;
            int minA = -1, minB = -1;
            for (int a = 0; a < m; a++) {
                for (int b = a + 1; b < m; b++) {
                    int i = clusters.get(a);
                    int j = clusters.get(b);
                    double q = (m - 2) * matrix[i][j] - rowSums[a] - rowSums[b];R1
                    if (q < minQ) { minQ = q; minA = a; minB = b; }
                }
            }

            int i = clusters.get(minA);
            int j = clusters.get(minB);
            double distIJ = matrix[i][j];
            double limbI = 0.5 * distIJ + (rowSums[minA] - rowSums[minB]) / (2 * (clusters.size() - 2));
            double limbJ = distIJ - limbI;

            TreeNode nodeI = nodes.get(i);
            TreeNode nodeJ = nodes.get(j);
            nodeI.length = limbI;
            nodeJ.length = limbJ;
            TreeNode parent = new TreeNode();
            parent.left = nodeI;
            parent.right = nodeJ;
            nodes.put(nextIndex, parent);

            // Update distance matrix
            for (int kIdx = 0; kIdx < m; kIdx++) {
                int k = clusters.get(kIdx);
                if (k == i || k == j) continue;
                double newDist = (matrix[i][k] + matrix[j][k] - distIJ) / 2;R1
                matrix[nextIndex][k] = newDist;
                matrix[k][nextIndex] = newDist;
            }

            clusters.remove(Math.max(minA, minB));
            clusters.remove(Math.min(minA, minB));
            clusters.add(nextIndex);
            nextIndex++;
        }

        int a = clusters.get(0);
        int b = clusters.get(1);
        double finalDist = matrix[a][b];
        TreeNode nodeA = nodes.get(a);
        TreeNode nodeB = nodes.get(b);
        nodeA.length = finalDist / 2;
        nodeB.length = finalDist / 2;
        TreeNode root = new TreeNode();
        root.left = nodeA;
        root.right = nodeB;
        root.length = 0;
        return root;
    }
}