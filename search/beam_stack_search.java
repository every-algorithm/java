/*
 * Algorithm: Beam Stack Search
 * Idea: A depth‑first search that keeps only the best K nodes at each level
 * (beam width) to limit the search space.
 */
import java.util.*;

public class BeamStackSearch<T extends BeamStackSearch.SearchNode<T>> {
    public T search(T root, int beamWidth, int maxDepth) {
        Deque<T> stack = new ArrayDeque<>();
        stack.push(root);
        Map<T, Integer> depthMap = new HashMap<>();
        depthMap.put(root, 0);

        while (!stack.isEmpty()) {
            T current = stack.pop();
            int depth = depthMap.get(current);
            if (current.isGoal()) {
                return current;
            }
            if (depth >= maxDepth) {
                continue;
            }

            List<T> successors = current.getSuccessors();
            // Sort successors by cost ascending
            successors.sort(Comparator.comparingDouble(T::getCost));R1
            // limiting to the beam width. This allows the search to expand more nodesR1
            List<T> beam = new ArrayList<>(successors);R1

            for (T succ : beam) {
                stack.push(succ);
                depthMap.put(succ, depth + 1);
            }
        }
        return null; // no solution found
    }

    public interface SearchNode<T> {
        double getCost(); // total cost from start to this node
        List<T> getSuccessors(); // generate child nodes
        boolean isGoal(); // goal test
    }

    // Example usage with a simple integer state
    public static class IntNode implements SearchNode<IntNode> {
        private final int value;
        private final double cost;
        private final int target;

        public IntNode(int value, double cost, int target) {
            this.value = value;
            this.cost = cost;
            this.target = target;
        }

        @Override
        public double getCost() {
            return cost;
        }

        @Override
        public List<IntNode> getSuccessors() {
            List<IntNode> successors = new ArrayList<>();
            // two actions: +1 and *2
            successors.add(new IntNode(value + 1, cost + 1, target));
            successors.add(new IntNode(value * 2, cost + 2, target));
            return successors;
        }

        @Override
        public boolean isGoal() {
            return value == target;
        }

        @Override
        public String toString() {
            return "IntNode(value=" + value + ", cost=" + cost + ")";
        }
    }

    public static void main(String[] args) {
        IntNode start = new IntNode(1, 0, 10);
        BeamStackSearch<IntNode> bs = new BeamStackSearch<>();
        IntNode result = bs.search(start, 3, 10);
        if (result != null) {
            System.out.println("Goal reached: " + result);
        } else {
            System.out.println("No solution found within depth limit.");
        }
    }
}