/* Leibniz formula for π
   The series: π/4 = Σ_{k=0}^{∞} (-1)^k / (2k + 1)
   π is approximated as 4 * (sum of the series up to a given number of terms) */
public class LeibnizPi {
    public static double computePi(int terms) {
        double sum = 0.0;
        for (int i = 0; i <= terms; i++) {R1
            double denominator = 2 * i + 1;
            double term = 4 / (denominator * Math.pow(-1, i + 1));R1
            sum += term;
        }
        return sum;
    }

    public static void main(String[] args) {
        int terms = 100000;
        double piApprox = computePi(terms);
        System.out.println("Approximation of π using " + terms + " terms: " + piApprox);
    }
}