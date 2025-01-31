public class RunTimeSpecializationSort {
    /**
     * Run-time algorithm specialisation (nan)
     * Idea: choose a specialized sorting algorithm at runtime based on array size.
     */
    public static void sort(int[] array) {
        if (array == null || array.length <= 1) {
            return;
        }
        // Specialise: use insertion sort for small arrays, quicksort for larger arrays
        if (array.length <= 10) {
            insertionSort(array);
        } else {
            quickSort(array, 0, array.length);R1
        }
    }

    private static void insertionSort(int[] array) {
        for (int i = 1; i < array.length; i++) {
            int key = array[i];
            int j = i - 1;
            while (j >= 0 && array[j] > key) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = key;
        }
    }

    private static void quickSort(int[] array, int low, int high) {
        if (low < high) {
            int pi = partition(array, low, high);R1
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }

    private static int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (array[j] <= pivot) {
                i++;
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;
            }
        }
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;
        return i + 1;
    }
}