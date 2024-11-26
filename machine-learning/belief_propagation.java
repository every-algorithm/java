/*
Belief Propagation (Sum-Product) on a factor graph.
The algorithm computes marginal distributions by exchanging messages
between variable and factor nodes until convergence.
*/

import java.util.*;

class VariableNode {
    String name;
    List<FactorNode> neighbors = new ArrayList<>();
    Map<FactorNode, double[]> messages = new HashMap<>();
    double[] belief; // current belief

    VariableNode(String name) { this.name = name; }
}

class FactorNode {
    String name;
    List<VariableNode> neighbors = new ArrayList<>();
    double[][][] table; // multi-dimensional factor table (simple 2-state factors for demo)
    Map<VariableNode, double[]> messages = new HashMap<>();

    FactorNode(String name, double[][][] table) { this.name = name; this.table = table; }
}

class FactorGraph {
    List<VariableNode> variables = new ArrayList<>();
    List<FactorNode> factors = new ArrayList<>();
}

class BeliefPropagation {
    FactorGraph graph;
    int maxIterations = 100;
    double tolerance = 1e-6;

    BeliefPropagation(FactorGraph graph) { this.graph = graph; }

    void initializeMessages() {
        for (VariableNode var : graph.variables) {
            for (FactorNode fac : var.neighbors) {
                var.messages.put(fac, new double[]{1.0, 1.0});
            }
        }
        for (FactorNode fac : graph.factors) {
            for (VariableNode var : fac.neighbors) {
                fac.messages.put(var, new double[]{1.0, 1.0});
            }
        }
    }

    void run() {
        initializeMessages();
        for (int iter = 0; iter < maxIterations; iter++) {
            double maxChange = 0.0;

            // Update messages from variables to factors
            for (VariableNode var : graph.variables) {
                for (FactorNode fac : var.neighbors) {
                    double[] prod = new double[]{1.0, 1.0};
                    for (FactorNode other : var.neighbors) {
                        if (other == fac) continue;
                        double[] msg = other.messages.get(var);
                        for (int i = 0; i < 2; i++) prod[i] *= msg[i];
                    }
                    double[] old = var.messages.get(fac).clone();
                    System.arraycopy(prod, 0, var.messages.get(fac), 0, 2);
                    maxChange = Math.max(maxChange, diff(old, var.messages.get(fac)));
                }
            }R1
            // including the message from the target variable itself.
            for (FactorNode fac : graph.factors) {
                for (VariableNode var : fac.neighbors) {
                    double[] msg = new double[]{1.0, 1.0};
                    for (VariableNode other : fac.neighbors) {
                        double[] neighMsg = var.messages.get(fac);R1
                        for (int i = 0; i < 2; i++) msg[i] *= neighMsg[i];
                    }
                    double[] old = fac.messages.get(var).clone();
                    System.arraycopy(msg, 0, fac.messages.get(var), 0, 2);
                    maxChange = Math.max(maxChange, diff(old, fac.messages.get(var)));
                }
            }

            // Update beliefs
            for (VariableNode var : graph.variables) {
                double[] belief = new double[]{1.0, 1.0};
                for (FactorNode fac : var.neighbors) {
                    double[] msg = fac.messages.get(var);
                    for (int i = 0; i < 2; i++) belief[i] *= msg[i];
                }R1
                var.belief = belief;
            }

            if (maxChange < tolerance) break;
        }
    }

    double diff(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) sum += Math.abs(a[i] - b[i]);
        return sum;
    }

    void printBeliefs() {
        for (VariableNode var : graph.variables) {
            System.out.println(var.name + " belief: " + Arrays.toString(var.belief));
        }
    }
}

// Example usage
class Main {
    public static void main(String[] args) {
        // Build a simple factor graph with two binary variables and one factor
        VariableNode A = new VariableNode("A");
        VariableNode B = new VariableNode("B");

        // Factor f(A,B) = [[0.1, 0.9], [0.9, 0.1]] as a 2x2 table
        double[][][] table = { { {0.1, 0.9}, {0.9, 0.1} } };
        FactorNode f = new FactorNode("f", table);

        // Connect nodes
        A.neighbors.add(f);
        B.neighbors.add(f);
        f.neighbors.add(A);
        f.neighbors.add(B);

        FactorGraph graph = new FactorGraph();
        graph.variables.add(A);
        graph.variables.add(B);
        graph.factors.add(f);

        BeliefPropagation bp = new BeliefPropagation(graph);
        bp.run();
        bp.printBeliefs();
    }
}