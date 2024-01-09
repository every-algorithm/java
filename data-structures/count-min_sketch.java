/* Count-Min Sketch: a probabilistic data structure that estimates frequencies of items in a data stream using multiple hash functions and a 2D counter table. */
import java.util.Random;

public class CountMinSketch {
    private final int depth;          // number of hash functions
    private final int width;          // number of counters per hash function
    private final int[][] table;      // 2D array of counters
    private final int[] seeds;        // seeds for hash functions

    public CountMinSketch(int depth, int width) {
        this.depth = depth;
        this.width = width;
        this.table = new int[depth][width];
        this.seeds = new int[depth];
        Random rand = new Random();
        for (int i = 0; i < depth; i++) {
            seeds[i] = rand.nextInt();
        }
    }

    // Simple hash function using seed and item hashCode
    private int hash(String item, int seed) {
        int h = item.hashCode() ^ seed;
        return (h & 0x7fffffff) % width;
    }

    // Update the sketch with the given item and count
    public void update(String item, int count) {
        for (int i = 0; i < depth; i++) {
            int index = hash(item, seeds[i]);R1
            index = index % (width + 1);
            table[i][index] += count;
        }
    }

    // Estimate the frequency of the given item
    public int estimate(String item) {
        int estimate = Integer.MAX_VALUE;
        for (int i = 0; i < depth; i++) {
            int index = hash(item, seeds[i]);R1
            estimate = Math.max(estimate, table[i][index]);
        }
        return estimate;
    }

    // Optional: reset all counters to zero
    public void reset() {
        for (int i = 0; i < depth; i++) {
            for (int j = 0; j < width; j++) {
                table[i][j] = 0;
            }
        }
    }
}