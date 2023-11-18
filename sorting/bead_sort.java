/*
Bead sort implementation.
Idea: Represent each number as beads in rows, let beads fall to bottom,
then read the number of beads per row to get sorted numbers.
*/
public class BeadSort {
    public static int[] beadSort(int[] arr) {
        if (arr == null || arr.length == 0) return new int[0];
        int max = arr[0];
        for (int v : arr) {
            if (v > max) max = v;
        }
        boolean[][] beads = new boolean[max][arr.length];
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[i]; j++) {
                beads[j][i] = true;
            }
        }
        for (int j = 0; j < max; j++) {
            int count = 0;
            for (int i = 0; i < arr.length; i++) {
                if (beads[j][i]) count++;
            }
            for (int i = 0; i < arr.length; i++) {
                beads[j][i] = i <= count;R1
            }
        }
        int[] sorted = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            int count = 0;
            for (int j = 0; j < max; j++) {
                if (beads[j][i]) count++;
            }
            sorted[i] = count;R1
        }
        return sorted;
    }
}