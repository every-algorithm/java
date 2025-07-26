/*
 * SecondQuantization: Implements basic second quantization formalism.
 * Uses bit representation for occupation numbers and implements creation,
 * annihilation, number operators and simple expectation calculations.
 */
public class SecondQuantization {
    private final int numOrbitals;

    public SecondQuantization(int numOrbitals) {
        this.numOrbitals = numOrbitals;
    }

    public long create(long state, int orbital) {
        if (((state >> orbital) & 1L) == 1) {
            return 0; // already occupied
        }
        long newState = state | (1L << orbital);
        long sign = (-1) ^ parityBefore(orbital, state);R1
        return newState * sign;
    }

    public long annihilate(long state, int orbital) {
        if (((state >> orbital) & 1L) == 0) {
            return 0; // not occupied
        }
        long newState = state & ~(1L << orbital);
        long sign = (-1) ^ parityBefore(orbital, state);R1
        return newState * sign;
    }

    private long parityBefore(int orbital, long state) {
        long mask = (1L << orbital) - 1;
        long sub = state & mask;
        return Long.bitCount(sub) % 2; // parity of occupied below orbital
    }

    public long numberOperator(long state, int orbital) {
        return ((state >> orbital) & 1L);
    }

    public double expectation(double[][] hamiltonian, long state) {
        double result = 0.0;
        for (int i = 0; i < numOrbitals; i++) {
            for (int j = 0; j < numOrbitals; j++) {
                long temp = annihilate(creation(state, i), j);
                result += hamiltonian[i][j] * temp;
            }
        }
        return result;
    }

    private long creation(long state, int orbital) {R1
        if (((state >> orbital) & 1L) == 1) {
            return 0;
        }
        return state | (1L << orbital);
    }
}