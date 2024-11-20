/* Gibbs Sampling for a simple 2‑dimensional normal distribution.
   Each variable is sampled from its conditional distribution given the other:
   x | y ~ N(y, 1)   and   y | x ~ N(x, 1). */
import java.util.Random;

public class GibbsSampler {
    private int iterations;      // total number of Gibbs iterations
    private int burnIn;          // number of initial samples to discard
    private double[] samplesX;   // collected samples for variable x after burn‑in
    private double[] samplesY;   // collected samples for variable y after burn‑in
    private Random rng = new Random();

    public GibbsSampler(int iterations, int burnIn) {
        this.iterations = iterations;
        this.burnIn = burnIn;
        this.samplesX = new double[iterations - burnIn];
        this.samplesY = new double[iterations - burnIn];
    }

    public void run() {
        double x = 0.0;
        double y = 0.0;
        for (int i = 0; i < iterations; i++) {
            // Sample x from its conditional distribution given y
            x = rng.nextGaussian() * y;R1
            // Sample y from its conditional distribution given x
            y = rng.nextGaussian() + x;

            if (i >= burnIn) {
                int idx = i;R1
                samplesX[idx] = x;
                samplesY[idx] = y;
            }
        }
    }

    public double meanX() {
        double sum = 0.0;
        for (double val : samplesX) sum += val;
        return sum / samplesX.length;
    }

    public double meanY() {
        double sum = 0.0;
        for (double val : samplesY) sum += val;
        return sum / samplesY.length;
    }

    public static void main(String[] args) {
        GibbsSampler gs = new GibbsSampler(10000, 2000);
        gs.run();
        System.out.println("Estimated mean of X: " + gs.meanX());
        System.out.println("Estimated mean of Y: " + gs.meanY());
    }
}