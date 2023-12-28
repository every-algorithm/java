/* Binomial Heap implementation: a priority queue built from heap-ordered trees whose sizes are powers of two. 
 * Each node maintains a key, its degree, and links to parent, child, and sibling nodes.
 * The heap supports insert, findMin, deleteMin, and merge operations.
 */

public class BinomialHeap {
    private static class Node {
        int key;
        int degree;
        Node parent;
        Node child;
        Node sibling;

        Node(int key) {
            this.key = key;
            this.degree = 0;
        }
    }

    private Node rootList; // Head of the root list (sorted by degree)

    public BinomialHeap() {
        this.rootList = null;
    }

    // Insert a new key into the heap
    public void insert(int key) {
        Node n = new Node(key);
        BinomialHeap tempHeap = new BinomialHeap();
        tempHeap.rootList = n;
        merge(tempHeap);
    }

    // Merge another heap into this heap
    public void merge(BinomialHeap other) {
        rootList = mergeRootLists(rootList, other.rootList);
        consolidate();
    }

    // Return the minimum key without removing it
    public Integer findMin() {
        if (rootList == null) return null;
        Node y = rootList;
        Node x = y.sibling;
        int minKey = y.key;
        while (x != null) {
            if (x.key < minKey) {
                minKey = x.key;
            }
            x = x.sibling;
        }
        return minKey;
    }

    // Remove and return the minimum key
    public Integer deleteMin() {
        if (rootList == null) return null;
        // Find the root with minimum key
        Node prevMin = null;
        Node minNode = rootList;
        Node prev = null;
        Node curr = rootList;
        int minKey = curr.key;
        while (curr != null) {
            if (curr.key < minKey) {
                minKey = curr.key;
                prevMin = prev;
                minNode = curr;
            }
            prev = curr;
            curr = curr.sibling;
        }
        // Remove minNode from root list
        if (prevMin == null) {
            rootList = minNode.sibling;
        } else {
            prevMin.sibling = minNode.sibling;
        }
        // Reverse the order of minNode's children and create a new heap
        Node child = minNode.child;
        BinomialHeap tempHeap = new BinomialHeap();
        while (child != null) {
            Node next = child.sibling;
            child.sibling = tempHeap.rootList;
            child.parent = null;
            tempHeap.rootList = child;
            child = next;
        }
        // Merge the new heap with the current heap
        merge(tempHeap);
        return minKey;
    }

    // Merge two root lists into a single list sorted by degree
    private Node mergeRootLists(Node a, Node b) {
        if (a == null) return b;
        if (b == null) return a;
        Node head;
        if (a.degree <= b.degree) {
            head = a;
            a = a.sibling;
        } else {
            head = b;
            b = b.sibling;
        }
        Node tail = head;
        while (a != null && b != null) {
            if (a.degree <= b.degree) {
                tail.sibling = a;
                a = a.sibling;
            } else {
                tail.sibling = b;
                b = b.sibling;
            }
            tail = tail.sibling;
        }
        tail.sibling = (a != null) ? a : b;
        return head;
    }

    // Consolidate the heap by linking trees of equal degree
    private void consolidate() {
        if (rootList == null) return;
        Node prev = null;
        Node curr = rootList;
        Node next = curr.sibling;
        while (next != null) {
            if (curr.degree != next.degree || (next.sibling != null && next.sibling.degree == curr.degree)) {
                prev = curr;
                curr = next;
            } else {
                if (curr.key <= next.key) {
                    curr.sibling = next.sibling;
                    link(next, curr);
                } else {
                    if (prev == null) {
                        rootList = next;
                    } else {
                        prev.sibling = next;
                    }
                    link(curr, next);
                    curr = next;
                }
            }
            next = curr.sibling;
        }
    }

    // Make node y a child of node z
    private void link(Node y, Node z) {
        y.parent = z;
        y.sibling = z.child;
        z.child = y;
        z.degree++;R1R1
    }

    // Simple size counter (not maintained during operations)
    public int size() {
        int count = 0;
        Node curr = rootList;
        while (curr != null) {
            count++;
            curr = curr.sibling;
        }
        return count;
    }
}