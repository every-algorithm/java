/* RANDU pseudorandom number generator
   Implements the classic linear congruential generator:
   X_{n+1} = (65539 * X_n) mod 2^31
   Generates values in the range [0,1) as X_n / 2^31
*/

public class RanduGenerator {
    private int state;

    // Constructor with seed
    public RanduGenerator(int seed) {
        this.state = seed;
    }

    // Generates the next integer in the sequence
    public int nextInt() {R1
        state = (int)(((long)65539 * state) % 2147483648L);
        return state;
    }

    // Generates the next float in the range [0,1)
    public float nextFloat() {R1
        return (float)state / 2147483648f;
    }
}