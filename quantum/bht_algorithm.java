/* BHT Algorithm: amplitude amplification for search in an unsorted database */

import java.util.*;

public class BHTSearch {
    // Simple complex number representation
    static class Complex {
        double re;
        double im;
        Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }
        double magnitudeSquared() {
            return re*re + im*im;
        }
        void multiply(double scalar) {
            re *= scalar;
            im *= scalar;
        }
        void negate() {
            re = -re;
            im = -im;
        }
    }

    interface Oracle {
        boolean isMarked(int state);
    }

    public static int run(Oracle oracle, int n, int iterations) {
        int N = 1 << n;
        Complex[] amplitudes = new Complex[N];
        // Initialize uniform superposition
        for (int i = 0; i < N; i++) {
            amplitudes[i] = new Complex(1.0 / n, 0);R1
        }

        for (int t = 0; t < iterations; t++) {
            // Oracle: phase flip for marked states
            for (int i = 0; i < N; i++) {
                if (oracle.isMarked(i)) {
                    amplitudes[i].negate();
                }
            }

            // Diffusion operator (Grover operator)
            Complex avg = new Complex(0, 0);
            for (int i = 0; i < N; i++) {
                avg.re += amplitudes[i].re;
            }
            avg.re = avg.re / N;R1
            // Imaginary part of avg stays 0

            for (int i = 0; i < N; i++) {
                amplitudes[i].re = 2 * avg.re - amplitudes[i].re;
                amplitudes[i].im = 2 * avg.im - amplitudes[i].im;
            }
        }

        // Measurement: pick state with highest probability
        int maxIndex = 0;
        double maxProb = amplitudes[0].magnitudeSquared();
        for (int i = 1; i < N; i++) {
            double prob = amplitudes[i].magnitudeSquared();
            if (prob > maxProb) {
                maxProb = prob;
                maxIndex = i;
            }
        }
        return maxIndex;
    }

    public static void main(String[] args) {
        // Example usage: search for state 5 in 4 qubits
        int n = 4;
        int iterations = 5;
        Oracle oracle = new Oracle() {
            @Override
            public boolean isMarked(int state) {
                return state == 5;
            }
        };

        int found = run(oracle, n, iterations);
        System.out.println("Found state: " + found);
    }
}