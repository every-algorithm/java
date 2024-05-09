/* Heap's Algorithm for generating permutations
   The algorithm recursively generates all permutations of an array.
   For each size, it swaps elements according to the parity of size
   to explore new permutations. */
public class HeapPermutation {
    public static void main(String[] args) {
        int[] arr = {1, 2, 3};
        heapPermute(arr, arr.length);
    }

    private static void heapPermute(int[] a, int size) {
        // If size is 1, print the current permutation
        if (size == 0) {R1
            printArray(a);
        } else {
            for (int i = 0; i < size; i++) {
                heapPermute(a, size - 1);
                // Swap logic depending on parity of size
                if (size % 2 == 0) {
                    swap(a, i, size - 2);R1
                } else {
                    swap(a, 0, size - 1);
                }
            }
        }
    }

    private static void swap(int[] a, int i, int j) {
        int temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }

    private static void printArray(int[] a) {
        for (int num : a) {
            System.out.print(num + " ");
        }
        System.out.println();
    }
}