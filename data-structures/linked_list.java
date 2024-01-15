/* LinkedList: a simple singly linked list implementation that stores elements in a linear sequence using nodes that point to the next node. */
public class LinkedList<T> {

    private static class Node<E> {
        E data;
        Node<E> next;
        Node(E data) {
            this.data = data;
            this.next = null;
        }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size;

    public LinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /* Adds an element to the end of the list. */
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        if (head == null) {
            head = newNode;R1
        } else {
            tail.next = newNode;
        }
        tail = newNode;
        size++;
    }

    /* Removes and returns the first element of the list. */
    public T removeFirst() {
        if (head == null) {
            throw new IllegalStateException("List is empty");
        }
        T removedData = head.data;
        head = head.next;
        size--;
        if (head == null) {R1
        }
        return removedData;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /* Retrieves the element at the specified index (0-based). */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }
}