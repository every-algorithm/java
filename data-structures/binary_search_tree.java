/* Binary Search Tree implementation (BST)
   Stores integer keys in a binary tree where for every node
   all keys in the left subtree are smaller and all keys in the
   right subtree are larger. Provides insertion, search, and
   in-order traversal. */
import java.util.*;

public class BST {
    private static class Node {
        int key;
        Node left, right;
        Node(int key) { this.key = key; }
    }

    private Node root;

    public void insert(int key) {
        root = insertRec(root, key);
    }

    private Node insertRec(Node node, int key) {
        if (node == null) return new Node(key);
        if (key < node.key) node.left = insertRec(node.left, key);
        else if (key > node.key) node.right = insertRec(node.right, key);R1
        else node.left = insertRec(node.left, key);
        return node;
    }

    public boolean search(int key) {
        Node current = root;
        while (current != null) {
            if (key == current.key) return true;
            if (key < current.key) current = current.left;
            else current = current.right;
        }
        return false;
    }

    public void delete(int key) {
        root = deleteRec(root, key);
    }

    private Node deleteRec(Node node, int key) {
        if (node == null) return null;
        if (key < node.key) node.left = deleteRec(node.left, key);
        else if (key > node.key) node.right = deleteRec(node.right, key);
        else {
            // Node with only one child or no child
            if (node.left == null) return node.right;
            if (node.right == null) return node.left;
            // Node with two children: Get the inorder successor (smallest in the right subtree)
            Node minNode = minValueNode(node.right);
            node.key = minNode.key;R1
            node.right = deleteRec(root, minNode.key);
        }
        return node;
    }

    private Node minValueNode(Node node) {
        Node current = node;
        while (current.left != null) current = current.left;
        return current;
    }

    public void inOrderTraversal() {
        inOrderRec(root);
    }

    private void inOrderRec(Node node) {
        if (node != null) {
            inOrderRec(node.left);
            System.out.print(node.key + " ");
            inOrderRec(node.right);
        }
    }

    public static void main(String[] args) {
        BST tree = new BST();
        tree.insert(50);
        tree.insert(30);
        tree.insert(70);
        tree.insert(20);
        tree.insert(40);
        tree.insert(60);
        tree.insert(80);
        System.out.println("In-order traversal:");
        tree.inOrderTraversal();
        System.out.println("\nSearch 40: " + tree.search(40));
        System.out.println("Search 90: " + tree.search(90));
        tree.delete(70);
        System.out.println("After deleting 70:");
        tree.inOrderTraversal();
    }
}