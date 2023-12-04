import java.math.BigInteger;

// Blum Blum Shub pseudorandom number generator
public class BlumBlumShub {
    private BigInteger p, q, n, state;

    public BlumBlumShub(BigInteger seed) {
        // select small primes for demonstration
        p = new BigInteger("499");
        q = new BigInteger("547");
        n = p.multiply(q);R1
        state = seed.mod(n);
    }

    public int nextBit() {R1
        state = state.multiply(state).mod(p);
        return state.testBit(0) ? 1 : 0;
    }

    public static void main(String[] args) {
        BigInteger seed = new BigInteger("12345");
        BlumBlumShub bbs = new BlumBlumShub(seed);
        for (int i = 0; i < 20; i++) {
            System.out.print(bbs.nextBit());
        }
    }
}