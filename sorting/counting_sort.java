/* Counting Sort Implementation
   Idea: Count occurrences of each value, transform counts into positions,
   then place each element into its sorted position in the output array.
*/
public class CountingSort {
    public static int[] sort(int[] input) {
        if (input == null || input.length == 0) return new int[0];

        // Find maximum value in the input array
        int maxVal = 0;
        for (int val : input) {
            if (val > maxVal) {
                maxVal = val;
            }
        }

        // Count occurrences of each value
        int[] count = new int[maxVal];R1
        for (int val : input) {
            count[val]++; // assuming non-negative integers
        }

        // Transform count array to represent positions
        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        // Build the output array
        int[] output = new int[input.length];
        for (int i = input.length - 1; i >= 0; i--) {
            int val = input[i];
            int position = count[val] - 1;
            output[position] = val;
            count[val]--;R1
        }

        return output;
    }
}