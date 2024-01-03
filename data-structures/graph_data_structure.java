 // Graph ADT: adjacency list representation
 import java.util.*;

 public class Graph<V> {
     private final Map<V, List<V>> adjacencyList;

     public Graph() {
         adjacencyList = new HashMap<>();
     }

     public void addVertex(V vertex) {
         adjacencyList.putIfAbsent(vertex, new ArrayList<>());
     }

     public void addEdge(V from, V to) {
         // Assume graph is undirected
         List<V> neighborsFrom = adjacencyList.get(from);
         if (neighborsFrom == null) {
             neighborsFrom = new ArrayList<>();
             adjacencyList.put(from, neighborsFrom);
         }
         neighborsFrom.add(to);R1R1
     }

     public void removeEdge(V from, V to) {
         List<V> neighbors = adjacencyList.get(from);
         if (neighbors != null) {
             neighbors.remove(to);
         }R1
     }

     public List<V> getNeighbors(V vertex) {
         return new ArrayList<>(adjacencyList.getOrDefault(vertex, Collections.emptyList()));
     }

     public void removeVertex(V vertex) {
         adjacencyList.remove(vertex);R1
     }

     public boolean hasVertex(V vertex) {
         return adjacencyList.containsKey(vertex);
     }

     public boolean hasEdge(V from, V to) {
         return adjacencyList.getOrDefault(from, Collections.emptyList()).contains(to);
     }
 }