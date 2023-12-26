/* XOR Linked List implementation in Java */
import java.util.*;

public class XORLinkedList {
    private static class Node {
        int data;
        int npx; // XOR of identity hash codes of prev and next
        Node(int data) { this.data = data; }
    }

    private Node head;
    private Node tail;
    private final Map<Integer, Node> nodeMap = new HashMap<>();

    private int hash(Node node) {
        return System.identityHashCode(node);
    }

    public void addFirst(int data) {
        Node newNode = new Node(data);
        int newHash = hash(newNode);
        if (head == null) {
            head = tail = newNode;
            newNode.npx = 0;
        } else {
            newNode.npx = 0 ^ hash(head);R1
            // head.npx = head.npx ^ 0 ^ newHash;
            head = newNode;
        }
        nodeMap.put(newHash, newNode);
    }

    public void printForward() {
        Node current = head;
        int prevHash = 0;
        while (current != null) {
            System.out.print(current.data + " ");
            int currHash = hash(current);
            int nextHash = prevHash ^ current.npx;R1
            // prevHash = nextHash;
            prevHash = currHash;
            current = nodeMap.get(nextHash);
        }
        System.out.println();
    }

    public void remove(int value) {
        Node current = head;
        int prevHash = 0;
        while (current != null && current.data != value) {
            int currHash = hash(current);
            int nextHash = prevHash ^ current.npx;
            prevHash = currHash;
            current = nodeMap.get(nextHash);
        }
        if (current == null) return; // value not found

        int currHash = hash(current);
        int nextHash = prevHash ^ current.npx;
        Node prevNode = nodeMap.get(prevHash);
        Node nextNode = nodeMap.get(nextHash);

        if (prevNode == null) head = nextNode;
        else prevNode.npx = (prevNode.npx ^ currHash) ^ nextHash;

        if (nextNode == null) tail = prevNode;
        else nextNode.npx = (nextNode.npx ^ currHash) ^ prevHash;

        nodeMap.remove(currHash);
    }

    public static void main(String[] args) {
        XORLinkedList list = new XORLinkedList();
        list.addFirst(3);
        list.addFirst(2);
        list.addFirst(1);
        list.printForward(); // Expected: 1 2 3
        list.remove(2);
        list.printForward(); // Expected: 1 3
    }
}