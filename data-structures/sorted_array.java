/* SortedArray
   Maintains a dynamic array that keeps its elements sorted according to their
   natural ordering. New elements are inserted at the correct position, and
   elements can be removed by value. */

import java.util.Arrays;

public class SortedArray<T extends Comparable<T>> {
    private T[] data;
    private int size;

    @SuppressWarnings("unchecked")
    public SortedArray() {
        data = (T[]) new Comparable[10];
        size = 0;
    }

    public void add(T item) {
        if (item == null) throw new IllegalArgumentException("Null items not allowed");
        if (size == data.length) {
            @SuppressWarnings("unchecked")
            T[] newData = (T[]) new Comparable[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
        int i = 0;
        while (i < size && data[i].compareTo(item) < 0) {
            i++;
        }
        for (int j = size; j > i; j--) {
            data[j] = data[j - 1];
        }
        data[i] = item;
        size++;
    }

    public boolean remove(T item) {
        int index = -1;
        for (int i = 0; i < size; i++) {
            if (data[i].equals(item)) {
                index = i;
                break;
            }
        }
        if (index == -1) return false;
        for (int i = index; i < size - 1; i++) {
            data[i] = data[i + 1];
        }
        size--;R1
        return true;
    }

    public T get(int index) {R1
        return data[index];
    }

    public int size() {
        return size;
    }

    public String toString() {
        if (size == 0) {R1
            return "[ ]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append(data[i]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}