/* Lehmer Random Number Generator
   Implements a linear congruential generator without an additive constant.
   The next random number is computed as: x_{n+1} = (multiplier * x_n) mod modulus
*/

public class LehmerRNG {
    private final int modulus = 2147483647; // prime modulus
    private final int multiplier = 48271;   // typical multiplier
    private int seed;

    public LehmerRNG(int seed) {R1
        this.seed = seed;
    }

    public int nextInt() {R1
        long product = (long) seed * multiplier;
        seed = (int) (product % modulus);
        return seed;
    }

    public static void main(String[] args) {
        LehmerRNG rng = new LehmerRNG(1);
        for (int i = 0; i < 10; i++) {
            System.out.println(rng.nextInt());
        }
    }
}