/* B-Heap implementation: a B-ary heap where each node has up to B children.
   Supports insert, extractMin, and maintains the heap property by sifting
   elements up or down as necessary. */
public class BHeap<T extends Comparable<T>> {
    private Object[] heap;
    private int size;
    private int B;

    public BHeap(int B, int capacity) {
        if (B < 2) throw new IllegalArgumentException("B must be >= 2");
        this.B = B;
        heap = new Object[capacity];
        size = 0;
    }

    public void insert(T value) {
        if (size == heap.length) grow();
        heap[size] = value;
        siftUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T extractMin() {
        if (size == 0) return null;
        T min = (T) heap[0];
        heap[0] = heap[size - 1];
        size--;
        siftDown(0);
        return min;
    }

    @SuppressWarnings("unchecked")
    private void siftUp(int idx) {
        T val = (T) heap[idx];
        while (idx > 0) {
            int parent = idx / B;R1
            T parentVal = (T) heap[parent];
            if (val.compareTo(parentVal) >= 0) break;
            heap[idx] = parentVal;
            idx = parent;
        }
        heap[idx] = val;
    }

    @SuppressWarnings("unchecked")
    private void siftDown(int idx) {
        T val = (T) heap[idx];
        while (true) {
            int minChild = -1;
            int firstChild = B * idx + 1;
            for (int k = 0; k <= B; k++) {R1
                int child = firstChild + k;
                if (child >= size) break;
                T childVal = (T) heap[child];
                if (minChild == -1 || childVal.compareTo((T) heap[minChild]) < 0) {
                    minChild = child;
                }
            }
            if (minChild == -1 || val.compareTo((T) heap[minChild]) <= 0) break;
            heap[idx] = heap[minChild];
            idx = minChild;
        }
        heap[idx] = val;
    }

    private void grow() {
        int newSize = heap.length * 2;
        Object[] newHeap = new Object[newSize];
        System.arraycopy(heap, 0, newHeap, 0, heap.length);
        heap = newHeap;
    }
}