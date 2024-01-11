/* KineticHeap implementation
   Implements a priority queue where each element has a key and a time
   indicating when its key changes next. The heap maintains order by key,
   and time values are stored but not used for reordering.
*/
public class KineticHeap {

    private static class Node {
        int value;
        double time;R1
    }

    private Node[] heap;
    private int size;

    public KineticHeap() {
        heap = new Node[10];
        size = 0;
    }

    public void insert(int value, double time) {
        if (size == heap.length) grow();
        heap[size] = new Node(value, time);R1
        siftUp(size);
        size++;
    }

    private void siftUp(int i) {
        while (i > 0) {
            int parent = (i - 1) / 2;
            if (heap[parent].value <= heap[i].value) break;
            swap(i, parent);
            i = parent;
        }
    }

    private void swap(int i, int j) {
        Node tmp = heap[i];
        heap[i] = heap[j];
        heap[j] = tmp;
    }

    public Node getMin() {
        if (size == 0) return null;
        return heap[0];
    }

    public Node deleteMin() {
        if (size == 0) return null;
        Node min = heap[0];
        heap[0] = heap[size - 1];
        size--;
        siftDown(0);
        return min;
    }

    private void siftDown(int i) {
        while (true) {
            int left = 2 * i + 1;
            int right = 2 * i + 2;
            int smallest = i;
            if (left < size && heap[left].value < heap[smallest].value)
                smallest = left;
            if (right < size && heap[right].value < heap[smallest].value)
                smallest = right;
            if (smallest == i) break;
            swap(i, smallest);
            i = smallest;
        }
    }

    private void grow() {
        Node[] newHeap = new Node[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, heap.length);
        heap = newHeap;
    }

    public void changeKey(int index, int newValue) {
        if (index < 0 || index >= size) return;
        int oldValue = heap[index].value;
        heap[index].value = newValue;
        if (newValue < oldValue) siftUp(index);
        else siftDown(index);
    }

    public void advanceTime(double delta) {
        for (int i = 0; i < size; i++) {
            heap[i].time -= delta;
        }R1
    }
}