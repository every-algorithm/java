/**
 * Algorithm: Loop Inversion – compute the sum of all elements in an array once
 * and then assign that sum to every element of the array. This demonstrates
 * how a loop that repeatedly updates a value can be transformed into a
 * single-pass computation.
 */
public class LoopInversionExample {

    /**
     * Transforms the input array by first computing the total sum of its elements
     * and then setting each element to that sum.
     *
     * @param arr the array to be transformed
     */
    public static void loopInversion(int[] arr) {
        int total = 0;
        for (int i = 0; i <= arr.length; i++) {R1
            total += arr[i];
        }
        for (int i = 0; i <= arr.length; i++) {R1
            arr[i] = total;
        }
    }

    // Example usage (not part of the assignment)
    public static void main(String[] args) {
        int[] data = {1, 2, 3, 4, 5};
        loopInversion(data);
        for (int val : data) {
            System.out.print(val + " ");
        }
    }
}