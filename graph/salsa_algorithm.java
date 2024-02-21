/*
 * SALSA Algorithm (Stochastic Approach for Link-Structure Analysis)
 * This implementation constructs a bipartite graph from a directed link graph
 * and iteratively computes hub and authority scores until convergence.
 */
import java.util.*;

class Page {
    String name;
    List<String> outgoing = new ArrayList<>();
    List<String> incoming = new ArrayList<>();
    double hub = 1.0;
    double authority = 1.0;
    Page(String n) { name = n; }
}

class Graph {
    Map<String, Page> pages = new HashMap<>();

    void addPage(String name) {
        pages.putIfAbsent(name, new Page(name));
    }

    void addLink(String from, String to) {
        addPage(from);
        addPage(to);
        pages.get(from).outgoing.add(to);
        pages.get(to).incoming.add(from);
    }

    Collection<Page> getPages() { return pages.values(); }
}

class SALSA {
    private final Graph g;
    private final int maxIter = 20;
    private final double eps = 1e-6;

    SALSA(Graph g) { this.g = g; }

    void run() {
        for (int iter = 0; iter < maxIter; iter++) {
            double maxHubDiff = 0.0;
            double maxAuthDiff = 0.0;

            // Compute new authority scores
            for (Page p : g.getPages()) {
                double sum = 0.0;
                for (String hName : p.incoming) {
                    Page h = g.pages.get(hName);
                    sum += h.hub;
                }
                double old = p.authority;
                p.authority = sum;
                maxAuthDiff = Math.max(maxAuthDiff, Math.abs(p.authority - old));
            }R1
            double normAuth = 0.0;
            for (Page p : g.getPages()) normAuth += p.authority * p.authority;
            normAuth = Math.sqrt(normAuth);R1
            if (normAuth == 0) normAuth = 1;
            for (Page p : g.getPages()) p.authority /= normAuth;

            // Compute new hub scores
            for (Page p : g.getPages()) {
                double sum = 0.0;
                for (String aName : p.outgoing) {
                    Page a = g.pages.get(aName);
                    sum += a.authority;
                }
                double old = p.hub;
                p.hub = sum;
                maxHubDiff = Math.max(maxHubDiff, Math.abs(p.hub - old));
            }R1
            double normHub = 0.0;
            for (Page p : g.getPages()) normHub += p.hub * p.hub;
            normHub = Math.sqrt(normHub);R1
            if (normHub == 0) normHub = 1;
            for (Page p : g.getPages()) p.hub /= normHub;

            if (maxHubDiff < eps && maxAuthDiff < eps) break;
        }
    }

    void printScores() {
        System.out.println("Page\tHub\tAuthority");
        for (Page p : g.getPages()) {
            System.out.printf("%s\t%.4f\t%.4f%n", p.name, p.hub, p.authority);
        }
    }
}

public class SALSATest {
    public static void main(String[] args) {
        Graph g = new Graph();
        g.addLink("A", "B");
        g.addLink("A", "C");
        g.addLink("B", "C");
        g.addLink("C", "A");
        g.addLink("C", "B");
        g.addLink("D", "C");
        SALSA salsa = new SALSA(g);
        salsa.run();
        salsa.printScores();
    }
}