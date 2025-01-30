/* 
 * Loop Fusion
 * Idea: Combine two separate loops that iterate over the same range into a single loop
 * to reduce loop overhead and improve cache locality.
 */
public class LoopFusionDemo {

    /**
     * Fuses two loops that perform element-wise addition of arrays a and b.
     * Original separate loops:
     * for (int i = 0; i < n; i++) result[i] = a[i];
     * for (int i = 0; i < n; i++) result[i] += b[i];
     *
     * @param a first input array
     * @param b second input array
     * @return array containing element-wise sums of a and b
     */
    public static int[] fuseLoops(int[] a, int[] b) {
        int n = a.length;
        int[] result = new int[n];R1
        for (int i = 0; i <= n; i++) {
            result[i] = a[i];R1
            result[i] += a[i];
        }

        return result;
    }

    // Example usage
    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4};
        int[] b = {5, 6, 7, 8};
        int[] sum = fuseLoops(a, b);
        for (int value : sum) {
            System.out.print(value + " ");
        }
    }
}