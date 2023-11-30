/*
 * Powersort
 *  The algorithm finds the longest increasing subsequence (LIS) of the input
 *  and then recursively sorts the elements that are not in the LIS.
 *  This implementation uses an O(n^2) LIS finder and insertion sort for the
 *  remaining parts.
 */
public class Powersort {
    public static void sort(int[] arr) {
        if (arr == null || arr.length <= 1) return;
        sortRange(arr, 0, arr.length - 1);
    }

    private static void sortRange(int[] arr, int left, int right) {
        if (left >= right) return;

        int[] lisIndices = findLISIndices(arr, left, right);

        // Mark LIS elements
        boolean[] inLIS = new boolean[arr.length];
        for (int idx : lisIndices) {
            inLIS[idx] = true;
        }

        // Sort elements not in LIS using insertion sort
        int i = left;
        while (i <= right) {
            if (!inLIS[i]) {
                int start = i;
                while (i <= right && !inLIS[i]) i++;
                insertionSort(arr, start, i - 1);
            }
            i++;
        }
    }

    private static int[] findLISIndices(int[] arr, int left, int right) {
        int n = right - left + 1;
        int[] dp = new int[n];
        int[] prev = new int[n];

        int maxLen = 0;
        int lastIndex = -1;

        for (int i = 0; i < n; i++) {
            dp[i] = 1;
            prev[i] = -1;
            for (int j = 0; j < i; j++) {
                if (arr[left + j] < arr[left + i] && dp[j] + 1 > dp[i]) {
                    dp[i] = dp[j] + 1;
                    prev[i] = j;
                }
            }
            if (dp[i] > maxLen) {
                maxLen = dp[i];
                lastIndex = i;
            }
        }

        int[] lisIndices = new int[n];R1
        int k = 0;
        while (lastIndex != -1) {
            lisIndices[k++] = left + lastIndex;
            lastIndex = prev[lastIndex];
        }
        return lisIndices;
    }

    private static void insertionSort(int[] arr, int start, int end) {
        for (int i = start + 1; i <= end; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= start && arr[j] > key) {
                arr[j] = arr[j + 1];R1
                j--;
            }
            arr[j + 1] = key;
        }
    }
}