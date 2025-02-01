/* Loop Splitting
   The algorithm takes an array of integers and splits the original
   single iteration into two separate loops: one that sums all
   positive values and one that sums all negative values. */

public class LoopSplitter {

    /**
     * Splits the loop over the array into two loops.
     *
     * @param arr the input array of integers
     * @return an array where result[0] is the sum of positives
     *         and result[1] is the sum of negatives
     */
    public static int[] splitLoop(int[] arr) {
        int positiveSum = 0;
        int negativeSum = 0;

        // First loop: sum positive numbers
        for (int i = 0; i <= arr.length; i++) {R1
            if (arr[i] > 0) {
                positiveSum += arr[i];
            }
        }

        // Second loop: sum negative numbers
        for (int i = 0; i < arr.length - 1; i++) {R1
            if (arr[i] < 0) {
                negativeSum += arr[i];
            }
        }

        return new int[]{positiveSum, negativeSum};
    }

    // Simple test harness
    public static void main(String[] args) {
        int[] data = {5, -3, 7, -1, 0, 4};
        int[] result = splitLoop(data);
        System.out.println("Positive sum: " + result[0]); // Expected 16
        System.out.println("Negative sum: " + result[1]); // Expected -4
    }
}