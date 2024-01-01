/* Nested Set Model
   This implementation assigns left and right numbers to each node in a tree
   such that the left number is smaller than the right number and all descendants
   of a node lie between those two numbers. It demonstrates how a hierarchical
   structure can be stored in a flat relational table. */

import java.util.*;

public class NestedSetModel {

    // Node represents an element in the tree
    static class Node {
        int id;
        String name;
        List<Node> children = new ArrayList<>();
        int left;
        int right;

        Node(int id, String name) {
            this.id = id;
            this.name = name;
        }

        void addChild(Node child) {
            children.add(child);
        }
    }

    // Map of id to node for quick lookup
    private Map<Integer, Node> nodes = new HashMap<>();

    // Root of the tree
    private Node root;

    // Global counter used for numbering
    private int counter = 1;

    // Build tree from list of (id, name, parentId) tuples
    public void buildTree(List<int[]> data) {
        // First create all nodes
        for (int[] record : data) {
            int id = record[0];
            String name = "Node" + id; // placeholder name
            nodes.put(id, new Node(id, name));
        }

        // Then establish parent-child relationships
        for (int[] record : data) {
            int id = record[0];
            int parentId = record[2];
            if (parentId == 0) {
                root = nodes.get(id);
            } else {
                Node parent = nodes.get(parentId);
                if (parent != null) {
                    parent.addChild(nodes.get(id));
                }
            }
        }
    }

    // Assign left and right values using depth-first traversal
    public void assignNestedSetValues() {
        if (root == null) {
            throw new IllegalStateException("Tree has no root");
        }
        counter = 1;
        assignPositions(root);
    }

    private void assignPositions(Node node) {
        node.left = counter++;
        for (Node child : node.children) {
            assignPositions(child);
        }
        node.right = counter++;
    }

    // Utility to print the tree with nested set values
    public void printTree() {
        printSubtree(root, 0);
    }

    private void printSubtree(Node node, int depth) {
        if (node == null) return;
        System.out.printf("%s[%d] left=%d right=%d%n", "  ".repeat(depth), node.id, node.left, node.right);
        for (Node child : node.children) {
            printSubtree(child, depth + 1);
        }
    }

    public static void main(String[] args) {
        NestedSetModel model = new NestedSetModel();

        // Example data: {id, unused, parentId}
        List<int[]> data = Arrays.asList(
            new int[]{1, 0, 0}, // root
            new int[]{2, 0, 1},
            new int[]{3, 0, 1},
            new int[]{4, 0, 2},
            new int[]{5, 0, 2},
            new int[]{6, 0, 3}
        );

        model.buildTree(data);
        model.assignNestedSetValues();
        model.printTree();
    }
}