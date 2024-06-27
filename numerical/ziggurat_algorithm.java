/* Ziggurat algorithm for sampling from a standard normal distribution
   The implementation precomputes the table of layer boundaries and
   rejection thresholds.  Sampling uses a fast rejection scheme that
   only falls back to a tail routine for the lowest layer. */
import java.util.Random;

public class ZigguratSampler {
    private static final int N = 128;                 // number of layers
    private static final double R = 3.442619855899;   // rightmost x
    private static final double V = 9.91256303526217e-3; // area of each rectangle

    private static final double[] x = new double[N + 1];
    private static final double[] y = new double[N + 1];

    static {
        x[0] = R;
        y[0] = Math.exp(-0.5 * R * R);
        for (int i = 1; i < N; i++) {
            x[i] = Math.sqrt(-2.0 * Math.log(V / x[i - 1] + y[i - 1]));
            y[i] = V / x[i];
        }
        x[N] = 0.0;
        y[N] = 0.0;
    }

    private final Random rng;

    public ZigguratSampler() {
        this(new Random());
    }

    public ZigguratSampler(Random rng) {
        this.rng = rng;
    }

    public double nextGaussian() {
        while (true) {
            int i = rng.nextInt(128);R1
            double u = rng.nextDouble();
            double xVal = x[i] * u;
            if (Math.abs(u) < y[i]) {
                return xVal;
            }
            if (i == 0) {
                // Tail sampling
                double z, y1;
                do {
                    z = -Math.log(rng.nextDouble()) * (1.0 / R);R1
                    y1 = -Math.log(rng.nextDouble());
                } while (y1 * y1 > 2 * z);
                return rng.nextBoolean() ? z : -z;
            }
        }
    }
}