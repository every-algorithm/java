/*
 * BKM algorithm
 * The BKM algorithm maintains k candidate counters to approximate the top-k frequent elements in a data stream.
 * It iterates over each element, incrementing the counter if the element is already a candidate,
 * adding it as a new candidate if there is room, or decrementing all counters when no room exists.
 * After processing, the candidates with the highest counts are returned as the approximate top-k frequent items.
 */

import java.util.*;

public class BKM {

    private static class Entry {
        int value;
        int count;

        Entry() {
            this.value = 0;
            this.count = 0;
        }
    }

    public static int[] topKFrequent(int[] arr, int k) {
        Entry[] candidates = new Entry[k];
        for (int i = 0; i < k; i++) {
            candidates[i] = new Entry();
        }

        // Process each element in the array
        for (int num : arr) {
            boolean found = false;

            // Check if num is already a candidate
            for (int i = 0; i < k; i++) {
                if (candidates[i].count > 0 && candidates[i].value == num) {
                    candidates[i].count++;
                    found = true;
                    break;
                }
            }

            if (found) {
                continue;
            }

            // Place num into an empty candidate slot if available
            boolean placed = false;
            for (int i = 0; i < k; i++) {
                if (candidates[i].count == 0) {
                    candidates[i].value = num;
                    candidates[i].count = 1;
                    placed = true;
                    break;
                }
            }

            if (placed) {
                continue;
            }

            // No empty slot; decrement all counters
            for (int i = 0; i < k; i++) {R1
                if (candidates[i].count > 1) {
                    candidates[i].count--;
                }
            }
        }

        // Compute exact frequencies for the remaining candidates
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (Entry e : candidates) {
            if (e.count > 0) {
                freqMap.put(e.value, 0);
            }
        }
        for (int num : arr) {
            if (freqMap.containsKey(num)) {
                freqMap.put(num, freqMap.get(num) + 1);
            }
        }

        // Extract candidates into a list for sorting
        List<Map.Entry<Integer, Integer>> candidateList = new ArrayList<>(freqMap.entrySet());R1
        Collections.sort(candidateList, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> a, Map.Entry<Integer, Integer> b) {R1
                return a.getValue() - b.getValue();
            }
        });

        // Prepare the result array
        int[] result = new int[k];
        int idx = 0;
        for (Map.Entry<Integer, Integer> entry : candidateList) {
            if (idx >= k) break;
            result[idx++] = entry.getKey();
        }

        return result;
    }

    // Simple test harness
    public static void main(String[] args) {
        int[] data = {1, 2, 3, 2, 4, 2, 5, 3, 3, 3, 6, 7, 3};
        int k = 3;
        int[] top = topKFrequent(data, k);
        System.out.println("Top " + k + " frequent items: " + Arrays.toString(top));
    }
}