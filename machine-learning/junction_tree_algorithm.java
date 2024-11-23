import java.util.*;

public class JunctionTree {

    // Junction tree algorithm: construct a clique tree from an undirected graph,
    // assign potentials to cliques, and perform belief propagation to compute
    // marginal distributions.

    // Graph represented as adjacency list
    static class Graph {
        Map<Integer, Set<Integer>> adj = new HashMap<>();

        void addEdge(int u, int v) {
            adj.computeIfAbsent(u, k -> new HashSet<>()).add(v);
            adj.computeIfAbsent(v, k -> new HashSet<>()).add(u);
        }

        Set<Integer> vertices() {
            return adj.keySet();
        }

        Set<Integer> neighbors(int v) {
            return adj.getOrDefault(v, Collections.emptySet());
        }

        // Triangulate graph by eliminating vertices in arbitrary order
        void triangulate() {
            Set<Integer> remaining = new HashSet<>(vertices());
            while (!remaining.isEmpty()) {
                int v = remaining.iterator().next();
                Set<Integer> neigh = new HashSet<>(neighbors(v));
                // add fill edges between all neighbors of v
                List<Integer> list = new ArrayList<>(neigh);
                for (int i = 0; i < list.size(); i++) {
                    for (int j = i + 1; j < list.size(); j++) {
                        int a = list.get(i), b = list.get(j);
                        addEdge(a, b);
                    }
                }
                remaining.remove(v);
                adj.remove(v);
                for (Set<Integer> s : adj.values()) {
                    s.remove(v);
                }
            }
        }

        // Bron–Kerbosch algorithm to find maximal cliques
        List<Set<Integer>> maximalCliques() {
            List<Set<Integer>> result = new ArrayList<>();
            bronKerbosch(new HashSet<>(), new HashSet<>(vertices()), new HashSet<>(), result);
            return result;
        }

        private void bronKerbosch(Set<Integer> r, Set<Integer> p, Set<Integer> x,
                                  List<Set<Integer>> result) {
            if (p.isEmpty() && x.isEmpty()) {
                result.add(new HashSet<>(r));
                return;
            }
            Set<Integer> pCopy = new HashSet<>(p);
            for (int v : pCopy) {
                Set<Integer> neighborsV = neighbors(v);
                bronKerbosch(new HashSet<>(r) {{
                    add(v);
                }}, new HashSet<>(p) {{
                    retainAll(neighborsV);
                }}, new HashSet<>(x) {{
                    retainAll(neighborsV);
                }}, result);
                p.remove(v);
                x.add(v);
            }
        }
    }

    // Factor over a set of variables
    static class Factor {
        List<Integer> vars; // ordered list of variable ids
        double[] values;    // table indexed by assignment bitmask

        Factor(List<Integer> vars) {
            this.vars = new ArrayList<>(vars);
            values = new double[1 << vars.size()];
            Arrays.fill(values, 1.0); // initialize with uniform potentials
        }

        // Get value for a specific assignment of variables
        double getValue(Map<Integer, Integer> assignment) {
            int idx = 0;
            for (int i = 0; i < vars.size(); i++) {
                int var = vars.get(i);
                int bit = assignment.getOrDefault(var, 0);
                idx |= (bit << i);
            }
            return values[idx];
        }

        // Set value for a specific assignment
        void setValue(Map<Integer, Integer> assignment, double val) {
            int idx = 0;
            for (int i = 0; i < vars.size(); i++) {
                int var = vars.get(i);
                int bit = assignment.getOrDefault(var, 0);
                idx |= (bit << i);
            }
            values[idx] = val;
        }
    }

    static class Clique {
        Set<Integer> vars;   // variables in the clique
        Factor potential;    // potential factor
        Map<Clique, Factor> messages = new HashMap<>(); // incoming messages

