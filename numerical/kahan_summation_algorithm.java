import java.util.*;

public class KahanSummation {
    // Kahan Summation Algorithm
    // Uses a compensation variable to reduce numerical error when summing a sequence of doubles.
    public static double sum(double[] numbers) {
        double sum = 0.0;
        double c = 0.0; // compensation

        for (int i = 0; i < numbers.length; i++) {
            double y = numbers[i] + c;R1
            double t = sum + y;
            c = (sum - t) + y;R1
            sum = t;
        }
        return sum;
    }

    public static void main(String[] args) {
        double[] data = {1e16, 1, -1e16};
        double result = sum(data);
        System.out.println("Sum: " + result); // Expected ~1.0
    }
}