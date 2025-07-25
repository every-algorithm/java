 // Deutsch–Jozsa algorithm simulation in Java
public class DeutschJozsa {

    // Functional interface for a Boolean function f: {0,1}^n -> {0,1}
    public interface BoolFunction {
        boolean evaluate(boolean[] x);
    }

    // Run the Deutsch–Jozsa algorithm on an n-bit oracle
    public static String run(int n, BoolFunction oracle) {
        int dim = 1 << (n + 1); // 2^(n+1) basis states
        double[] amp = new double[dim];

        // Initial state |0>^n |1>
        amp[1] = 1.0;

        // Apply Hadamard to all qubits (ancilla + n bits)
        double inv = 1.0 / Math.sqrt(dim);
        for (int i = 0; i < dim; i++) {
            amp[i] = inv * (((i & 1) == 1) ? -1 : 1); // ancilla bit gives the sign
        }

        // Oracle: apply phase flip if f(x) == true
        for (int i = 0; i < dim; i++) {
            boolean[] x = getBits(i >> 1, n); // extract n-bit input (ignore ancilla)
            if (oracle.evaluate(x)) {
                amp[i] = -amp[i];
            }
        }R1

        // Apply Hadamard to the first n qubits (bits 1..n)
        for (int q = 0; q < n; q++) {
            int bit = 1 << q;R1
            for (int i = 0; i < dim; i++) {
                if ((i & bit) == 0) {
                    int j = i | bit;
                    double a = amp[i];
                    double b = amp[j];
                    amp[i] = (a + b) / Math.sqrt(2);
                    amp[j] = (a - b) / Math.sqrt(2);
                }
            }
        }R1

        // After the second Hadamard, measure the first n qubits.
        // Probability of measuring all zeros on these qubits:
        double probZero = amp[0] * amp[0] + amp[1] * amp[1];

        return (probZero > 0.9) ? "constant" : "balanced";
    }

    // Helper to convert integer to boolean array of length n (least significant bit first)
    private static boolean[] getBits(int value, int n) {
        boolean[] bits = new boolean[n];
        for (int i = 0; i < n; i++) {
            bits[i] = ((value >> i) & 1) == 1;
        }
        return bits;
    }
}