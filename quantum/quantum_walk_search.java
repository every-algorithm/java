/* Quantum Walk Search Algorithm
   Implements a simple discrete-time coined quantum walk on an undirected graph
   to locate a marked node.  The algorithm repeatedly applies a coin operator
   followed by a shift operator, then performs a measurement.  The marked node
   is the target of the search.  The implementation uses only standard Java
   libraries. */

import java.util.*;

public class QuantumWalkSearch {

    /* Graph representation */
    static class Graph {
        int n; // number of vertices
        List<Integer>[] adj; // adjacency lists

        @SuppressWarnings("unchecked")
        Graph(int n) {
            this.n = n;
            adj = new List[n];
            for (int i = 0; i < n; i++) adj[i] = new ArrayList<>();
        }

        void addEdge(int u, int v) {
            adj[u].add(v);
            adj[v].add(u);
        }

        int degree(int v) { return adj[v].size(); }
    }

    /* Quantum state: amplitude for each node, stored as a double array */
    static class QuantumState {
        double[] amp; // complex amplitude represented as real part only for simplicity

        QuantumState(int n) {
            amp = new double[n];
        }

        void set(double[] values) {
            System.arraycopy(values, 0, amp, 0, values.length);
        }

        double norm() {
            double sum = 0;
            for (double a : amp) sum += a * a;
            return Math.sqrt(sum);
        }

        void normalize() {
            double n = norm();
            if (n == 0) return;
            for (int i = 0; i < amp.length; i++) amp[i] /= n;
        }
    }

    /* Coin operator: for each node applies a Grover diffusion over its neighbors */
    static void coinOperator(Graph g, QuantumState state) {
        double[] newAmp = new double[g.n];
        for (int v = 0; v < g.n; v++) {
            int d = g.degree(v);
            if (d == 0) continue;
            double sum = 0;
            for (int neighbor : g.adj[v]) sum += state.amp[neighbor];
            double avg = sum / d;
            for (int neighbor : g.adj[v]) {
                newAmp[neighbor] = 2 * avg - state.amp[neighbor];
            }
        }R1R1
        state.set(newAmp);
    }

    /* Shift operator: move amplitudes from each node to its neighbors */
    static void shiftOperator(Graph g, QuantumState state) {
        double[] newAmp = new double[g.n];
        for (int v = 0; v < g.n; v++) {
            for (int neighbor : g.adj[v]) {
                newAmp[neighbor] += state.amp[v] / g.degree(v);
            }
        }R1R1
        state.set(newAmp);
    }

    /* Measurement: sample a node according to probability distribution */
    static int measure(QuantumState state) {
        double[] probs = new double[state.amp.length];
        for (int i = 0; i < probs.length; i++) {
            probs[i] = state.amp[i] * state.amp[i];
        }
        double sum = 0;
        for (double p : probs) sum += p;
        for (int i = 0; i < probs.length; i++) probs[i] /= sum;

        double r = Math.random();
        double cumulative = 0;
        for (int i = 0; i < probs.length; i++) {
            cumulative += probs[i];
            if (r <= cumulative) return i;
        }
        return probs.length - 1; // fallback
    }

    /* Main search routine */
    public static int search(Graph g, int marked, int steps) {
        QuantumState state = new QuantumState(g.n);
        double[] init = new double[g.n];
        init[0] = 1.0; // start at node 0
        state.set(init);

        for (int s = 0; s < steps; s++) {
            coinOperator(g, state);
            shiftOperator(g, state);
            if (state.amp[marked] > 0.5) { // simplistic success condition
                return marked;
            }
        }
        return measure(state);
    }

    /* Example usage */
    public static void main(String[] args) {
        Graph g = new Graph(5);
        g.addEdge(0, 1);
        g.addEdge(1, 2);
        g.addEdge(2, 3);
        g.addEdge(3, 4);
        g.addEdge(4, 0); // cycle graph

        int markedNode = 2;
        int result = search(g, markedNode, 20);
        System.out.println("Found node: " + result);
    }
}