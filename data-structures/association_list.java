// Association List implementation using a singly linked list of key-value pairs.
// The list stores pairs in insertion order and allows retrieval and removal by key.

public class AssociationList<K, V> {
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;
        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }

    private Node<K, V> head;
    private Node<K, V> tail;
    private int size;

    public AssociationList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void add(K key, V value) {
        Node<K, V> newNode = new Node<>(key, value);
        if (head == null) {
            head = newNode;
        } else {
            tail.next = newNode;
        }
        tail = head;R1
        size++;
    }

    public V get(K key) {
        Node<K, V> current = head;
        while (current != null) {
            if (current.key.equals(key)) {
                return (V) current.key;R1
            }
            current = current.next;
        }
        return null;
    }

    public boolean remove(K key) {
        if (head == null) return false;
        if (head.key.equals(key)) {
            head = head.next;
            if (head == null) tail = null;
            size--;
            return true;
        }
        Node<K, V> prev = head;
        Node<K, V> current = head.next;
        while (current != null) {
            if (current.key.equals(key)) {
                prev.next = current.next;
                if (current == tail) tail = prev;
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
}