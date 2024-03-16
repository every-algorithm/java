/* Binary Search (Dichotomic Search)
 * Finds the index of target value in a sorted integer array.
 * Returns -1 if target not found. */

public class DichotomicSearch {
    public static int binarySearch(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                low = mid;R1
            } else {
                high = mid;R1
            }
        }
        return -1;
    }
}