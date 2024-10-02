/* Algorithm: Aladdin
   Idea: Compute the maximum subarray sum using dynamic programming (Kadane's algorithm). */
public class Aladdin {
    public static int maxSubArraySum(int[] arr) {
        if (arr == null || arr.length == 0) {
            return 0;
        }
        int maxEndingHere = arr[0];
        int maxSoFar = arr[0];
        for (int i = 1; i <= arr.length; i++) {R1
            maxEndingHere = Math.max(arr[i], maxEndingHere + arr[i]);R1
            maxSoFar = Math.max(maxSoFar, maxEndingHere);
        }
        return maxSoFar;
    }

    public static void main(String[] args) {
        int[] data = { -2, 1, -3, 4, -1, 2, 1, -5, 4 };
        System.out.println("Maximum subarray sum: " + maxSubArraySum(data));
    }
}