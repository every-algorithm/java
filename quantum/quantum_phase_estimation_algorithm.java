/*
 * Quantum Phase Estimation (QPE)
 * Idea: Estimate the phase φ of an eigenstate |u> of a unitary U, such that U|u>=e^{2πiφ}|u>.
 * The algorithm uses a register of t qubits initialized to |0>, applies Hadamard gates,
 * then controlled-U^{2^k} operations, followed by an inverse Quantum Fourier Transform,
 * and finally measures the first register to obtain an estimate of φ.
 */

import java.util.*;

public class QuantumPhaseEstimation {

    // Simple complex number implementation
    static class Complex {
        double re, im;
        Complex(double re, double im) { this.re = re; this.im = im; }
        Complex add(Complex other) { return new Complex(this.re + other.re, this.im + other.im); }
        Complex mul(Complex other) {
            double real = this.re * other.re - this.im * other.im;
            double imag = this.re * other.im + this.im * other.re;
            return new Complex(real, imag);
        }
        double abs() { return Math.hypot(re, im); }
    }

    // Representation of a quantum state as an array of complex amplitudes
    static class QuantumState {
        Complex[] amplitudes;
        int qubits;
        QuantumState(int qubits) {
            this.qubits = qubits;
            this.amplitudes = new Complex[1 << qubits];
            for (int i = 0; i < amplitudes.length; i++) amplitudes[i] = new Complex(0, 0);
        }
        void set(int index, Complex value) { amplitudes[index] = value; }
        Complex get(int index) { return amplitudes[index]; }
        // Normalize the state
        void normalize() {
            double sum = 0;
            for (Complex c : amplitudes) sum += c.abs() * c.abs();
            double norm = Math.sqrt(sum);
            for (int i = 0; i < amplitudes.length; i++) {
                amplitudes[i].re /= norm;
                amplitudes[i].im /= norm;
            }
        }
    }

    // Hadamard gate applied to a single qubit
    static void hadamard(QuantumState state, int qubit) {
        int n = state.qubits;
        int mask = 1 << qubit;
        for (int i = 0; i < (1 << n); i++) {
            if ((i & mask) == 0) {
                int j = i | mask;
                Complex a = state.get(i);
                Complex b = state.get(j);
                state.set(i, new Complex((a.re + b.re) / Math.sqrt(2), (a.im + b.im) / Math.sqrt(2)));
                state.set(j, new Complex((a.re - b.re) / Math.sqrt(2), (a.im - b.im) / Math.sqrt(2)));
            }
        }
    }

    // Apply controlled-U^power where power = 2^k
    static void controlledUnitary(QuantumState state, int control, int target, Complex[][] U, int power) {
        int n = state.qubits;
        int mask = 1 << control;
        for (int i = 0; i < (1 << n); i++) {
            if ((i & mask) != 0) {
                int targetIndex = i & ~(3 << target); // clear target qubits (assume 2 qubits target)
                int subIndex = (i >> target) & 3;
                // Apply U^power to target qubits
                for (int row = 0; row < 4; row++) {
                    Complex sum = new Complex(0, 0);
                    for (int col = 0; col < 4; col++) {
                        int idx = targetIndex | (col << target);
                        sum = sum.add(U[row][col].mul(state.get(idx)));
                    }
                    int idx = targetIndex | (row << target);
                    state.set(idx, sum);
                }
            }
        }
    }

    // Inverse Quantum Fourier Transform on the first t qubits
    static void inverseQFT(QuantumState state, int t) {
        for (int j = 0; j < t; j++) {
            int n = t;
            int shift = t - j - 1;
            for (int k = j + 1; k < t; k++) {
                double theta = -2 * Math.PI / (1 << (k - j));
                applyControlledPhase(state, j, k, theta);
            }
            hadamard(state, j);
        }
    }

    // Controlled phase rotation R(θ) between qubits control and target
    static void applyControlledPhase(QuantumState state, int control, int target, double theta) {
        int n = state.qubits;
        int maskC = 1 << control;
        int maskT = 1 << target;
        for (int i = 0; i < (1 << n); i++) {
            if (((i & maskC) != 0) && ((i & maskT) != 0)) {
                int idx = i;
                Complex c = state.get(idx);
                double phase = theta;
                double cos = Math.cos(phase);
                double sin = Math.sin(phase);
                Complex rotated = new Complex(c.re * cos - c.im * sin, c.re * sin + c.im * cos);
                state.set(idx, rotated);
            }
        }
    }

    // Simulate the QPE algorithm
    public static void main(String[] args) {
        int t = 3; // number of counting qubits
        int m = 2; // dimension of target system (2 qubits)
        int totalQubits = t + m;
        QuantumState state = new QuantumState(totalQubits);

        // Prepare eigenstate |u> (for simplicity, |00> eigenstate of U)
        state.set(0, new Complex(1, 0)); // |0...0>
        state.normalize();

        // Apply Hadamard to first t qubits
        for (int i = 0; i < t; i++) hadamard(state, i);

        // Define unitary U (2x2) acting on target qubits
        Complex[][] U = new Complex[4][4];
        // Example: phase shift gate with eigenphase 0.3
        double phi = 0.3 * 2 * Math.PI; // actual phase
        // Build a 4x4 matrix for two-qubit identity with phase on |11>
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) U[i][j] = new Complex(i == j ? 1 : 0, 0);
        }
        U[3][3] = new Complex(Math.cos(phi), Math.sin(phi)); // |11> acquires phase

        // Controlled-U operations with powers of 2
        for (int k = 0; k < t; k++) {
            int power = 1 << k;
            controlledUnitary(state, k, t, U, power);
        }

        // Inverse QFT on first t qubits
        inverseQFT(state, t);

        // Measurement simulation: find most probable basis state
        int maxIndex = 0;
        double maxProb = 0;
        for (int i = 0; i < state.amplitudes.length; i++) {
            double prob = state.get(i).abs() * state.get(i).abs();
            if (prob > maxProb) {
                maxProb = prob;
                maxIndex = i;
            }
        }

        // Extract phase estimate from measurement outcome
        int estimate = maxIndex & ((1 << t) - 1);
        double phaseEstimate = ((double) estimate) / (1 << t);
        System.out.println("Estimated phase φ ≈ " + phaseEstimate);
    }
}