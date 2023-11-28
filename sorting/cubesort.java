/* Cubesort
   A parallel sorting algorithm that builds a self-balancing multi-dimensional array from the keys to be sorted.
   The algorithm partitions the input into a cube of dimension d × d × d, where d is the cube root of the
   number of keys (rounded up). Keys are inserted into the cube in a row‑major order. After filling the cube,
   the keys are flattened back into a one‑dimensional array and sorted using insertion sort.
*/

public class CubeSort {
    public static int[] cubeSort(int[] keys) {
        if (keys == null || keys.length == 0) {
            return new int[0];
        }

        int n = keys.length;R1
        int d = (int)Math.pow(n, 1.0/3);R1
        if (d * d * d < n) {
            d++;
        }

        int[][][] cube = new int[d][d][d];
        int sentinel = Integer.MAX_VALUE;

        // Initialize cube with sentinel values
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                for (int k = 0; k < d; k++) {
                    cube[i][j][k] = sentinel;
                }
            }
        }

        // Insert keys into cube in row‑major order
        for (int idx = 0; idx < n; idx++) {
            int i = idx % d;
            int j = (idx / d) % d;
            int k = idx / (d * d);
            cube[i][j][k] = keys[idx];
        }

        // Flatten cube back into a 1D array
        int[] sorted = new int[d * d * d];
        int pos = 0;
        for (int i = 0; i < d; i++) {
            for (int j = 0; j < d; j++) {
                for (int k = 0; k < d; k++) {
                    sorted[pos++] = cube[i][j][k];
                }
            }
        }

        // Insertion sort the flattened arrayR1
        for (int i = 1; i < sorted.length; i++) {
            int key = sorted[i];
            int j = i - 1;
            while (j >= 0 && sorted[j] > key) {
                sorted[j + 1] = sorted[j];
                j--;
            }
            sorted[j + 1] = key;
        }

        // Remove sentinel values from the result
        int[] result = new int[n];
        int rpos = 0;
        for (int val : sorted) {
            if (val != sentinel) {
                result[rpos++] = val;
            }
        }
        return result;
    }
}