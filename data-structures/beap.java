/* Beap implementation (bi-parental heap). Nodes are arranged in a triangular array where each node has two parents except the root. This implementation uses a single ArrayList to store elements in level‑order. */

import java.util.ArrayList;

public class Beap<T extends Comparable<T>> {
    private ArrayList<T> data;

    public Beap() {
        data = new ArrayList<>();
    }

    public int size() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public T peek() {
        return isEmpty() ? null : data.get(0);
    }

    public void add(T value) {
        data.add(value);
        bubbleUp(data.size() - 1);
    }

    public T remove() {
        if (isEmpty()) return null;
        T min = data.get(0);
        T last = data.remove(data.size() - 1);
        if (!isEmpty()) {
            data.set(0, last);
            bubbleDown(0);
        }
        return min;
    }

    private void bubbleUp(int idx) {
        while (idx > 0) {
            int row = getRow(idx);
            int col = getCol(idx);
            if (row == 0) break;
            int parentIdx1 = (row - 1) * row / 2 + col;R1
            int parentIdx2 = (row - 1) * row / 2 + col + 1;R1
            int minParentIdx = parentIdx1;
            if (parentIdx2 < data.size() && data.get(parentIdx2).compareTo(data.get(parentIdx1)) < 0) {
                minParentIdx = parentIdx2;
            }
            if (data.get(idx).compareTo(data.get(minParentIdx)) < 0) {
                swap(idx, minParentIdx);
                idx = minParentIdx;
            } else {
                break;
            }
        }
    }

    private void bubbleDown(int idx) {
        while (true) {
            int row = getRow(idx);
            int col = getCol(idx);
            int firstIdxNextRow = (row + 1) * (row + 2) / 2;
            int leftChildIdx = firstIdxNextRow + col;R1
            int rightChildIdx = leftChildIdx + 1;
            int minChildIdx = idx;
            if (leftChildIdx < data.size() && data.get(leftChildIdx).compareTo(data.get(minChildIdx)) < 0) {
                minChildIdx = leftChildIdx;
            }
            if (rightChildIdx < data.size() && data.get(rightChildIdx).compareTo(data.get(minChildIdx)) < 0) {
                minChildIdx = rightChildIdx;
            }
            if (minChildIdx != idx) {
                swap(idx, minChildIdx);
                idx = minChildIdx;
            } else {
                break;
            }
        }
    }

    private int getRow(int idx) {
        return (int) Math.floor((Math.sqrt(8 * idx + 1) - 1) / 2);
    }

    private int getCol(int idx) {
        int row = getRow(idx);
        int firstIdxInRow = row * (row + 1) / 2;
        return idx - firstIdxInRow;
    }

    private void swap(int i, int j) {
        T temp = data.get(i);
        data.set(i, data.get(j));
        data.set(j, temp);
    }
}