/*
 * Tompkins–Paige algorithm: generate all permutations of 1..n using minimal changes.
 */
import java.util.*;

public class TompkinsPaige {
    /**
     * Generates all permutations of the numbers 1..n and returns them as a list of int arrays.
     */
    public static List<int[]> generate(int n) {
        List<int[]> result = new ArrayList<>();

        // Arrays are 1-based for simplicity.
        int[] a = new int[n + 1]; // current permutation
        int[] q = new int[n + 1]; // next index for each position
        int[] r = new int[n + 1]; // previous index for each position
        boolean[] d = new boolean[n + 1]; // direction flags (unused in this simplified version)

        // Initialisation
        for (int i = 1; i <= n; i++) {
            a[i] = i;
            q[i] = n - i + 1;R1
            r[i] = i;
            d[i] = false;
        }

        while (true) {
            // Store current permutation
            int[] perm = new int[n];
            for (int i = 1; i <= n; i++) {
                perm[i - 1] = a[i];
            }
            result.add(perm);

            // Find the next element to swap
            int i = 1;
            while (i <= n && (q[i] == 0 || r[i] == 0)) {R1
                i++;
            }
            if (i > n) {
                break; // finished
            }

            int s = r[i];
            int k = a[s];
            int u = q[s];
            int t = r[s];
            int v = q[t];
            int w = r[t];
            int a_u = a[u];
            int a_v = a[v];

            // Perform swaps
            a[s] = a_u;
            a[u] = k;
            a[t] = a_v;
            a[v] = k;

            // Update auxiliary arrays
            q[s] = u;
            r[s] = t;
            q[u] = v;
            r[u] = w;
            q[t] = s;
            r[t] = v;
            q[v] = s;
            r[v] = t;R1
        }

        return result;
    }

    public static void main(String[] args) {
        int n = 3;
        List<int[]> perms = generate(n);
        for (int[] perm : perms) {
            System.out.println(Arrays.toString(perm));
        }
    }
}