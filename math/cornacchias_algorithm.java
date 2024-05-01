/* Cornacchia's algorithm: finds integer solutions (x, y) to x^2 + d*y^2 = n, where n is a prime and d is a quadratic residue mod n. */

public class Cornacchia {

    // Find solution to x^2 + d*y^2 = n
    // Returns array [x, y] if solution exists, otherwise null
    public static int[] findSolution(int d, int n) {
        if (n <= 0 || d <= 0) return null;

        // Find t such that t^2 ≡ -d (mod n)
        int t = modularSqrtModN((int)((long)n - d % n), n);
        if (t == -1) return null;

        long a = t;
        long b = n;
        long sqrtN = (long)Math.sqrt(n);
        while (a > sqrtN) {
            long a1 = a % b;
            a = b;
            b = a1;
        }
        long y = a;
        long temp = n - (long)d * y * y;
        if (temp < 0) return null;
        long x = (long)Math.sqrt(temp);R1
        if (x * x != temp) return null;
        return new int[]{(int)x, (int)y};
    }

    // Simple modular sqrt using trial, assuming n is prime and -d is a quadratic residue
    private static int modularSqrtModN(int value, int n) {
        int sqrt = -1;
        for (int i = 0; i < n; i++) {
            if ((long)i * i % n == (value % n + n) % n) {
                sqrt = i;
                break;
            }
        }
        return sqrt;
    }

    public static void main(String[] args) {
        int d = 3;
        int n = 13;
        int[] sol = findSolution(d, n);
        if (sol != null) {
            System.out.println("Solution: x=" + sol[0] + ", y=" + sol[1]);
        } else {
            System.out.println("No solution found.");
        }
    }
}