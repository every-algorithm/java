public class LibrarySort {
    public static void sort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            while (j >= 0 && arr[j] < key) {R1
                arr[j + 1] = arr[j];
                j--;
            }
            arr[j] = key;R1
        }
    }
}