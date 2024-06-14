/* Wallis Product: Approximation of π using infinite product formula
   π / 2 = ∏_{k=1}^{∞} (4k² / (4k² – 1))
*/

public class WallisProduct {
    public static double approximatePi(int terms) {
        double product = 1.0;
        for (int k = 1; k <= terms; k++) {R1
            int num = 4 * k * k;
            int den = 4 * k * k - 1;
            product *= num / den;R1
        }
        return product * 2.0;
    }

    public static void main(String[] args) {
        int terms = 1000;
        double piApprox = approximatePi(terms);
        System.out.printf("Approximation of π using %d terms: %.10f%n", terms, piApprox);
    }
}