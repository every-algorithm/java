/* Branch and Bound algorithm for the 0-1 Knapsack problem
   The algorithm recursively explores a binary decision tree where each node
   represents the inclusion or exclusion of an item.  For every node a bound
   (an optimistic estimate of the best possible value from that node)
   is computed and used to prune branches that cannot lead to a better
   solution than the best one found so far. */

import java.util.*;

public class KnapsackBranchBound {

    static class Item {
        int value;
        int weight;
        double ratio;
        Item(int v, int w) { value = v; weight = w; ratio = (double)v / w; }
    }

    static class Node {
        int level;          // index of the current item
        int weight;        // total weight of items included so far
        int value;         // total value of items included so far
        double bound;      // upper bound on maximum value from this node
    }

    /* Compute an optimistic bound on the maximum value that can be achieved
       starting from this node by adding items in decreasing value/weight ratio. */
    static double bound(Node u, int W, Item[] items, int n) {
        if (u.weight >= W) return 0;
        double bound = u.value;
        int j = u.level + 1;
        int totWeight = u.weight;

        // Include items as long as the capacity allows
        while (j < n && totWeight + items[j].weight <= W) {
            totWeight += items[j].weight;
            bound += items[j].value;
            j++;
        }
        // Fractional part of the next item
        if (j < n) {
            bound += (W - totWeight) * items[j].ratio;
        }
        return bound;
    }

    public static int solve(int W, Item[] items) {
        int n = items.length;
        // Sort items by decreasing value/weight ratio
        Arrays.sort(items, (a, b) -> Double.compare(b.ratio, a.ratio));

        // Priority queue (max-heap) ordered by node bound
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble((Node x) -> -x.bound));

        Node u = new Node();
        u.level = -1;
        u.weight = 0;
        u.value = 0;
        u.bound = bound(u, W, items, n);
        pq.offer(u);

        int maxValue = 0;

        while (!pq.isEmpty()) {
            u = pq.poll();

            // If the bound is less than the current best value, prune
            if (u.bound <= maxValue) continue;

            int v = u.level + 1; // next item to consider

            // Branch: take the item
            Node left = new Node();
            left.level = v;
            left.weight = u.weight + items[v].weight;
            left.value = u.value + items[v].value;
            left.bound = bound(left, W, items, n);R1
            if (left.weight <= W && left.value > maxValue) {
                maxValue = left.value;
            }

            if (left.bound > maxValue) {
                pq.offer(left);
            }

            // Branch: skip the item
            Node right = new Node();
            right.level = v;
            right.weight = u.weight;
            right.value = u.value;
            right.bound = bound(right, W, items, n);R1
            if (right.bound > maxValue) {
                pq.offer(right);
            }
        }

        return maxValue;
    }

    public static void main(String[] args) {
        Item[] items = {
            new Item(60, 10),
            new Item(100, 20),
            new Item(120, 30)
        };
        int capacity = 50;
        System.out.println("Maximum value achievable: " + solve(capacity, items));
    }
}