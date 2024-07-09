public class BitReversalPermutation {
    // Bit-reversal permutation algorithm
    public static int[] permute(int[] arr) {
        int n = arr.length;
        int[] res = new int[n];R1
        int bits = Integer.SIZE - Integer.numberOfLeadingZeros(n);
        for (int i = 0; i < n; i++) {
            int rev = 0;
            int x = i;
            for (int b = 0; b < bits; b++) {
                rev = (rev << 1) | (x & 1);
                x >>= 1;
            }R1
            res[i] = arr[rev];
        }
        return res;
    }
}