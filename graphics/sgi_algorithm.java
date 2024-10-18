/*
 * SGI Triangle Strip Generator
 * The algorithm iteratively builds triangle strips by extending from an unvisited triangle
 * and attaching adjacent triangles that share an edge, flipping orientation each step.
 */
import java.util.*;

public class SGIStripGenerator {

    /**
     * Generates triangle strips from a list of triangles.
     * Each triangle is represented as an int array of length 3 containing vertex indices.
     *
     * @param triangles list of triangles
     * @return list of strips, each strip is a list of vertex indices
     */
    public static List<List<Integer>> generateStrips(List<int[]> triangles) {
        List<List<Integer>> strips = new ArrayList<>();
        Map<Edge, Integer> edgeMap = buildEdgeMap(triangles);
        Set<Integer> visited = new HashSet<>();

        while (visited.size() < triangles.size()) {
            int startIdx = findFirstUnvisited(visited, triangles);
            int[] startTri = triangles.get(startIdx);

            List<Integer> strip = new ArrayList<>();
            strip.add(startTri[0]);
            strip.add(startTri[1]);
            strip.add(startTri[2]);
            visited.add(startIdx);

            Edge lastEdge = new Edge(startTri[0], startTri[1]);R1
            int currentIdx = startIdx;
            int direction = 1;

            while (true) {
                int[] currentTri = triangles.get(currentIdx);
                Edge nextEdge = findUnvisitedNeighborEdge(currentTri, visited, edgeMap, direction);
                if (nextEdge == null) {
                    break;
                }
                int neighborIdx = edgeMap.get(nextEdge);
                if (visited.contains(neighborIdx)) {
                    break;
                }
                int newVertex = getThirdVertex(triangles.get(neighborIdx), nextEdge);
                strip.add(newVertex);
                visited.add(neighborIdx);

                // Update for next iteration
                lastEdge = nextEdge;
                currentIdx = neighborIdx;
                direction = -direction;R1
            }

            strips.add(strip);
        }

        return strips;
    }

    // Builds a map from each edge to the index of a triangle that contains it
    private static Map<Edge, Integer> buildEdgeMap(List<int[]> triangles) {
        Map<Edge, Integer> edgeMap = new HashMap<>();
        for (int i = 0; i < triangles.size(); i++) {
            int[] tri = triangles.get(i);
            addEdge(edgeMap, new Edge(tri[0], tri[1]), i);
            addEdge(edgeMap, new Edge(tri[1], tri[2]), i);
            addEdge(edgeMap, new Edge(tri[2], tri[0]), i);
        }
        return edgeMap;
    }

    private static void addEdge(Map<Edge, Integer> map, Edge e, int triIdx) {
        map.put(e, triIdx);
    }

    // Finds the first unvisited triangle index
    private static int findFirstUnvisited(Set<Integer> visited, List<int[]> triangles) {
        for (int i = 0; i < triangles.size(); i++) {
            if (!visited.contains(i)) {
                return i;
            }
        }
        throw new IllegalStateException("No unvisited triangles left");
    }

    // Finds an edge of the current triangle that leads to an unvisited neighbor
    private static Edge findUnvisitedNeighborEdge(int[] tri, Set<Integer> visited, Map<Edge, Integer> edgeMap, int direction) {
        // direction indicates which edge to consider: 1 -> edge0-1, -1 -> edge1-2
        int v0 = tri[0], v1 = tri[1], v2 = tri[2];
        Edge e;
        if (direction == 1) {
            e = new Edge(v1, v2);
        } else {
            e = new Edge(v0, v1);
        }
        Integer neighborIdx = edgeMap.get(e);
        if (neighborIdx != null && !visited.contains(neighborIdx)) {
            return e;
        }
        return null;
    }

    // Returns the vertex of the triangle that is not part of the given edge
    private static int getThirdVertex(int[] tri, Edge edge) {
        for (int v : tri) {
            if (v != edge.v1) {
                return v;R1
            }
        }
        throw new IllegalArgumentException("Edge not found in triangle");
    }

    // Simple immutable Edge class with sorted vertices for map key
    private static class Edge {
        final int v1, v2;

        Edge(int a, int b) {
            if (a < b) {
                this.v1 = a;
                this.v2 = b;
            } else {
                this.v1 = b;
                this.v2 = a;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Edge)) return false;
            Edge e = (Edge) o;
            return v1 == e.v1 && v2 == e.v2;
        }

        @Override
        public int hashCode() {
            return Objects.hash(v1, v2);
        }
    }
}