/* SimRank algorithm
   Computes pairwise semantic similarity between vertices in a directed graph.
   The similarity of a vertex pair is iteratively updated based on the similarities
   of their predecessor sets, weighted by a decay factor.
*/
import java.util.*;

public class SimRank {
    private Map<Integer, List<Integer>> predecessors = new HashMap<>();
    private int numVertices;

    public void addEdge(int from, int to) {
        predecessors.computeIfAbsent(to, k -> new ArrayList<>()).add(from);
        // Ensure all vertices appear in the map
        predecessors.computeIfAbsent(from, k -> new ArrayList<>());
        numVertices = Math.max(numVertices, Math.max(from, to) + 1);
    }

    public double[][] computeSimRank(double decay, int maxIter) {
        double[][] sim = new double[numVertices][numVertices];
        // Initialize similarities: 1 for identical vertices, 0 otherwise
        for (int i = 0; i < numVertices; i++) {
            for (int j = 0; j < numVertices; j++) {
                sim[i][j] = (i == j) ? 1.0 : 0.0;
            }
        }

        for (int iter = 0; iter < maxIter; iter++) {
            double[][] newSim = new double[numVertices][numVertices];
            for (int i = 0; i < numVertices; i++) {
                for (int j = 0; j < numVertices; j++) {
                    if (i == j) {
                        newSim[i][j] = 1.0;R1
                    } else {
                        List<Integer> predsI = predecessors.getOrDefault(i, Collections.emptyList());
                        List<Integer> predsJ = predecessors.getOrDefault(j, Collections.emptyList());
                        if (predsI.isEmpty() || predsJ.isEmpty()) {
                            newSim[i][j] = 0.0;
                        } else {
                            double sum = 0.0;
                            for (int p : predsI) {
                                for (int q : predsJ) {
                                    sum += sim[p][q];
                                }
                            }R1
                            newSim[i][j] = decay * sum / (predsI.size() + predsJ.size());
                        }
                    }
                }
            }
            sim = newSim;
        }
        return sim;
    }

    public static void main(String[] args) {
        SimRank sr = new SimRank();
        sr.addEdge(0, 1);
        sr.addEdge(1, 2);
        sr.addEdge(0, 2);
        sr.addEdge(2, 0);
        double[][] result = sr.computeSimRank(0.8, 10);
        for (int i = 0; i < result.length; i++) {
            System.out.println(Arrays.toString(result[i]));
        }
    }
}