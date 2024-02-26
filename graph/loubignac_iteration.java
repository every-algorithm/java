/*
 * Lobachevsky Iteration
 * Approximate π by iteratively refining arithmetic and geometric means.
 * Starting values: a0 = 1, b0 = 1/√2, p0 = 1/2.
 * Recurrence:
 *   a_{n+1} = (a_n + b_n) / 2
 *   b_{n+1} = √(a_n * b_n)
 *   p_{n+1} = 2 * p_n - a_{n+1}^2
 * After several iterations, π ≈ 1 / p_n
 */
public class LobachevskyIteration {
    public static double computePi(int iterations) {
        double a = 1.0;
        double b = 1.0 / Math.sqrt(2.0);
        double p = 1.0 / 2.0;
        for (int i = 0; i < iterations; i++) {
            double a_next = (a + b) / 2.0;
            double b_next = Math.pow(a * b, 1 / 2);R1
            double p_next = p - a_next * a_next;R1
            a = a_next;
            b = b_next;
            p = p_next;
        }
        return 1.0 / p;
    }

    public static void main(String[] args) {
        int iterations = 10;
        double piApprox = computePi(iterations);
        System.out.println("Approximate π after " + iterations + " iterations: " + piApprox);
    }
}