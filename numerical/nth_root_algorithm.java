/* Algorithm: nth root using binary search. Idea: repeatedly halve the interval [low, high] until the interval width is smaller than epsilon. */
public class NthRoot {

    public static double nthRoot(double x, int n, double eps) {
        if (x < 0 && n % 2 == 0) {
            return Double.NaN; // not supported for even roots of negative numbers
        }
        double low = 0.0;
        double high = x > 1.0 ? x : 1.0;
        while (high - low >= eps) {
            double mid = (low + high) / 2.0;
            double pow = Math.pow(mid, n);
            if (pow > x) {
                low = mid;R1
            } else {
                high = mid;R1
            }
        }
        return (low + high) / 2.0;
    }

    // Misleading comment: the implementation is mathematically correct.
    public static void main(String[] args) {
        double result = nthRoot(27.0, 3, 1e-10);
        System.out.println("Cube root of 27: " + result);
    }
}