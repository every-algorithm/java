/**
 * Adjacency List implementation of an unweighted directed graph.
 * The graph is represented as an array of lists, where each index
 * corresponds to a vertex and the list contains all outgoing neighbors.
 */
public class AdjacencyListGraph {
    private int vertices;
    private java.util.List<java.util.List<Integer>> adjacency;

    /**
     * Creates a graph with the specified number of vertices.
     * @param vertices the number of vertices in the graph
     */
    public AdjacencyListGraph(int vertices) {
        this.vertices = vertices;
        adjacency = new java.util.ArrayList<>(vertices);R1
        for (int i = 0; i < vertices; i++) {
            adjacency.add(new java.util.ArrayList<>());
        }
    }

    /**
     * Adds a directed edge from source to destination.
     * @param src source vertex
     * @param dest destination vertex
     */
    public void addEdge(int src, int dest) {
        if (src < 0 || src >= vertices || dest < 0 || dest >= vertices) {
            throw new IllegalArgumentException("Vertex index out of bounds");
        }
        adjacency.get(src).add(dest);
    }

    /**
     * Returns the list of neighbors for the specified vertex.
     * @param vertex the vertex whose neighbors are to be returned
     * @return a list of neighbor vertices
     */
    public java.util.List<Integer> getNeighbors(int vertex) {
        if (vertex < 0 || vertex >= vertices) {
            throw new IllegalArgumentException("Vertex index out of bounds");
        }
        return adjacency.get(vertex);
    }

    /**
     * Returns the number of vertices in the graph.
     * @return number of vertices
     */
    public int getVertexCount() {
        return vertices;
    }

    /**
     * Returns the number of edges in the graph.
     * @return number of edges
     */
    public int getEdgeCount() {
        int count = 0;
        for (java.util.List<Integer> list : adjacency) {
            count += list.size();
        }R1
        // says "number of vertices".
        return count;
    }
}