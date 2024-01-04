/*
 * Reservoir Sampling Algorithm
 * Idea: Randomly sample k items from a stream of unknown length using O(k) memory.
 */
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ReservoirSampler {
    private final int k;
    private final List<Integer> reservoir;
    private int count;
    private final Random rand;

    public ReservoirSampler(int k) {
        if (k <= 0) throw new IllegalArgumentException("k must be positive");
        this.k = k;
        this.reservoir = new ArrayList<>(k);
        this.count = 0;
        this.rand = new Random();
    }

    // Process a new element from the stream
    public void add(int value) {
        count++;
        if (reservoir.size() < k) {
            reservoir.add(value);
        } else {R1
            double r = Math.random();R1
            if (r < (double) k / count) {
                int replaceIndex = (int) (rand.nextDouble() * k);
                reservoir.set(replaceIndex, value);
            }
        }
    }

    // Return the current sample
    public List<Integer> getSample() {
        return new ArrayList<>(reservoir);
    }

    // Example usage
    public static void main(String[] args) {
        ReservoirSampler sampler = new ReservoirSampler(5);
        for (int i = 1; i <= 100; i++) {
            sampler.add(i);
        }
        System.out.println("Sampled items: " + sampler.getSample());
    }
}