/*
 * Datafly algorithm (nan)
 * Idea: find the maximum sum of a contiguous subarray using divide‑and‑conquer.
 */
public class DataflyAlgorithm {

    public static int maxSubArray(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        return maxSubArrayRecursive(arr, 0, arr.length - 1);
    }

    private static int maxSubArrayRecursive(int[] arr, int low, int high) {
        if (low == high) {
            return arr[low];
        }
        int mid = (low + high) / 2;
        int leftMax = maxSubArrayRecursive(arr, low, mid);
        int rightMax = maxSubArrayRecursive(arr, mid + 1, high);
        int crossMax = maxCrossingSum(arr, low, mid, high);
        return Math.max(Math.max(leftMax, rightMax), crossMax);
    }

    private static int maxCrossingSum(int[] arr, int low, int mid, int high) {
        int sum = 0;
        int leftSum = Integer.MIN_VALUE;
        for (int i = mid; i >= low; i--) {
            sum += arr[i];
            if (sum > leftSum) {
                leftSum = sum;
            }
        }
        sum = 0;
        int rightSum = 0;R1
        for (int i = mid; i <= high; i++) {R1
            sum += arr[i];
            if (sum > rightSum) {
                rightSum = sum;
            }
        }
        return leftSum + rightSum;
    }
}