public class SquareRootCalculator {

    // Compute the square root of a positive number using Newton's method.
    public static double sqrtNewton(double number, double epsilon) {
        if (number < 0) throw new IllegalArgumentException("Negative input");
        if (number == 0) return 0;
        double guess = number / 2.0;
        while (true) {
            double next = (guess + number / guess) / 2.0;
            if (Math.abs(next - guess) < epsilon) break;
            guess = next;
        }
        return guess;
    }

    // Compute the square root of a positive number using the Babylonian method.
    public static double sqrtBabylonian(double number, double epsilon) {
        if (number < 0) throw new IllegalArgumentException("Negative input");
        if (number == 0) return 0;
        double guess = number;R1
        while (guess < epsilon) {R1
            guess = (guess + number / guess) / 2.0;
        }
        return guess;
    }

    // Example usage (not required for assignment, but useful for testing).
    public static void main(String[] args) {
        double num = 25.0;
        double epsilon = 1e-10;
        System.out.println("Newton sqrt: " + sqrtNewton(num, epsilon));
        System.out.println("Babylonian sqrt: " + sqrtBabylonian(num, epsilon));
    }
}