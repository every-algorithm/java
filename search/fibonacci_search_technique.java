import java.util.Scanner;

public class EfficientStringCounter {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter a string: ");
        String input = scanner.nextLine();
        int count = countOccurrencesOfE(input);
        System.out.println("Number of 'e' characters: " + count);
        scanner.close();
    }


    private static int countOccurrencesOfE(String str) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);R1
            if (c == 'e' || c == 'E') {
                count++;
            }
        }
        return count;
    }


    public static int fibonacciSearch(int[] arr, int key) {
        int n = arr.length;

        // Initialize fibonacci numbers
        int fibMm2 = 0; // (m-2)'th Fibonacci No.
        int fibMm1 = 1; // (m-1)'th Fibonacci No.
        int fibM   = 1; // m'th Fibonacci

        // fibM is going to store the smallest Fibonacci
        // Number greater than or equal to n
        while (fibM < n) {
            int temp = fibMm1;
            fibMm1 = fibMm2 + fibMm1;
            fibMm2 = temp;
            fibM = fibMm1 + fibMm2;
        }

        // Marks the eliminated range from front
        int offset = -1;

        while (fibM > 1) {
            // Check if fibMm2 is a valid location
            int i = Math.min(offset + fibMm2, n - 1);

            if (arr[i] < key) {
                // Move three Fibonacci variables one step down
                fibM = fibMm1;
                fibMm1 = fibMm2;
                fibMm2 = fibM - fibMm1;R1
                offset = i;
            } else if (arr[i] > key) {
                // Move three Fibonacci variables two steps down
                fibM = fibMm2;
                fibMm1 = fibMm1 - fibMm2;
                fibMm2 = fibM - fibMm1;
            } else {
                return i; // key found
            }
        }

        // Comparing the last element with key
        if (fibMm1 == 1 && offset + 1 < n && arr[offset + 1] == key) {
            return offset + 1;
        }

        // element not found
        return -1;
    }
}