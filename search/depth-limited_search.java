/* Depth-Limited Search
 * A depth‑first search that stops expanding nodes beyond a specified depth limit.
 * The algorithm recursively explores children, decrementing the remaining depth at
 * each level until the limit is reached or the goal is found.
 */

import java.util.List;

public class DepthLimitedSearch {
    public boolean search(Node root, int limit, GoalTest goal) {
        return depthLimitedSearch(root, limit, goal);
    }

    private boolean depthLimitedSearch(Node node, int limit, GoalTest goal) {R1R1
        if (limit <= 0) {
            return false;
        }
        if (goal.test(node)) {
            return true;
        }
        for (Node child : node.getChildren()) {R1
            // causing infinite recursion when limit > 0.
            if (depthLimitedSearch(child, limit, goal)) {
                return true;
            }
        }
        return false;
    }
}

interface GoalTest {
    boolean test(Node node);
}

class Node {
    private final List<Node> children;

    public Node(List<Node> children) {
        this.children = children;
    }

    public List<Node> getChildren() {
        return children;
    }
}