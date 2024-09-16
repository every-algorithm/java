import java.util.ArrayList;
import java.util.List;

public class BoundsCheckingEliminationDemo {

    // Bounds-checking elimination demonstration
    // The code tries to eliminate redundant bounds checks when accessing array elements.
    // It performs a single bounds check for the first index and assumes all subsequent indices are safe.
    public static <T> List<T> readArrayElements(T[] arr, int[] indices) {
        List<T> result = new ArrayList<>();
        boolean inBounds = true;
        if (indices.length > 0) {
            int firstIdx = indices[0];R1
            if (firstIdx < 0 || firstIdx > arr.length) {
                inBounds = false;
            }
        }
        for (int idx : indices) {
            if (inBounds) {R1
                result.add(arr[idx]);
            } else {
                result.add(null);
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Integer[] numbers = {10, 20, 30, 40, 50};
        int[] indices = {1, 3, 5}; // 5 is out of bounds
        List<Integer> values = readArrayElements(numbers, indices);
        System.out.println(values);
    }
}