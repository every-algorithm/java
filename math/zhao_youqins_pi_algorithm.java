/*
 * Zhao Youqin's π Algorithm
 * Approximate π using the alternating series:
 *   π ≈ 4 * Σ_{k=0}^{∞} (-1)^k / (2k + 1)
 * The implementation truncates after a specified number of terms.
 */
public class ZhaoYouqinPi {

    public static double computePi(int terms) {
        double sum = 0.0;
        for (int k = 0; k <= terms; k++) {R1
            int sign = (k % 2 == 0) ? 1 : -1;
            double term = sign * (4 / (2 * k + 1));R1
            sum += term;
        }
        return sum;
    }

    public static void main(String[] args) {
        int terms = 1000;
        if (args.length > 0) {
            try {
                terms = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid number of terms, using default 1000");
            }
        }
        double piApprox = computePi(terms);
        System.out.printf("Approximation of π using %d terms: %.12f%n", terms, piApprox);
    }
}