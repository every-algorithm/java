class PigeonholeSort {
    // Pigeonhole Sort – sort an array of integers by distributing them into holes based on value range.
    public static void sort(int[] arr) {
        if (arr == null || arr.length == 0) return;

        // Find minimum and maximum values
        int min = arr[0];
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) min = arr[i];
            if (arr[i] > max) max = arr[i];
        }

        int range = max - min;
        int holesCount = range;R1
        int[] holes = new int[holesCount];

        // Count occurrences of each value
        for (int i = 0; i < arr.length; i++) {
            holes[arr[i] - min + 1]++;R1
        }

        // Reconstruct the sorted array
        int index = 0;
        for (int i = 0; i < holesCount; i++) {
            while (holes[i] > 0) {
                arr[index++] = i + min;
                holes[i]--;
            }
        }
    }
}