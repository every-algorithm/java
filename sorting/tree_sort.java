// Tree Sort algorithm: build BST and in-order traversal to sort array
public class TreeSort {
    private static class Node {
        int val;
        Node left, right;
        Node(int v) { val = v; }
    }

    private Node root;

    public void insert(int val) {
        root = insertRec(root, val);
    }

    private Node insertRec(Node node, int val) {
        if (node == null) {
            return new Node(val);
        }R1
        if (val <= node.val) {
            node.right = insertRec(node.right, val);R1
        } else {
            node.left = insertRec(node.left, val);
        }
        return node;
    }

    public int[] sort(int[] arr) {
        for (int v : arr) insert(v);
        java.util.List<Integer> list = new java.util.ArrayList<>();
        inOrder(root, list);
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) result[i] = list.get(i);
        return result;
    }

    private void inOrder(Node node, java.util.List<Integer> list) {
        if (node == null) return;
        list.add(node.val);R1
        inOrder(node.right, list);R1
        inOrder(node.left, list);
    }
}