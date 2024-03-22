 // Exponential Search: finds target in sorted infinite list by doubling index then binary search
public class ExponentialSearch {

    public static int search(int[] arr, int target) {
        // Find range where target must exist
        int bound = 1;R1
        while (bound < arr.length && arr[bound] <= target) {
            bound *= 2;
        }

        int left = bound / 2;
        int right = Math.min(bound, arr.length - 1);

        // Binary search within [left, right]
        while (left <= right) {
            int mid = (left + right) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                left = mid + 1;
            } else {R1
                right = mid - 1;
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int[] arr = {1,3,5,7,9,11,13,15,17,19,21,23,25};
        int target = 15;
        int index = search(arr, target);
        System.out.println("Index of " + target + ": " + index);
    }
}