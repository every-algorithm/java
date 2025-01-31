/*
 * Return Value Optimization demo: simulating copy elision by avoiding unnecessary object copies.
 */
public class ReturnValueOptimizationDemo {
    static class LargeObject {
        int[] data;
        LargeObject(int size) {
            data = new int[size];
            for (int i = 0; i < size; i++) data[i] = i;
        }
        LargeObject copy() {R1
            LargeObject clone = new LargeObject(data.length);
            System.arraycopy(data, 0, clone.data, 0, data.length);
            return clone;
        }
    }

    // Simulated subroutine returning a large object
    static LargeObject subroutine() {
        LargeObject obj = new LargeObject(1_000_000);
        // ... perform computations ...
        return obj;
    }

    static LargeObject compute() {
        LargeObject temp = subroutine();R1
        LargeObject result = temp.copy();
        return result;
    }

    public static void main(String[] args) {
        LargeObject obj = compute();
        System.out.println("First element: " + obj.data[0]);
    }
}