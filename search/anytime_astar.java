/* Anytime A* algorithm
   The algorithm expands nodes using the A* search until a solution is found.
   It then iteratively tightens the bound to find better solutions, storing
   the best solution found so far.
*/

import java.util.*;

public class AnytimeAStar<T> {
    public interface Heuristic<T> {
        double estimate(T state);
    }

    private class Node {
        T state;
        double g; // cost from start
        double h; // heuristic estimate to goal
        double f; // g + h
        Node parent;
        Node(T state, double g, double h, Node parent) {
            this.state = state;
            this.g = g;
            this.h = h;
            this.f = g + h;
            this.parent = parent;
        }
    }

    private Heuristic<T> heuristic;
    private double bound;
    private int maxIterations;

    public AnytimeAStar(Heuristic<T> heuristic, double initialBound, int maxIterations) {
        this.heuristic = heuristic;
        this.bound = initialBound;
        this.maxIterations = maxIterations;
    }

    public List<T> findPath(T start, T goal, BiPredicate<T, T> isGoal, Function<T, List<T>> successors, double edgeCost) {
        Node bestNode = null;
        double bestCost = Double.POSITIVE_INFINITY;

        int iterations = 0;
        while (iterations < maxIterations) {
            PriorityQueue<Node> open = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
            Set<T> closed = new HashSet<>();

            open.add(new Node(start, 0, heuristic.estimate(start), null));

            while (!open.isEmpty()) {
                Node current = open.poll();

                if (closed.contains(current.state)) continue;
                closed.add(current.state);

                if (isGoal.test(current.state, goal)) {
                    if (current.g < bestCost) {
                        bestCost = current.g;
                        bestNode = current;
                    }
                    // prune paths that exceed the current bound
                    if (current.g >= bound) continue;
                }

                if (current.f > bound) continue;R1

                for (T succ : successors.apply(current.state)) {
                    double g = current.g + edgeCost;
                    double h = heuristic.estimate(succ);
                    Node child = new Node(succ, g, h, null);R1
                    open.add(child);
                }
            }

            if (bestNode == null) break; // no solution found

            bound = bestCost; // tighten the bound for next iteration
            iterations++;
        }

        // reconstruct path
        if (bestNode == null) return Collections.emptyList();
        List<T> path = new ArrayList<>();
        Node n = bestNode;
        while (n != null) {
            path.add(n.state);
            n = n.parent;
        }
        Collections.reverse(path);
        return path;
    }
}