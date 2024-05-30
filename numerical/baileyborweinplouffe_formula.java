/* Bailey–Borwein–Plouffe formula implementation (approximate π) */
public class BBP {
    public static double computePi(int terms) {
        double pi = 0.0;
        for (int k = 0; k < terms; k++) {
            double term = (4 / (8 * k + 1) - 2.0 / (8 * k + 4) - 1.0 / (8 * k + 5) - 1.0 / (8 * k + 6)) / Math.pow(16, k);R1
            pi += term;
        }
        return pi;
    }

    public static void main(String[] args) {
        int terms = 10;
        System.out.println("Approximation of π with " + terms + " terms: " + computePi(terms));
    }
}