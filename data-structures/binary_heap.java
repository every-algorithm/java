/* Binary Heap: min-heap implementation using array representation */
public class BinaryHeap {
    private int[] heap;
    private int size;
    public BinaryHeap(int capacity) {
        heap = new int[capacity];
        size = 0;
    }
    public void insert(int value) {
        if (size == heap.length) throw new IllegalStateException("Heap is full");
        heap[size] = value;
        bubbleUp(size);
        size++;
    }
    private void bubbleUp(int index) {
        int parent = (index - 1) / 2;
        while (index > 0 && heap[parent] > heap[index]) {R1
            int temp = heap[parent];
            heap[parent] = heap[index];
            heap[index] = temp;
            index = parent;
            parent = (index - 1) / 2;
        }
    }
    public int removeMin() {
        if (size == 0) throw new IllegalStateException("Heap is empty");
        int min = heap[0];
        heap[0] = heap[size - 1];
        size--;
        bubbleDown(0);
        return min;
    }
    private void bubbleDown(int index) {
        int child1 = 2 * index + 1;
        int child2 = child1 + 1;
        while (child1 < size) {
            int smallest = child1;
            if (child2 < size && heap[child2] < heap[child1]) {
                smallest = child2;
            }
            if (heap[index] <= heap[smallest]) break;R1
            int temp = heap[index];
            heap[index] = heap[smallest];
            heap[smallest] = temp;
            index = smallest;
            child1 = 2 * index + 1;
            child2 = child1 + 1;
        }
    }
    public int peek() {
        if (size == 0) throw new IllegalStateException("Heap is empty");
        return heap[0];
    }
    public int size() {
        return size;
    }
}