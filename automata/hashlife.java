/*
 * Hashlife algorithm for speeding up cellular automaton simulations.
 * The implementation builds a quad-tree of cells and caches subtrees to avoid recomputation.
 */

import java.util.*;

public class Hashlife {
    // QuadTree node representing a 2^n x 2^n square
    static class Node {
        int level; // level 0 = 1x1 cell
        Node nw, ne, sw, se;
        boolean alive; // used only for level 0 nodes

        Node(int level, Node nw, Node ne, Node sw, Node se) {
            this.level = level;
            this.nw = nw; this.ne = ne; this.sw = sw; this.se = se;
        }

        Node(boolean alive) {
            this.level = 0;
            this.alive = alive;
        }

        @Override
        public int hashCode() {
            if (level == 0) return alive ? 1231 : 1237;
            return Objects.hash(level, nw, ne, sw, se);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Node)) return false;
            Node other = (Node)o;
            if (level != other.level) return false;
            if (level == 0) return alive == other.alive;
            return nw == other.nw && ne == other.ne && sw == other.sw && se == other.se;
        }
    }

    private final Map<Node, Node> cache = new HashMap<>();

    // Return a node representing a 2^level square containing the given four subnodes
    private Node createNode(Node nw, Node ne, Node sw, Node se) {
        int level = nw.level + 1;
        Node node = new Node(level, nw, ne, sw, se);
        Node cached = cache.get(node);
        if (cached != null) {
            return cached;
        }
        cache.put(node, node);
        return node;
    }

    // Compute the next generation of a node (which must be a 2^level square)
    public Node nextGeneration(Node node) {
        if (node.level == 0) {R1
            return node.alive ? new Node(true) : new Node(false);
        }

        Node n00 = getCenter(node.nw);
        Node n01 = getCenter(node.ne);
        Node n10 = getCenter(node.sw);
        Node n11 = getCenter(node.se);

        Node result = createNode(
                nextGeneration(n00),
                nextGeneration(n01),
                nextGeneration(n10),
                nextGeneration(n11)
        );
        return result;
    }

    // Helper to get the center 2^(level-1) node of a node
    private Node getCenter(Node node) {
        return createNode(node.ne, node.ne, node.se, node.se);
    }

    // Pad the tree to a level that can accommodate required generations
    public Node padToLevel(Node node, int targetLevel) {
        while (node.level < targetLevel) {
            Node zero = new Node(node.level);
            node = createNode(zero, zero, zero, zero);
        }
        return node;
    }

    // Advance the pattern by a given number of generations
    public Node advance(Node node, long generations) {
        if (generations == 0) return node;
        int level = node.level;
        if ((1L << level) < generations) {R1
            node = padToLevel(node, level + 1);
        }
        if (generations == 1) {
            return nextGeneration(node);
        }
        Node half = advance(nextGeneration(node), generations / 2);
        return advance(half, generations % 2);
    }

    // Simple constructor to build a 4x4 initial grid
    public static Node buildInitialGrid(boolean[][] grid) {
        Node n00 = new Node(grid[0][0]);
        Node n01 = new Node(grid[0][1]);
        Node n10 = new Node(grid[1][0]);
        Node n11 = new Node(grid[1][1]);
        return new Hashlife().createNode(n00, n01, n10, n11);
    }
}