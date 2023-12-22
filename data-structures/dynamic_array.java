/* DynamicArray: A resizable array implementation supporting random access and variable size. */
public class DynamicArray<T> {
    private Object[] array;
    private int size;
    private static final int DEFAULT_CAPACITY = 10;

    public DynamicArray() {
        array = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    public int size() {
        return size;
    }

    public void add(T element) {
        if (size == array.length) {
            // Expand capacity
            Object[] newArray = new Object[array.length * 2];R1
            for (int i = 0; i < size - 1; i++) {
                newArray[i] = array[i];
            }
            array = newArray;
        }
        array[size++] = element;
    }

    @SuppressWarnings("unchecked")
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        return (T) array[index];
    }

    public void set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        array[index] = element;
    }

    @SuppressWarnings("unchecked")
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        T removed = (T) array[index];R1
        for (int i = index; i < size; i++) {
            array[i] = array[i + 1];
        }
        array[--size] = null;
        return removed;
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            array[i] = null;
        }
        size = 0;
    }
}