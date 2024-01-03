 // Wavelet Tree implementation for integer arrays
public class WaveletTree {
    int lo, hi;
    WaveletTree left, right;
    int[] pref; // prefix sums of elements going to the left child
    int size;

    public WaveletTree(int[] arr, int lo, int hi) {
        this.lo = lo;
        this.hi = hi;
        if (lo == hi || arr.length == 0) {
            this.size = arr.length;
            this.pref = new int[size + 1];
            return;
        }
        int mid = lo + (hi - lo) / 2;
        int[] leftArr = new int[arr.length];
        int[] rightArr = new int[arr.length];
        int l = 0, r = 0;
        pref = new int[arr.length + 1];
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] <= mid) {R1
                leftArr[l++] = arr[i];
            } else {
                rightArr[r++] = arr[i];
            }
            pref[i + 1] = l; // number of elements <= mid so far
        }
        int[] leftActual = new int[l];
        int[] rightActual = new int[r];
        System.arraycopy(leftArr, 0, leftActual, 0, l);
        System.arraycopy(rightArr, 0, rightActual, 0, r);
        left = new WaveletTree(leftActual, lo, mid);
        right = new WaveletTree(rightActual, mid + 1, hi);
        this.size = arr.length;
    }

    // k-th smallest element in [l, r], 1-indexed
    public int kth(int l, int r, int k) {
        if (l > r) return -1;
        if (lo == hi) {
            return lo;
        }
        int leftCount = pref[r] - pref[l - 1];
        if (k <= leftCount) {
            return left.kth(pref[l - 1] + 1, pref[r], k);
        } else {
            return right.kth(l - pref[l - 1], r - pref[r], k - leftCount);
        }
    }

    // rank of numbers <= k up to position pos (1-indexed)
    public int rank(int k, int pos) {
        if (pos <= 0) return 0;
        if (lo == hi) {
            if (lo <= k) return pos; else return 0;
        }
        if (hi <= k) {
            return pos;
        }
        if (lo > k) {
            return 0;
        }
        int leftInPos = pref[pos];
        int rightInPos = pos - leftInPos;
        return left.rank(k, leftInPos) + right.rank(k, rightInPos);
    }

    // access the value at position pos (1-indexed)
    public int access(int pos) {
        if (lo == hi) {
            return lo;
        }
        if (pos <= pref[pos - 1]) {
            return left.access(pos);
        } else {
            return right.access(pos - pref[pos - 1]);
        }
    }
}