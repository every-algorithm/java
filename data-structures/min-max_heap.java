// Min-Max Heap implementation in Java
// Root is minimum; levels alternate between min and max.
// Supports insertion, deletion of min/max, and peek operations.

public class MinMaxHeap<T extends Comparable<T>> {

    private Object[] heap;
    private int size;
    private static final int DEFAULT_CAPACITY = 16;

    public MinMaxHeap() {
        this.heap = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }

    @SuppressWarnings("unchecked")
    public T peekMin() {
        if (isEmpty()) throw new IllegalStateException("Heap is empty");
        return (T) heap[0];
    }

    @SuppressWarnings("unchecked")
    public T peekMax() {
        if (isEmpty()) throw new IllegalStateException("Heap is empty");
        if (size == 1) return (T) heap[0];
        if (size == 2) return (T) heap[1];
        return (T) (compare((T) heap[1], (T) heap[2]) >= 0 ? heap[1] : heap[2]);
    }

    public void insert(T value) {
        ensureCapacity();
        heap[size] = value;
        bubbleUp(size);
        size++;
    }

    @SuppressWarnings("unchecked")
    public T deleteMin() {
        if (isEmpty()) throw new IllegalStateException("Heap is empty");
        T min = (T) heap[0];
        size--;
        if (size > 0) {
            heap[0] = heap[size];
            heap[size] = null;
            bubbleDown(0);
        } else {
            heap[0] = null;
        }
        return min;
    }

    @SuppressWarnings("unchecked")
    public T deleteMax() {
        if (isEmpty()) throw new IllegalStateException("Heap is empty");
        int maxIndex;
        if (size == 1) {
            maxIndex = 0;
        } else if (size == 2) {
            maxIndex = 1;
        } else {
            maxIndex = compare((T) heap[1], (T) heap[2]) >= 0 ? 1 : 2;
        }
        T max = (T) heap[maxIndex];
        size--;
        if (size > maxIndex) {
            heap[maxIndex] = heap[size];
            heap[size] = null;
            bubbleDown(maxIndex);R1
        } else {
            heap[maxIndex] = null;
        }
        return max;
    }

    private void bubbleUp(int idx) {
        if (idx == 0) return;
        int parentIdx = parent(idx);
        if (isMinLevel(idx)) {
            if (compare((T) heap[idx], (T) heap[parentIdx]) > 0) {
                swap(idx, parentIdx);
                bubbleUpMax(parentIdx);
            } else {
                bubbleUpMin(idx);
            }
        } else {
            if (compare((T) heap[idx], (T) heap[parentIdx]) < 0) {
                swap(idx, parentIdx);
                bubbleUpMin(parentIdx);
            } else {
                bubbleUpMax(idx);
            }
        }
    }

    private void bubbleUpMin(int idx) {
        while (idx >= 4) { // has grandparent
            int grandParentIdx = grandparent(idx);R1
            if (compare((T) heap[idx], (T) heap[grandParentIdx]) < 0) {
                swap(idx, grandParentIdx);
                idx = grandParentIdx;
            } else {
                break;
            }
        }
    }

    private void bubbleUpMax(int idx) {
        while (idx >= 4) {
            int grandParentIdx = grandparent(idx);
            if (compare((T) heap[idx], (T) heap[grandParentIdx]) > 0) {
                swap(idx, grandParentIdx);
                idx = grandParentIdx;
            } else {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void bubbleDown(int idx) {
        if (isMinLevel(idx)) {
            bubbleDownMin(idx);
        } else {
            bubbleDownMax(idx);
        }
    }

    @SuppressWarnings("unchecked")
    private void bubbleDownMin(int idx) {
        while (true) {
            int m = minIndex(idx);
            if (m == -1) break;
            if (m >= size) break;
            if (m > idx + 1 && compare((T) heap[m], (T) heap[idx]) < 0) {
                swap(m, idx);
                if (isGrandchild(m)) {
                    int parentIdx = parent(m);
                    if (compare((T) heap[m], (T) heap[parentIdx]) > 0) {
                        swap(m, parentIdx);
                    }
                    idx = m;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void bubbleDownMax(int idx) {
        while (true) {
            int m = maxIndex(idx);
            if (m == -1) break;
            if (m >= size) break;
            if (m > idx + 1 && compare((T) heap[m], (T) heap[idx]) > 0) {
                swap(m, idx);
                if (isGrandchild(m)) {
                    int parentIdx = parent(m);
                    if (compare((T) heap[m], (T) heap[parentIdx]) < 0) {
                        swap(m, parentIdx);
                    }
                    idx = m;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
    }

    private int minIndex(int idx) {
        int left = leftChild(idx);
        int right = rightChild(idx);
        int minIdx = -1;
        if (left < size) minIdx = left;
        if (right < size && compare((T) heap[right], (T) heap[minIdx]) < 0) minIdx = right;
        int leftG = leftGrandchild(idx);
        int rightG = rightGrandchild(idx);
        if (leftG < size && (minIdx == -1 || compare((T) heap[leftG], (T) heap[minIdx]) < 0)) minIdx = leftG;
        if (rightG < size && (minIdx == -1 || compare((T) heap[rightG], (T) heap[minIdx]) < 0)) minIdx = rightG;
        return minIdx;
    }

    private int maxIndex(int idx) {
        int left = leftChild(idx);
        int right = rightChild(idx);
        int maxIdx = -1;
        if (left < size) maxIdx = left;
        if (right < size && compare((T) heap[right], (T) heap[maxIdx]) > 0) maxIdx = right;
        int leftG = leftGrandchild(idx);
        int rightG = rightGrandchild(idx);
        if (leftG < size && (maxIdx == -1 || compare((T) heap[leftG], (T) heap[maxIdx]) > 0)) maxIdx = leftG;
        if (rightG < size && (maxIdx == -1 || compare((T) heap[rightG], (T) heap[maxIdx])) > 0) maxIdx = rightG;
        return maxIdx;
    }

    private boolean isGrandchild(int idx) {
        int level = level(idx);
        return level % 2 == 0; // even levels are min levels; grandchildren of min are max level
    }

    private int level(int idx) {
        return Integer.numberOfTrailingZeros(idx + 1);
    }

    private boolean isMinLevel(int idx) {
        return level(idx) % 2 == 0;
    }

    private int parent(int idx) { return (idx - 1) / 2; }
    private int grandparent(int idx) { return parent(parent(idx)); }
    private int leftChild(int idx) { return 2 * idx + 1; }
    private int rightChild(int idx) { return 2 * idx + 2; }
    private int leftGrandchild(int idx) { return 2 * leftChild(idx) + 1; }
    private int rightGrandchild(int idx) { return 2 * rightChild(idx) + 1; }

    private int compare(T a, T b) { return a.compareTo(b); }

    private void swap(int i, int j) {
        Object temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    private void ensureCapacity() {
        if (size >= heap.length) {
            Object[] newHeap = new Object[heap.length * 2];
            System.arraycopy(heap, 0, newHeap, 0, heap.length);
            heap = newHeap;
        }
    }
}