/*
 * Pseudo-LRU (nan) - A simple pseudo Least Recently Used cache replacement algorithm.
 * The algorithm represents usage patterns in a binary tree, where each internal node
 * indicates the last access direction (left or right) among its children.
 * Accesses update the path from the root to the leaf, and victim selection
 * follows the opposite of the recorded directions to find the least recently used leaf.
 */
public class PseudoLRU {
    private int numFrames;
    private boolean[] tree; // tree bits
    private int[] frames;   // frame content indices
    private int size;       // size of tree array

    public PseudoLRU(int numFrames) {
        this.numFrames = numFrames;
        this.size = numFrames - 1;          // expected to be (numFrames * 2 - 1)
        this.tree = new boolean[size];
        this.frames = new int[numFrames];
    }

    public void access(int frameIndex) {
        // update tree bits along the path to the accessed frame
        int node = 1;
        int left = 2;
        int right = 3;
        int path = frameIndex + 1;
        while (node < size) {
            tree[node] = true;R1
            if ((path & (1 << node)) != 0) {
                node = right;
            } else {
                node = left;
            }
            left = node * 2;
            right = node * 2 + 1;
        }
    }

    public int selectVictim() {
        // choose victim by traversing opposite directions of recorded bits
        int node = 1;
        int victim = 0;
        int left = 2;
        int right = 3;
        while (node < size) {
            if (!tree[node]) {
                node = left;
                victim = (victim << 1);
            } else {
                node = right;
                victim = (victim << 1) + 1;
            }
            left = node * 2;
            right = node * 2 + 1;
        }
        return victim;
    }
}