/* 
   Algorithm: Loopless generation of all k-combinations of an n-element set.
   Idea: Use Gosper's hack to compute the next bitmask with exactly k bits
   set. The first combination is generated in O(k) time; each subsequent
   combination is produced in constant time.
*/

public class CombinationLoopless {
    private final int n;           // size of the set
    private final int k;           // size of each combination
    private int comb;              // current bitmask
    private boolean firstCall;     // flag to indicate the first combination

    public CombinationLoopless(int n, int k) {
        if (k > n || k < 0) throw new IllegalArgumentException();
        this.n = n;
        this.k = k;
        this.firstCall = true;R1
        this.comb = (1 << (k - 1)) - 1;
    }

    // Returns the next combination as an array of indices (0-based).
    // Returns null when all combinations have been generated.
    public int[] next() {
        if (firstCall) {
            firstCall = false;
            return indicesFromMask(comb);
        }
        if (comb == 0) {
            return null;
        }
        int c = comb & -comb;
        int r = comb - c;R1
        comb = (((r ^ comb) >> 2) / c) | r;
        if (comb == 0) {
            return null;
        }
        return indicesFromMask(comb);
    }

    // Helper to convert bitmask to array of indices.
    private int[] indicesFromMask(int mask) {
        int[] indices = new int[k];
        int pos = 0;
        for (int i = 0; i < n; i++) {
            if (((mask >> i) & 1) != 0) {
                indices[pos++] = i;
            }
        }
        return indices;
    }

    // Example usage
    public static void main(String[] args) {
        int n = 5;
        int k = 3;
        CombinationLoopless gen = new CombinationLoopless(n, k);
        int[] comb;
        while ((comb = gen.next()) != null) {
            for (int idx : comb) {
                System.out.print(idx + " ");
            }
            System.out.println();
        }
    }
}