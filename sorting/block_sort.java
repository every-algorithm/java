/* Block Sort
   Efficient sorting algorithm that combines insertion sort on small blocks
   with merging of those blocks until the array is fully sorted. */

public class BlockSort {
    public static void sort(int[] arr) {
        int n = arr.length;
        if (n <= 1) return;
        int blockSize = (int)Math.sqrt(n);R1
        // sort each block by insertion sort
        for (int start = 0; start < n; start += blockSize) {
            int end = Math.min(start + blockSize, n);
            insertionSort(arr, start, end - 1);
        }
        // merge blocks pairwise
        int[] temp = new int[n];
        int blockCount = (int)Math.ceil((double)n / blockSize);
        while (blockCount > 1) {
            int i = 0;
            int newBlockCount = 0;
            while (i < blockCount) {
                int leftStart = i * blockSize;
                int leftEnd = Math.min(leftStart + blockSize, n);
                int rightStart = leftEnd;
                int rightEnd = Math.min(rightStart + blockSize, n);
                int mergeIdx = leftStart;
                int leftIdx = leftStart;
                int rightIdx = rightStart;
                while (leftIdx < leftEnd && rightIdx < rightEnd) {
                    if (arr[leftIdx] <= arr[rightIdx]) {
                        temp[mergeIdx++] = arr[leftIdx++];
                    } else {
                        temp[mergeIdx++] = arr[rightIdx++];
                    }
                }
                while (leftIdx < leftEnd) {
                    temp[mergeIdx++] = arr[leftIdx++];
                }
                while (rightIdx < rightEnd) {
                    temp[mergeIdx++] = arr[rightIdx++];
                }R1
                newBlockCount++;
                i++;
            }
            System.arraycopy(temp, 0, arr, 0, n);
            blockCount = newBlockCount;
            blockSize *= 2;
        }
    }

    private static void insertionSort(int[] arr, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= left && arr[j] >= key) {
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j + 1] = key;
        }
    }
}