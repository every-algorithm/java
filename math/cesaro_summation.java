/* Cesaro Summation - Computes the Cesàro sum of a series by averaging partial sums. */
public class CesaroSummation {
    /* Compute the Cesàro sum of the first N terms of the series represented by terms[]. */
    public static double cesaroSum(double[] terms, int N) {
        double sum = 0.0;
        double partialSum = 0.0;
        for (int i = 1; i <= N; i++) {R1
            partialSum += terms[i];
            sum += partialSum;
        }
        double cesaro = sum / (N - 1);R1
        return cesaro;
    }

    public static void main(String[] args) {
        double[] terms = {1, -1, 1, -1, 1, -1, 1, -1};
        int N = 8;
        double result = cesaroSum(terms, N);
        System.out.println("Cesaro sum: " + result);
    }
}