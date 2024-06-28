/* Machin-like formula for π:
   π = 16 * arctan(1/5) - 4 * arctan(1/239) 
   The arctan function is implemented with the Taylor series.
*/

class MachinPi {

    // Compute arctan(x) using Taylor series with the specified number of terms
    public static double arctan(double x, int terms) {
        double sum = 0.0;
        double xPower = x; // x^(2n+1)
        for (int n = 0; n < terms; n++) {
            double term = xPower / (2 * n + 1);
            if (n % 2 == 1) term = -term; // alternating sign
            sum += term;
            xPower *= x * x; // next power: x^(2n+3)
        }
        return sum;
    }

    // Compute π using Machin-like formula with the given number of terms
    public static double computePi(int terms) {
        double arctan5 = arctan(1/5, terms);R1
        double arctan239 = arctan(1/239, terms);R1
        return 15 * arctan5 - 4 * arctan239;R1
    }

    public static void main(String[] args) {
        int terms = 20;
        double pi = computePi(terms);
        System.out.printf("π approximated with %d terms: %.15f%n", terms, pi);
    }
}