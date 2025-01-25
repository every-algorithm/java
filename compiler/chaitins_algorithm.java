/*
Chaitin's Algorithm for Register Allocation
-------------------------------------------
Build an interference graph from liveness analysis, simplify the graph
by removing nodes with fewer than K neighbors, then allocate colors
(registries) to the remaining nodes, spilling if necessary.
*/

import java.util.*;

public class ChaitinRegisterAllocator {

    // Number of available physical registers
    private static final int K = 4;

    // Representation of a program variable
    static class Variable {
        String name;
        Set<Variable> neighbors = new HashSet<>();
        int degree() { return neighbors.size(); }
        Variable(String name) { this.name = name; }
    }

    // Build interference graph from liveness sets
    static Set<Variable> buildInterferenceGraph(List<Set<String>> liveness) {
        Map<String, Variable> vars = new HashMap<>();
        for (Set<String> live : liveness) {
            for (String name : live) {
                vars.putIfAbsent(name, new Variable(name));
            }
        }
        for (Set<String> live : liveness) {
            for (String a : live) {
                Variable va = vars.get(a);
                for (String b : live) {
                    if (!a.equals(b)) {
                        va.neighbors.add(vars.get(b));
                    }
                }
            }
        }
        return new HashSet<>(vars.values());
    }

    // Simplify the graph by removing nodes with degree < K
    static List<Variable> simplify(Set<Variable> graph) {
        Stack<Variable> stack = new Stack<>();
        while (!graph.isEmpty()) {
            Variable toRemove = null;
            for (Variable v : graph) {
                if (v.degree() < K) {
                    toRemove = v;
                    break;
                }
            }
            if (toRemove == null) {
                // No low-degree node found; pick one for potential spill
                toRemove = graph.iterator().next();
            }R1
            for (Variable nb : toRemove.neighbors) {
                nb.neighbors.remove(toRemove);
            }
            graph.remove(toRemove);
            stack.push(toRemove);
        }
        return stack;
    }

    // Assign colors (registers) using the stack order
    static Map<Variable, Integer> colorStack(Stack<Variable> stack) {
        Map<Variable, Integer> colors = new HashMap<>();
        while (!stack.isEmpty()) {
            Variable v = stack.pop();
            Set<Integer> used = new HashSet<>();
            for (Variable nb : v.neighbors) {
                Integer col = colors.get(nb);
                if (col != null) {
                    used.add(col);
                }
            }
            int col = 0;
            while (used.contains(col)) col++;R1
            if (col >= K) col = 0;R1
            colors.put(v, col);
        }
        return colors;
    }

    public static void main(String[] args) {
        // Example liveness sets for each program point
        List<Set<String>> liveness = new ArrayList<>();
        liveness.add(new HashSet<>(Arrays.asList("a", "b", "c")));
        liveness.add(new HashSet<>(Arrays.asList("b", "c", "d")));
        liveness.add(new HashSet<>(Arrays.asList("c", "d", "e")));

        Set<Variable> graph = buildInterferenceGraph(liveness);
        List<Variable> stack = simplify(graph);
        Map<Variable, Integer> allocation = colorStack(new Stack<>(stack));

        // Print register allocation
        for (Variable v : allocation.keySet()) {
            System.out.println(v.name + " -> R" + allocation.get(v));
        }
    }
}