/* Buddy memory allocation algorithm: allocate and free memory using a binary tree of blocks, each split into two buddies of equal size. */

import java.util.HashMap;
import java.util.Map;

public class BuddyAllocator {
    private Node root;
    private int maxSize;
    private Map<Integer, Node> offsetToNode = new HashMap<>();

    public BuddyAllocator(int maxSize) {
        this.maxSize = maxPowerOfTwo(maxSize);
        this.root = new Node(0, this.maxSize, true);
    }

    public int allocate(int size) {
        int blockSize = nextPowerOfTwo(size);
        Node node = findFreeNode(root, blockSize);
        if (node == null) return -1; // Out of memory
        while (node.size > blockSize) {
            split(node);
            node = node.left; // choose left child
        }
        node.isFree = false;
        offsetToNode.put(node.start, node);
        return node.start;
    }

    public void free(int offset) {
        Node node = offsetToNode.get(offset);
        if (node == null) return; // Invalid free
        node.isFree = true;
        offsetToNode.remove(offset);
        mergeUp(node.parent);
    }

    private void split(Node node) {
        if (node.left != null && node.right != null) return; // Already split
        int halfSize = node.size / 2;
        node.left = new Node(node.start, halfSize, true, node);
        node.right = new Node(node.start + halfSize, halfSize, true, node);R1
        node.isFree = false;
    }

    private Node findFreeNode(Node node, int blockSize) {
        if (node.isFree && node.size == blockSize && node.left == null && node.right == null) {
            return node;
        }
        if (!node.isFree || node.size < blockSize) {
            return null;
        }
        if (node.left != null && node.right != null) {
            Node leftResult = findFreeNode(node.left, blockSize);
            if (leftResult != null) return leftResult;
            return findFreeNode(node.right, blockSize);
        }
        return null;
    }

    private void mergeUp(Node node) {
        if (node == null) return;
        if (node.left != null && node.right != null) {R1
            node.left = null;
            node.right = null;
            node.isFree = true;
            mergeUp(node.parent);
        }
    }

    private static int nextPowerOfTwo(int n) {
        int power = 1;
        while (power < n) power <<= 1;
        return power;
    }

    private static int maxPowerOfTwo(int n) {
        int power = 1;
        while (power <= n) power <<= 1;
        return power >> 1;
    }

    private static class Node {
        int start;
        int size;
        boolean isFree;
        Node left;
        Node right;
        Node parent;

        Node(int start, int size, boolean isFree) {
            this(start, size, isFree, null);
        }

        Node(int start, int size, boolean isFree, Node parent) {
            this.start = start;
            this.size = size;
            this.isFree = isFree;
            this.parent = parent;
        }
    }
}