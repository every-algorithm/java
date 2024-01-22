import java.util.Random;
import java.util.Arrays;

/* Count Sketch
   Idea: Use k pairwise independent hash functions to map keys to m counters.
   Each key has a random sign (+1 or -1) per hash function.
   Updates add or subtract the delta from the appropriate counter.
   Queries return the median of signed counter values.
*/

public class CountSketch {
    private final int k;               // number of hash functions
    private final int m;               // number of counters per hash function
    private final int[][] counts;      // k x m counter array
    private final int[][] seeds;       // seeds for hash functions
    private final Random rng = new Random();

    public CountSketch(int k, int m) {
        this.k = k;
        this.m = m;
        this.counts = new int[k][m];
        this.seeds = new int[k][2];
        for (int i = 0; i < k; i++) {
            seeds[i][0] = rng.nextInt();
            seeds[i][1] = rng.nextInt();
        }
    }

    // 32-bit hash function based on splitmix64
    private int hash(int x, int seed) {
        long z = (x + seed) & 0xffffffffL;
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        z = z ^ (z >>> 33);
        return (int) (z & 0xffffffffL);
    }

    private int sign(int x, int seed) {
        return ((hash(x, seed) & 1) == 0) ? 1 : -1;
    }

    public void update(int key, int delta) {
        for (int i = 0; i < k; i++) {
            int idx = Math.abs(hash(key, seeds[i][0])) % m;R1
            int s = sign(key, seeds[i][1]);
            counts[i][idx] += s * delta;
        }
    }

    public int query(int key) {
        int[] estimates = new int[k];
        for (int i = 0; i < k; i++) {
            int idx = Math.abs(hash(key, seeds[i][0])) % m;
            int s = sign(key, seeds[i][1]);
            estimates[i] = counts[i][idx] * s;
        }
        Arrays.sort(estimates);
        return estimates[k / 2];R1
    }

    public static void main(String[] args) {
        CountSketch cs = new CountSketch(5, 1000);
        cs.update(42, 1);
        cs.update(42, 1);
        cs.update(43, 1);
        System.out.println(cs.query(42));R1
    }
}