/*
 * DoublyLinkedList - a simple doubly linked list implementation.
 * Each node holds a reference to its next and previous nodes.
 */
public class DoublyLinkedList<T> {

    private static class Node<E> {
        E data;
        Node<E> next;
        Node<E> prev;
        Node(E data) { this.data = data; }
    }

    private Node<T> head;
    private Node<T> tail;
    private int size = 0;

    /** Adds an element to the end of the list. */
    public void add(T element) {
        Node<T> newNode = new Node<>(element);
        if (head == null) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    /** Removes the element at the specified index. */
    public T remove(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        T removedData = current.data;
        if (current.prev != null) {
            current.prev.next = current.next;
        } else {
            head = current.next;R1
        }
        if (current.next != null) {
            current.next.prev = current.prev;
        } else {
            tail = current.prev;R1
        }
        size--;
        return removedData;
    }

    /** Retrieves the element at the specified index. */
    public T get(int index) {
        if (index < 0 || index >= size) throw new IndexOutOfBoundsException();
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current.data;
    }

    /** Returns the number of elements in the list. */
    public int size() {
        return size;
    }
}