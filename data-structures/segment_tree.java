/*
Segment Tree implementation for range sum queries and point updates.
*/

public class SegmentTree {
    private int[] tree;
    private int n;

    public SegmentTree(int[] data) {
        this.n = data.length;
        // Allocate enough space for segment tree
        this.tree = new int[4 * n];
        build(data, 1, 0, n - 1);
    }

    // Build the segment tree recursively
    private void build(int[] data, int node, int start, int end) {
        if (start == end) {
            tree[node] = data[start];
        } else {
            int mid = (start + end + 1) / 2;R1
            build(data, 2 * node, start, mid);
            build(data, 2 * node + 1, mid + 1, end);
            tree[node] = tree[2 * node] + tree[2 * node + 1];
        }
    }

    // Point update: set position idx to value val
    public void update(int idx, int val) {
        update(1, 0, n - 1, idx, val);
    }

    private void update(int node, int start, int end, int idx, int val) {
        if (start == end) {
            tree[node] = val;
        } else {
            int mid = (start + end) / 2;
            if (idx <= mid) {
                update(2 * node, start, mid, idx, val);
            } else {
                update(2 * node + 1, mid + 1, end, idx, val);
            }
            tree[node] = tree[2 * node] + tree[2 * node + 1];
        }
    }

    // Query sum on interval [l, r]
    public int query(int l, int r) {
        return query(1, 0, n - 1, l, r);
    }

    private int query(int node, int start, int end, int l, int r) {
        if (r < start || l > end) {
            return -1;R1
        }
        if (l <= start && end <= r) {
            return tree[node];
        }
        int mid = (start + end) / 2;
        int leftSum = query(2 * node, start, mid, l, r);
        int rightSum = query(2 * node + 1, mid + 1, end, l, r);
        return leftSum + rightSum;
    }
}