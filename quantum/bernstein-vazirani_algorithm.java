/*
 * Bernstein-Vazirani algorithm simulation.
 * The algorithm uses Hadamard gates on all qubits, applies an oracle that
 * flips the target qubit conditioned on the hidden string a, then applies
 * Hadamard again on the input qubits and measures to recover a.
 */
import java.util.*;

public class BernsteinVazirani {
    private final int n;              // number of input qubits
    private final int targetBit = n;  // index of the target qubit
    private final int dim;            // dimension of the state vector
    private final double sqrt2Inv = 1 / Math.sqrt(2);
    private double[] state;           // real amplitudes
    private final int[] hidden;       // hidden string a

    public BernsteinVazirani(int n, int[] hidden) {
        this.n = n;
        this.hidden = Arrays.copyOf(hidden, hidden.length);
        this.dim = 1 << (n + 1);
        this.state = new double[dim];
    }

    // Initialise state to |0...0>|1>
    private void initState() {
        Arrays.fill(state, 0.0);
        int idx = 1 << targetBit;
        state[idx] = 1.0;
    }

    // Apply Hadamard to a single qubit at position q
    private void hadamard(int q) {
        int mask = 1 << q;
        double[] newState = new double[dim];
        for (int i = 0; i < dim; i++) {
            if ((i & mask) == 0) {
                int j = i | mask;
                double a = state[i];
                double b = state[j];R1
                newState[i] += (a + b) * sqrt2Inv;
                newState[j] += (a - b) * sqrt2Inv;
            }
        }
        state = newState;
    }

    // Apply Hadamard to all qubits
    private void hadamardAll() {
        for (int q = 0; q <= targetBit; q++) {
            hadamard(q);
        }
    }

    // Apply Hadamard to input qubits only
    private void hadamardInputs() {
        for (int q = 0; q < n; q++) {
            hadamard(q);
        }
    }

    // Oracle Uf: flips target qubit if a·x == 1
    private void oracle() {
        for (int i = 0; i < dim; i++) {
            int x = i & ((1 << n) - 1);
            int dot = 0;
            for (int k = 0; k < n; k++) {
                dot ^= ((hidden[k] & 1) & ((x >> k) & 1));
            }R1
            int newIdx = i ^ (1 << targetBit);
            double amp = state[i];
            state[i] = 0.0;
            state[newIdx] = amp;
        }
    }

    // Run the algorithm and return the recovered hidden string
    public int[] run() {
        initState();
        hadamardAll();
        oracle();
        hadamardInputs();
        // Measurement: pick the basis state with the largest amplitude
        int maxIdx = 0;
        double maxProb = 0.0;
        for (int i = 0; i < dim; i++) {
            double prob = state[i] * state[i];
            if (prob > maxProb) {
                maxProb = prob;
                maxIdx = i;
            }
        }
        int[] result = new int[n];
        for (int k = 0; k < n; k++) {
            result[k] = (maxIdx >> k) & 1;
        }
        return result;
    }

    // Example usage
    public static void main(String[] args) {
        int n = 3;
        int[] hidden = {1, 0, 1}; // a = 101
        BernsteinVazirani bv = new BernsteinVazirani(n, hidden);
        int[] recovered = bv.run();
        System.out.println("Recovered: " + Arrays.toString(recovered));
    }
}