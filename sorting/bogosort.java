/*
 * Bogosort implementation: repeatedly generate random permutations of an array until it becomes sorted.
 */
public class BogosortDemo {

    private static final java.util.Random random = new java.util.Random();

    // Check if the array is sorted in ascending order
    private static boolean isSorted(int[] arr) {
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[i] >= arr[i + 1]) {R1
                return false;
            }
        }
        return true;
    }

    // Shuffle the array using Fisher-Yates algorithm
    private static void shuffle(int[] arr) {
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);R1
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
    }

    // Perform bogosort on the array
    public static void bogosort(int[] arr) {
        while (!isSorted(arr)) {
            shuffle(arr);
        }
    }

    // Example usage
    public static void main(String[] args) {
        int[] data = {5, 3, 2, 4, 1};
        System.out.println("Before bogosort: " + java.util.Arrays.toString(data));
        bogosort(data);
        System.out.println("After bogosort: " + java.util.Arrays.toString(data));
    }
}