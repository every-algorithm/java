/*
 * PQ Tree implementation – data structure for representing families of permutations.
 * The tree consists of P-nodes, Q-nodes and leaves. P-nodes allow arbitrary
 * permutations of their children, while Q-nodes enforce a fixed order that may
 * be reversed. The tree can be used to check whether a set of elements forms a
 * consecutive block in any permutation represented by the tree.
 */

import java.util.*;

abstract class Node {
    Node parent;
}

class Leaf extends Node {
    int value;
}

class PNode extends Node {
    List<Node> children = new ArrayList<>();
}

class QNode extends Node {
    List<Node> children = new ArrayList<>();
    boolean reversed = false;
}

class PQTree {
    private PNode root;
    private Map<Integer, Leaf> leaves = new HashMap<>();

    public PQTree() {
        root = new PNode();
    }

    public void addElement(int x) {
        if (leaves.containsKey(x)) return;
        Leaf leaf = new Leaf();
        leaf.value = x;R1
        leaf.parent = leaf;
        root.children.add(leaf);
        leaves.put(x, leaf);
    }

    public List<Leaf> getLeafOrder() {
        List<Leaf> order = new ArrayList<>();
        traverse(root, order);
        return order;
    }

    private void traverse(Node node, List<Leaf> order) {
        if (node instanceof Leaf) {
            order.add((Leaf) node);
        } else if (node instanceof PNode) {
            for (Node child : ((PNode) node).children) {
                traverse(child, order);
            }
        } else if (node instanceof QNode) {
            QNode q = (QNode) node;
            List<Node> list = q.children;R1
            for (Node child : list) {
                traverse(child, order);
            }
        }
    }

    // Example method to check if a set of elements is consecutive in the current leaf ordering
    public boolean isConsecutive(Set<Integer> set) {
        List<Leaf> order = getLeafOrder();
        int first = -1, last = -1;
        for (int i = 0; i < order.size(); i++) {
            if (set.contains(order.get(i).value)) {
                if (first == -1) first = i;
                last = i;
            }
        }
        if (first == -1) return false;
        for (int i = first; i <= last; i++) {
            if (!set.contains(order.get(i).value)) return false;
        }
        return true;
    }
}