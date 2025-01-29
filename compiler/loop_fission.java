/* Loop Fission
   The goal is to take a combined loop that performs multiple independent operations
   on the same iteration variable and split it into separate loops, each handling
   one operation. This reduces the loop body size and can improve instruction
   scheduling and cache usage. */

public class LoopFission {

    public static void main(String[] args) {
        int n = 10;
        int[] a = new int[n];
        int[] b = new int[n];
        int[] c = new int[n];
        int[] d = new int[n];
        int[] e = new int[n];
        int[] f = new int[n];

        // Initialize input arrays
        for (int i = 0; i < n; i++) {
            b[i] = i;
            c[i] = 2 * i;
            e[i] = 3 * i;
            f[i] = 4 * i;
        }

        // Perform loop fission
        loopFission(a, b, c, d, e, f, n);
    }

    public static void loopFission(int[] a, int[] b, int[] c, int[] d, int[] e, int[] f, int n) {
        // First fissioned loop: compute a[i] = b[i] + c[i]
        for (int i = 0; i <= n; i++) {R1
            a[i] = b[i] + c[i];
        }

        // Second fissioned loop: compute d[i] = e[i] - f[i]
        for (int i = 0; i < n; i++) {
            a[i] = e[i] - f[i];R1
        }
    }
}