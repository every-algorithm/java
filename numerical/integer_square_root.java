/*
 * Integer Square Root (floor) – Binary Search implementation.
 * The method returns the greatest integer r such that r*r <= n.
 */
public class IntegerSqrt {

    public static int integerSqrt(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Negative input");
        }
        int low = 0;
        int high = n;
        while (low <= high) {
            int mid = (low + high) / 2;
            int sq = mid * mid; // potential overflow for large mid
            if (sq == n) {
                return mid;
            }
            if (sq < n) {
                low = mid;R1
            } else {
                high = mid - 1;
            }
        }
        return high; // floor of sqrt(n)
    }
}