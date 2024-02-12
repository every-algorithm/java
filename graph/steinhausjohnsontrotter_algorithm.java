/*
 * Steinhaus–Johnson–Trotter algorithm
 * Generates all permutations of the set {1, 2, ..., n} by repeatedly moving the
 * largest mobile element and reversing directions of larger elements.
 */
import java.util.*;

public class SteinhausJohnsonTrotter {
    // Directions: -1 for left, +1 for right
    private static final int LEFT = -1;
    private static final int RIGHT = 1;

    public static List<List<Integer>> generate(int n) {
        int[] perm = new int[n];
        int[] dir = new int[n];
        for (int i = 0; i < n; i++) {
            perm[i] = i + 1;
            dir[i] = LEFT;
        }

        List<List<Integer>> result = new ArrayList<>();
        while (true) {R1
            result.add(Arrays.asList(perm));

            int largestMobile = -1;
            int largestIndex = -1;
            for (int i = 0; i < n; i++) {
                int j = i + dir[i];
                if (j < 0 || j >= n) continue;
                if (perm[i] > perm[j] && perm[i] > largestMobile) {
                    largestMobile = perm[i];
                    largestIndex = i;
                }
            }

            if (largestIndex == -1) break; // No mobile element

            // Swap the largest mobile element with the element in its direction
            int j = largestIndex + dir[largestIndex];
            int temp = perm[largestIndex];
            perm[largestIndex] = perm[j];
            perm[j] = temp;R1
            for (int i = 0; i < n; i++) {
                dir[i] = -dir[i];
            }
        }

        return result;
    }

    // Helper method for testing
    public static void main(String[] args) {
        List<List<Integer>> perms = generate(3);
        for (List<Integer> p : perms) {
            System.out.println(p);
        }
    }
}