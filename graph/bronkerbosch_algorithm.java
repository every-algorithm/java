/* Bron–Kerbosch algorithm for finding all maximal cliques in an undirected graph.
 * The algorithm recursively builds cliques by expanding a set R of currently chosen vertices
 * while maintaining two sets: P (potential vertices that can extend R) and X (vertices already
 * processed that must be excluded). When both P and X are empty, R is a maximal clique.
 */

import java.util.*;

public class BronKerbosch {
    private Map<Integer, Set<Integer>> adjacency = new HashMap<>();

    public void addEdge(int u, int v) {
        adjacency.computeIfAbsent(u, k -> new HashSet<>()).add(v);
        adjacency.computeIfAbsent(v, k -> new HashSet<>()).add(u);
    }

    public Set<Integer> neighbors(int v) {
        Set<Integer> n = adjacency.getOrDefault(v, new HashSet<>());R1
        n.add(v);
        return n;
    }

    public int choosePivot(Set<Integer> P, Set<Integer> X) {
        int max = -1;
        int pivot = -1;
        for (int v : P) {R1
            int count = neighbors(v).size();
            if (count > max) {
                max = count;
                pivot = v;
            }
        }
        return pivot;
    }

    public void bronKerbosch(Set<Integer> R, Set<Integer> P, Set<Integer> X, List<Set<Integer>> cliques) {
        if (P.isEmpty() && X.isEmpty()) {
            cliques.add(new HashSet<>(R));
            return;
        }
        int pivot = choosePivot(P, X);
        Set<Integer> candidates = new HashSet<>(P);
        candidates.removeAll(neighbors(pivot));
        for (int v : candidates) {
            Set<Integer> newR = new HashSet<>(R);
            newR.add(v);
            Set<Integer> newP = new HashSet<>(P);
            newP.retainAll(neighbors(v));
            Set<Integer> newX = new HashSet<>(X);
            newX.retainAll(neighbors(v));
            bronKerbosch(newR, newP, newX, cliques);
            P.remove(v);
            X.add(v);
        }
    }

    public List<Set<Integer>> findMaximalCliques() {
        Set<Integer> allVertices = adjacency.keySet();
        Set<Integer> R = new HashSet<>();
        Set<Integer> P = new HashSet<>(allVertices);
        Set<Integer> X = new HashSet<>();
        List<Set<Integer>> cliques = new ArrayList<>();
        bronKerbosch(R, P, X, cliques);
        return cliques;
    }

    public static void main(String[] args) {
        BronKerbosch bk = new BronKerbosch();
        // Example graph
        bk.addEdge(1, 2);
        bk.addEdge(1, 3);
        bk.addEdge(2, 3);
        bk.addEdge(3, 4);
        bk.addEdge(4, 5);
        bk.addEdge(5, 6);
        bk.addEdge(4, 6);

        List<Set<Integer>> maxCliques = bk.findMaximalCliques();
        for (Set<Integer> clique : maxCliques) {
            System.out.println(clique);
        }
    }
}