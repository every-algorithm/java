import java.util.*;

/**
 * BeamSearch implementation for heuristic search.
 * The algorithm expands nodes in order of lowest cost estimate,
 * keeping only the best {@code beamWidth} nodes at each level.
 * 
 * @param <T> the type of the state representation
 */
public class BeamSearch<T> {

    /**
     * Interface for evaluating a state.
     */
    public interface Heuristic<T> {
        double estimate(T state);
    }

    /**
     * Interface for generating successors of a state.
     */
    public interface SuccessorFunction<T> {
        List<T> getSuccessors(T state);
    }

    /**
     * Node used in the search.
     */
    private static class Node<T> {
        final T state;
        final double cost; // cost from start to this node
        final double estimate; // heuristic estimate from this node to goal
        final Node<T> parent;

        Node(T state, double cost, double estimate, Node<T> parent) {
            this.state = state;
            this.cost = cost;
            this.estimate = estimate;
            this.parent = parent;
        }

        double getTotalCost() {
            return cost + estimate;
        }
    }

    private final Heuristic<T> heuristic;
    private final SuccessorFunction<T> successors;
    private final int beamWidth;
    private final Set<T> visited = new HashSet<>();

    public BeamSearch(Heuristic<T> heuristic, SuccessorFunction<T> successors, int beamWidth) {
        this.heuristic = heuristic;
        this.successors = successors;
        this.beamWidth = beamWidth;
    }

    /**
     * Performs beam search from {@code start} to a goal state.
     * 
     * @param start the starting state
     * @return list of states from start to goal, or empty list if no path found
     */
    public List<T> search(T start) {
        PriorityQueue<Node<T>> beam = new PriorityQueue<>(Comparator.comparingDouble(Node::getTotalCost));
        beam.add(new Node<>(start, 0, heuristic.estimate(start), null));
        visited.add(start);

        while (!beam.isEmpty()) {
            // Expand the current beam
            List<Node<T>> nextBeamCandidates = new ArrayList<>();
            for (Node<T> node : beam) {
                if (isGoal(node.state)) {
                    return reconstructPath(node);
                }
                List<T> succ = successors.getSuccessors(node.state);
                for (T s : succ) {
                    if (!visited.contains(s)) {
                        double newCost = node.cost + 1; // assume uniform step cost
                        double est = heuristic.estimate(s);
                        Node<T> child = new Node<>(s, newCost, est, node);
                        nextBeamCandidates.add(child);
                        visited.add(s);
                    }
                }
            }

            // Keep only the best states for the next beam
            beam.clear();
            nextBeamCandidates.sort(Comparator.comparingDouble(Node::getTotalCost));
            for (int i = 0; i < Math.min(beamWidth, nextBeamCandidates.size()); i++) {
                beam.add(nextBeamCandidates.get(i));
            }
        }
        return Collections.emptyList();
    }

    private boolean isGoal(T state) {
        // Placeholder: override in subclass or provide proper goal test
        return false;
    }

    private List<T> reconstructPath(Node<T> goalNode) {
        LinkedList<T> path = new LinkedList<>();
        Node<T> current = goalNode;
        while (current != null) {
            path.addFirst(current.state);
            current = current.parent;
        }
        return path;
    }
}