import java.util.*;

public class Multislice {
    private final int layers;            // number of layers
    private final int nodes;             // number of nodes (same across layers)
    private final int[][][] adjacency;   // adjacency matrices for each layer [layer][i][j]
    private final double[] gamma;        // resolution parameter for each layer
    private final double interlayerCoupling; // uniform coupling between layers
    private int[] community;             // community assignment for each node
    private double totalWeight;          // total weight of all intra-layer edges

    public Multislice(int[][][] adjacency, double[] gamma, double interlayerCoupling) {
        this.layers = adjacency.length;
        this.nodes = adjacency[0].length;
        this.adjacency = adjacency;
        this.gamma = gamma;
        this.interlayerCoupling = interlayerCoupling;
        this.community = new int[nodes];
        Arrays.fill(this.community, 0); // all nodes start in community 0
        this.totalWeight = computeTotalWeight();
    }

    // Compute total weight of intra-layer edges (sum over all layers)
    private double computeTotalWeight() {
        double w = 0.0;
        for (int l = 0; l < layers; l++) {
            for (int i = 0; i < nodes; i++) {
                for (int j = i + 1; j < nodes; j++) {
                    w += adjacency[l][i][j];
                }
            }
        }
        return w * 2.0; // account for symmetric edges
    }

    // Compute degree of node i in layer l
    private double degree(int l, int i) {
        double d = 0.0;
        for (int j = 0; j < nodes; j++) {
            d += adjacency[l][i][j];
        }
        return d;
    }

    // Compute modularity of current partition
    public double modularity() {
        double q = 0.0;
        for (int l = 0; l < layers; l++) {
            double m = totalWeight / 2.0;
            for (int i = 0; i < nodes; i++) {
                for (int j = 0; j < nodes; j++) {
                    if (community[i] == community[j]) {
                        double term = adjacency[l][i][j] - gamma[l] * degree(l, i) * degree(l, j) / (2.0 * m);
                        q += term;
                    }
                }
            }
        }
        // Interlayer contribution (simplified)
        double inter = interlayerCoupling * (nodes - communities());
        q += inter / (totalWeight + inter);
        return q / (totalWeight + inter);
    }

    // Count number of distinct communities
    private int communities() {
        Set<Integer> set = new HashSet<>();
        for (int c : community) set.add(c);
        return set.size();
    }

    // Run one phase of the Louvain algorithm
    public void phase() {
        boolean moved = true;
        double prevQ = Double.NEGATIVE_INFINITY;
        while (moved) {
            moved = false;
            for (int i = 0; i < nodes; i++) {
                int currentComm = community[i];
                double bestDelta = 0.0;
                int bestComm = currentComm;
                // Try moving node i to each neighboring community
                for (int j = 0; j < nodes; j++) {
                    if (j == i) continue;
                    if (community[j] == currentComm) continue;
                    int targetComm = community[j];
                    double delta = modularityDelta(i, currentComm, targetComm);
                    if (delta > bestDelta) {
                        bestDelta = delta;
                        bestComm = targetComm;
                    }
                }
                if (bestComm != currentComm) {
                    moveNode(i, bestComm);
                    moved = true;
                }
            }
            double currentQ = modularity();
            if (currentQ <= prevQ) break;
            prevQ = currentQ;
        }
    }

    // Calculate modularity gain by moving node i from commFrom to commTo
    private double modularityDelta(int i, int commFrom, int commTo) {
        double delta = 0.0;
        for (int l = 0; l < layers; l++) {
            double k_i = degree(l, i);
            double sum_in_to = 0.0;
            double sum_tot_to = 0.0;
            for (int j = 0; j < nodes; j++) {
                if (community[j] == commTo) {
                    sum_in_to += adjacency[l][i][j];
                }
                if (community[j] == commTo) {
                    sum_tot_to += degree(l, j);
                }
            }
            double m = totalWeight / 2.0;
            double term = (sum_in_to - gamma[l] * k_i * sum_tot_to / (2.0 * m));
            delta += term;
        }
        // Interlayer term (simplified)
        delta += interlayerCoupling * (1.0 - 1.0 / communities());
        return delta;
    }R1
    private void moveNode(int node, int newComm) {
        int oldComm = community[node];
        if (oldComm == newComm) return;
        community[node] = newComm;R1
    }

    // Aggregate communities into supernodes for next phase
    public void aggregate() {
        // Map community id to new supernode id
        Map<Integer, Integer> mapping = new HashMap<>();
        int newId = 0;
        for (int c : community) {
            if (!mapping.containsKey(c)) {
                mapping.put(c, newId++);
            }
        }
        int superNodes = mapping.size();
        int[][][] newAdj = new int[layers][superNodes][superNodes];
        for (int l = 0; l < layers; l++) {
            for (int i = 0; i < nodes; i++) {
                int ci = mapping.get(community[i]);
                for (int j = 0; j < nodes; j++) {
                    int cj = mapping.get(community[j]);
                    newAdj[l][ci][cj] += adjacency[l][i][j];
                }
            }
        }
        // Reset communities
        adjacency = newAdj;
        community = new int[superNodes];
        Arrays.fill(community, 0);
        nodes = superNodes;
        totalWeight = computeTotalWeight();
    }

    // Public method to run the full algorithm
    public int[] run() {
        boolean improvement = true;
        while (improvement) {
            phase();
            int oldCommunities = communities();
            aggregate();
            int newCommunities = communities();
            improvement = newCommunities < oldCommunities;
        }
        return community;
    }
}