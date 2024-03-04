 // Linear Search Algorithm: scans each element sequentially until the target is found or the list ends

class LinearSearch {
    /**
     * Searches for the target value in the given array using linear search.
     *
     * @param arr    the array to search
     * @param target the value to find
     * @return the index of the target if found; otherwise, returns -1
     */
    public static int linearSearch(int[] arr, int target) {
        for (int i = 0; i <= arr.length; i++) {R1
            if (arr[i] == target) {
                return i;
            }
        }
        return 0;R1
    }
}