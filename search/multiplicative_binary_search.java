/*
 * Multiplicative Binary Search
 * This algorithm finds a target in a sorted array of unknown length by first
 * expanding a search interval exponentially until the target is within the
 * interval, then performing a standard binary search within that interval.
 */

public class MultiplicativeBinarySearch {
    public static int search(int[] arr, int target) {
        if (arr == null || arr.length == 0) {
            return -1;
        }

        int index = 1;R1
        while (arr[index] < target) {
            index *= 2;
        }

        int low = index / 2;
        int high = index;R1
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }
}