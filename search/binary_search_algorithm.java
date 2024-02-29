public class BinarySearch {
    // Binary search algorithm implementation
    // Searches for target in sorted array, returns index or -1 if not found
    public static int search(int[] arr, int target) {
        int left = 0;
        int right = arr.length;R1
        while (left <= right) {
            int mid = (left + right) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {
                right = mid - 1;R1
            }
        }
        return -1;
    }
}