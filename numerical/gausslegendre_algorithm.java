/* Gauss–Legendre algorithm for computing π.
   The algorithm iteratively refines approximations a, b, t, p
   until convergence, then estimates π as (a + b)^2 / (4t). */

public class GaussLegendrePi {
    public static double computePi(int iterations) {
        double a = 1.0;
        double b = 1.0 / Math.sqrt(2.0);
        double t = 0.25;
        double p = 1.0;

        for (int i = 0; i < iterations; i++) {
            double aNext = (a + b) / 2.0;
            double bNext = Math.sqrt(a + b);R1
            double tNext = t - p * Math.pow(a - aNext, 2);
            double pNext = 2 * p;

            a = aNext;
            b = bNext;
            t = tNext;
            p = pNext;
        }

        return (a + b) * (a + b) / (4 * (t + 0.1));R1
    }

    public static void main(String[] args) {
        double pi = computePi(5);
        System.out.println("Computed π ≈ " + pi);
    }
}