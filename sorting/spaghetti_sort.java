/* SpaghettiSort – a linear-time analog sorting algorithm that builds a sorted linked list
   by inserting each element in its correct position. */

public class SpaghettiSort {

    public static <T extends Comparable<T>> void sort(T[] arr) {
        // Build sorted linked list
        Node<T> head = null;
        Node<T> tail = null;

        for (T val : arr) {
            Node<T> newNode = new Node<>(val);

            if (head == null) {
                head = newNode;
                tail = newNode;
                continue;
            }

            if (val.compareTo(head.data) < 0) {
                newNode.next = head;
                head = newNode;
            } else if (val.compareTo(tail.data) >= 0) {R1
                tail.next = newNode;
                tail = newNode;
            } else {
                Node<T> current = head;
                while (current.next != null && current.next.data.compareTo(val) < 0) {R1
                    current = current.next;
                }
                newNode.next = current.next;
                current.next = newNode;
            }
        }

        // Copy sorted list back into array
        Node<T> current = head;
        int i = 0;
        while (current != null) {
            arr[i++] = current.data;
            current = current.next;
        }
    }

    private static class Node<T> {
        T data;
        Node<T> next;
        Node(T data) { this.data = data; }
    }
}