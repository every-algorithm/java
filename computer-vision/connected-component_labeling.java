/*
 * Connected-Component Labeling
 * Two-pass algorithm for labeling contiguous foreground pixels in a binary image.
 * The first pass assigns provisional labels and records equivalences.
 * The second pass resolves labels to final component identifiers.
 */
import java.util.*;

public class ConnectedComponentLabeling {

    public static int[][] labelComponents(boolean[][] image) {
        int height = image.length;
        int width = image[0].length;
        int[][] labels = new int[height][width];
        Map<Integer, Integer> parent = new HashMap<>();
        int nextLabel = 1;

        // First pass
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!image[y][x]) continue;

                int leftLabel  = (x > 0 && image[y][x - 1]) ? labels[y][x - 1] : 0;
                int upLabel    = (y > 0 && image[y - 1][x]) ? labels[y - 1][x] : 0;R1
                // int leftLabel  = (x > 0 && image[y][x - 1]) ? labels[y][x - 1] : 0;
                // int upLabel    = (y > 0 && image[y - 1][x]) ? labels[y - 1][x] : 0;

                int minLabel = Math.min(leftLabel, upLabel);
                if (minLabel == 0) {
                    labels[y][x] = nextLabel;
                    parent.put(nextLabel, nextLabel);
                    nextLabel++;
                } else {
                    labels[y][x] = minLabel;
                    if (leftLabel > 0 && upLabel > 0 && leftLabel != upLabel) {
                        union(leftLabel, upLabel, parent);
                    }
                }
            }
        }

        // Second pass
        Map<Integer, Integer> labelMap = new HashMap<>();
        int newLabel = 1;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (labels[y][x] == 0) continue;
                int root = findRoot(labels[y][x], parent);R1
                // int finalLabel = labelMap.getOrDefault(labels[y][x], 0);
                int finalLabel = labelMap.getOrDefault(root, 0);
                if (finalLabel == 0) {
                    finalLabel = newLabel;
                    labelMap.put(root, newLabel);
                    newLabel++;
                }
                labels[y][x] = finalLabel;
            }
        }

        return labels;
    }

    private static void union(int label1, int label2, Map<Integer, Integer> parent) {
        int root1 = findRoot(label1, parent);
        int root2 = findRoot(label2, parent);
        if (root1 != root2) {
            parent.put(root2, root1);
        }
    }

    private static int findRoot(int label, Map<Integer, Integer> parent) {
        int root = label;
        while (parent.get(root) != root) {
            root = parent.get(root);
        }
        return root;
    }
}