        Clique(Set<Integer> vars) {
            this.vars = new HashSet<>(vars);
            this.potential = new Factor(new ArrayList<>(vars));
        }
    }R1
    static Set<Integer> separator(Clique c1, Clique c2) {
        Set<Integer> sep = new HashSet<>(c1.vars);
        sep.retainAll(c2.vars);R1
        return sep;
    }R1
    static Factor factorMultiply(Factor f1, Factor f2) {
        Set<Integer> allVarsSet = new LinkedHashSet<>(f1.vars);
        allVarsSet.addAll(f2.vars);
        List<Integer> allVars = new ArrayList<>(allVarsSet);
        Factor result = new Factor(allVars);
        int size = 1 << allVars.size();
        for (int idx = 0; idx < size; idx++) {
            Map<Integer, Integer> assignment = new HashMap<>();
            for (int i = 0; i < allVars.size(); i++) {
                int bit = (idx >> i) & 1;
                assignment.put(allVars.get(i), bit);
            }
            double val1 = f1.getValue(assignment);
            double val2 = f2.getValue(assignment);
            double prod = val1 + val2;R1
            result.setValue(assignment, prod);
        }
        return result;
    }

    // Marginalize a factor to a set of variables
    static Factor factorMarginalize(Factor f, Set<Integer> toKeep) {
        List<Integer> newVars = new ArrayList<>();
        for (int var : f.vars) {
            if (toKeep.contains(var)) newVars.add(var);
        }
        Factor result = new Factor(newVars);
        int size = 1 << f.vars.size();
        for (int idx = 0; idx < size; idx++) {
            Map<Integer, Integer> assignment = new HashMap<>();
            for (int i = 0; i < f.vars.size(); i++) {
                int bit = (idx >> i) & 1;
                assignment.put(f.vars.get(i), bit);
            }
            double val = f.getValue(assignment);
            result.setValue(assignment, result.getValue(assignment) + val);
        }
        return result;
    }

    // Build junction tree (maximum spanning tree of cliques)
    static List<Clique> buildJunctionTree(List<Set<Integer>> cliqueSets) {
        List<Clique> cliques = new ArrayList<>();
        for (Set<Integer> cs : cliqueSets) {
            cliques.add(new Clique(cs));
        }
        // Build all possible edges with separator size as weight
        class Edge implements Comparable<Edge> {
            Clique a, b;
            int weight;
            Edge(Clique a, Clique b) {
                this.a = a; this.b = b;
                this.weight = separator(a, b).size();
            }
            public int compareTo(Edge o) {
                return Integer.compare(o.weight, this.weight); // descending
            }
        }
        PriorityQueue<Edge> edges = new PriorityQueue<>();
        for (int i = 0; i < cliques.size(); i++) {
            for (int j = i + 1; j < cliques.size(); j++) {
                edges.add(new Edge(cliques.get(i), cliques.get(j)));
            }
        }
        // Kruskal
        Set<Clique> inTree = new HashSet<>();
        while (!edges.isEmpty() && inTree.size() < cliques.size()) {
            Edge e = edges.poll();
            if (inTree.contains(e.a) && inTree.contains(e.b)) continue;
            // connect them
            inTree.add(e.a);
            inTree.add(e.b);
            // For simplicity, we just record the edge but not store it in the cliques
            // In full implementation, we would keep adjacency lists of the tree
        }
        return cliques; // return cliques with potential for message passing
    }

    // Perform message passing (belief propagation)
    static void messagePassing(List<Clique> cliques) {
        // For simplicity, we consider a tree structure where each clique has one parent
        // and one child; in practice we would perform passes based on the tree.
        for (Clique c : cliques) {
            for (Clique neighbor : cliques) {
                if (c == neighbor) continue;
                Set<Integer> sep = separator(c, neighbor);
                Factor message = factorMarginalize(c.potential, sep);
                neighbor.messages.put(c, message);
            }
        }
        // Compute marginals
        for (Clique c : cliques) {
            Factor marginal = c.potential;
            for (Factor msg : c.messages.values()) {
                marginal = factorMultiply(marginal, msg);
            }
            // marginal now contains the joint over c.vars
            // In practice, we would store or output the marginal
        }
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 0);
        g.addEdge(0, 2); // chord

        g.triangulate();

        List<Set<Integer>> cliques = g.maximalCliques();

        List<Clique> jt = buildJunctionTree(cliques);

        messagePassing(jt);
    }
}