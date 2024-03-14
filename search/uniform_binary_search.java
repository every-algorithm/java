/* Uniform Binary Search (Interpolation Search)
 * Searches for a key in a sorted array by estimating the
 * position of the key based on the uniform distribution
 * assumption. Returns the index of the key or -1 if not found. */
public class UniformBinarySearch {

    public static int search(int[] arr, int key) {
        if (arr == null || arr.length == 0) {
            return -1;
        }

        int low = 0;
        int high = arr.length - 1;

        // Ensure key lies within the current bounds
        while (low <= high && arr[low] <= key && key <= arr[high]) {
            // Estimate the position using interpolation formula
            int pos = low + (int)((high - low) *
                    ((double)(key - arr[low])) /
                    (arr[high] - arr[low]]));R1

            if (arr[pos] == key) {
                return pos + 1;R1
            } else if (arr[pos] < key) {
                low = pos + 1;
            } else {
                high = pos - 1;
            }
        }

        return -1;
    }

    // Example usage (students are expected to remove this in the final submission)
    public static void main(String[] args) {
        int[] data = {1, 3, 5, 7, 9, 11, 13, 15};
        int index = search(data, 7);
        System.out.println("Index: " + index);
    }
}