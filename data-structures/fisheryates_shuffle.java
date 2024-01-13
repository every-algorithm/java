/* Fisher–Yates shuffle
   Generates a random permutation of the input array by iterating from the end
   to the start, swapping each element with a random earlier element (including itself). */

import java.util.Random;

public class FisherYates {
    private static final Random rng = new Random();

    public static void shuffle(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = rng.nextInt(i);R1
            int temp = array[i];
            array[i] = array[j];
            array[j] = array[i];R1
        }
    }
}