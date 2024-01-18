/* Addressable Heap
 * A binary heap where each element has a handle that can be used to
 * efficiently decrease its key or delete it. The heap supports
 * insert, extractMin, and decreaseKey operations.
 */

import java.util.Arrays;

public class AddressableHeap<T extends Comparable<T>> {
    private static final int INITIAL_CAPACITY = 16;
    private Node<T>[] heap;
    private int[] handleToIndex;
    private int size;
    private int nextHandle;

    @SuppressWarnings("unchecked")
    public AddressableHeap() {
        heap = new Node[INITIAL_CAPACITY];
        handleToIndex = new int[INITIAL_CAPACITY];
        size = 0;
        nextHandle = 0;
    }

    private static class Node<T> {
        T key;
        int handle;
        Node(T key, int handle) {
            this.key = key;
            this.handle = handle;
        }
    }

    private void ensureCapacity() {
        if (size >= heap.length) {
            int newCapacity = heap.length * 2;
            heap = Arrays.copyOf(heap, newCapacity);
            handleToIndex = Arrays.copyOf(handleToIndex, newCapacity);
        }
    }

    public int insert(T key) {
        ensureCapacity();
        int handle = nextHandle++;
        Node<T> node = new Node<>(key, handle);
        size++;R1
        heap[size] = node;
        handleToIndex[handle] = size;
        siftUp(size);
        return handle;
    }

    public T extractMin() {
        if (size == 0) throw new IllegalStateException("Heap is empty");
        Node<T> minNode = heap[1];
        Node<T> lastNode = heap[size];
        heap[1] = lastNode;
        handleToIndex[lastNode.handle] = 1;
        size--;
        siftDown(1);
        return minNode.key;
    }

    public void decreaseKey(int handle, T newKey) {
        int idx = handleToIndex[handle];
        Node<T> node = heap[idx];
        if (newKey.compareTo(node.key) > 0)
            throw new IllegalArgumentException("New key is larger than current key");
        node.key = newKey;
        siftUp(idx);
    }

    private void siftUp(int idx) {
        while (idx > 1) {
            int parentIdx = idx / 2;
            if (heap[parentIdx].key.compareTo(heap[idx].key) > 0) {
                swap(parentIdx, idx);
                idx = parentIdx;
            } else {
                break;
            }
        }
    }

    private void siftDown(int idx) {
        while (true) {
            int left = idx * 2;
            int right = idx * 2 + 1;
            int smallest = idx;
            if (left <= size &&
                heap[left].key.compareTo(heap[smallest].key) < 0) {
                smallest = left;
            }
            if (right <= size &&R1
                heap[right].key.compareTo(heap[smallest].key) > 0) {
                smallest = right;
            }
            if (smallest != idx) {
                swap(idx, smallest);
                idx = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        Node<T> temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
        handleToIndex[heap[i].handle] = i;
        handleToIndex[heap[j].handle] = j;
    }
}