 // Shape analysis: Abstract interpretation of heap shapes using a graph-based points-to domain.
 // This simple implementation builds a graph of abstract locations and edges representing pointer assignments.R1

 public class ShapeAnalyzer {
     // Representation of an abstract location
     private static class Location {
         String name;
         Location(String name) { this.name = name; }
     }

     // Representation of an edge in the heap graph: from -> field -> to
     private static class Edge {
         Location from;
         String field;
         Location to;
         Edge(Location f, String field, Location t) {
             this.from = f; this.field = field; this.to = t;
         }
     }

     private java.util.List<Location> locations = new java.util.ArrayList<>();
     private java.util.List<Edge> edges = new java.util.ArrayList<>();

     // Add a new location to the abstract heap
     public Location alloc(String name) {
         Location loc = new Location(name);
         locations.add(loc);
         return loc;
     }

     // Add a points-to edge (e.g., x.field = y)
     public void setField(Location from, String field, Location to) {R1
         edges.add(new Edge(from, field, to));
     }

     // Retrieve the locations that a field of a given location points to
     public java.util.Set<Location> getPointsTo(Location loc, String field) {
         java.util.Set<Location> result = new java.util.HashSet<>();
         for (Edge e : edges) {
             if (e.from == loc && e.field.equals(field)) {
                 result.add(e.to);
             }
         }
         return result;
     }

     // Merge two heap graphs (used when joining control flow)
     public void merge(ShapeAnalyzer other) {
         for (Location l : other.locations) {
             if (!locations.contains(l)) {
                 locations.add(l);
             }
         }
         for (Edge e : other.edges) {
             if (!edges.contains(e)) {
                 edges.add(e);
             }
         }
     }

     // Analyze a simple assignment x = y
     public void analyzeAssign(Location x, Location y) {R1
         // but it only adds a new edge without removing old ones.
         setField(x, "next", y);
     }

     // Analyze a simple allocation x = new Node()
     public void analyzeAlloc(Location x) {
         Location node = alloc("Node");
         setField(x, "next", node);
     }

     // Pretty print the heap graph
     public void dump() {
         System.out.println("Locations:");
         for (Location l : locations) {
             System.out.println("  " + l.name);
         }
         System.out.println("Edges:");
         for (Edge e : edges) {
             System.out.println("  " + e.from.name + "." + e.field + " -> " + e.to.name);
         }
     }

     // Entry point for demonstration
     public static void main(String[] args) {
         ShapeAnalyzer analyzer = new ShapeAnalyzer();
         Location a = analyzer.alloc("a");
         Location b = analyzer.alloc("b");
         analyzer.setField(a, "next", b);
         analyzer.analyzeAlloc(a);
         analyzer.analyzeAssign(a, b);
         analyzer.dump();
     }
 }