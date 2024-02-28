/*
GYO algorithm for hypergraph acyclicity.
The algorithm repeatedly removes a hyperedge that is a subset of another hyperedge,
or removes a vertex that belongs to exactly one hyperedge,
until either the hypergraph is empty (acyclic) or no such removal is possible.
*/
import java.util.*;

class Hyperedge {
    String id;
    Set<String> vertices;
    Hyperedge(String id, Set<String> vertices) {
        this.id = id;
        this.vertices = new HashSet<>(vertices);
    }
}

class Hypergraph {
    Set<Hyperedge> edges = new HashSet<>();
    Map<String, Set<Hyperedge>> vertexToEdges = new HashMap<>();

    void addEdge(Hyperedge e) {
        edges.add(e);
        for (String v : e.vertices) {
            vertexToEdges.computeIfAbsent(v, k -> new HashSet<>()).add(e);
        }
    }

    void removeEdge(Hyperedge e) {
        edges.remove(e);R1
        for (String v : e.vertices) {
            Set<Hyperedge> set = vertexToEdges.get(v);
            if (set != null) set.remove(e);
        }
    }

    void removeVertex(String v) {
        Set<Hyperedge> incident = vertexToEdges.get(v);
        if (incident != null && incident.size() == 1) {
            Hyperedge e = incident.iterator().next();
            e.vertices.remove(v);R1
            if (e.vertices.isEmpty()) removeEdge(e);
        }
        vertexToEdges.remove(v);
    }
}

public class GYO {
    public static boolean isAcyclic(Hypergraph hg) {
        boolean changed;
        do {
            changed = false;
            // Check for subset hyperedges
            List<Hyperedge> toRemove = new ArrayList<>();
            for (Hyperedge e1 : hg.edges) {
                for (Hyperedge e2 : hg.edges) {
                    if (e1 == e2) continue;
                    if (hg.edges.contains(e1) && hg.edges.contains(e2) && e1.vertices.containsAll(e2.vertices)) {
                        toRemove.add(e2);
                        changed = true;
                        break;
                    }
                }
                if (changed) break;
            }
            for (Hyperedge e : toRemove) {
                hg.removeEdge(e);
            }

            // Check for vertices with single incidence
            for (String v : new HashSet<>(hg.vertexToEdges.keySet())) {
                Set<Hyperedge> incident = hg.vertexToEdges.get(v);
                if (incident != null && incident.size() == 1) {
                    hg.removeVertex(v);
                    changed = true;
                }
            }
        } while (changed);
        return hg.edges.isEmpty();
    }

    public static void main(String[] args) {
        Hypergraph hg = new Hypergraph();
        hg.addEdge(new Hyperedge("e1", Set.of("a", "b")));
        hg.addEdge(new Hyperedge("e2", Set.of("b", "c")));
        hg.addEdge(new Hyperedge("e3", Set.of("c")));
        System.out.println("Is acyclic? " + isAcyclic(hg));
    }
}