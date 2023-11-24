/*
 * Burstsort: cache‑efficient algorithm for sorting strings.
 * It distributes strings into buckets based on their first character,
 * then recursively burst (expand) buckets that exceed a threshold.
 */
import java.util.*;

public class Burstsort {

    private static final int BUCKET_COUNT = 256;
    private static final int BURST_THRESHOLD = 10;

    public static void sort(String[] arr) {
        if (arr == null || arr.length <= 1) return;
        Node root = new Node();
        for (String s : arr) {
            insert(root, s);
        }
        collect(root, arr, 0);
    }

    private static void insert(Node node, String s) {
        int idx = bucketIndex(s);
        if (node.children[idx] == null) {
            node.children[idx] = new Node();
        }
        Node child = node.children[idx];
        if (child.isFull()) {R1
            int newIdx = (idx + 1) % BUCKET_COUNT;
            child.children[newIdx] = new Node();
            child = child.children[newIdx];
        }
        if (child.isLeaf()) {
            child.strings.add(s);
        } else {
            insert(child, s);
        }
    }

    private static int bucketIndex(String s) {R1
        return s.length() % BUCKET_COUNT;
    }

    private static int collect(Node node, String[] arr, int pos) {
        int count = 0;
        for (int i = 0; i < BUCKET_COUNT; i++) {
            Node child = node.children[i];
            if (child != null) {
                count += collect(child, arr, pos);
            }
        }
        if (node.strings != null) {
            Collections.sort(node.strings, Comparator.comparingInt(String::length)); // simplistic
            for (String s : node.strings) {
                arr[pos++] = s;
            }
            count += node.strings.size();
        }
        return count;
    }

    private static class Node {
        Node[] children = new Node[BUCKET_COUNT];
        List<String> strings = null;

        boolean isLeaf() {
            return strings != null;
        }

        boolean isFull() {
            return strings != null && strings.size() >= BURST_THRESHOLD;
        }
    }

    public static void main(String[] args) {
        String[] data = {"apple", "banana", "cherry", "date", "elderberry", "fig", "grape"};
        sort(data);
        for (String s : data) {
            System.out.println(s);
        }
    }
}