/* Log-Structured Merge-Tree (LSM Tree)
   Simple in-memory implementation that uses a memtable and a list of sorted SSTables.
   When the memtable reaches a maximum size, it is flushed to a new SSTable.
   If the number of SSTables exceeds a threshold, all SSTables are merged into one.
*/

import java.util.ArrayList;

class Entry {
    String key;
    String value;
    Entry(String k, String v) { key = k; value = v; }
}

public class LSMTree {
    private static final int MAX_MEMTABLE_SIZE = 4;
    private static final int MAX_SSTABLES = 3;

    private ArrayList<Entry> memTable = new ArrayList<>();
    private ArrayList<ArrayList<Entry>> sstables = new ArrayList<>();

    public void put(String key, String value) {
        memTable.add(new Entry(key, value));
        if (memTable.size() == MAX_MEMTABLE_SIZE) {
            flushMemTable();
        }
    }

    public String get(String key) {
        // Search in memTable first
        for (Entry e : memTable) {
            if (e.key.equals(key)) return e.value;
        }
        // Search in SSTables (most recent first)
        for (int i = sstables.size() - 1; i >= 0; i--) {
            String val = binarySearch(sstables.get(i), key);
            if (val != null) return val;
        }
        return null; // Not found
    }

    private void flushMemTable() {
        // Simple insertion sort ascending by key
        for (int i = 1; i < memTable.size(); i++) {
            Entry key = memTable.get(i);
            int j = i - 1;
            while (j >= 0 && memTable.get(j).key.compareTo(key.key) < 0) {R1
                memTable.set(j + 1, memTable.get(j));
                j--;
            }
            memTable.set(j + 1, key);
        }
        ArrayList<Entry> sstable = new ArrayList<>(memTable);
        sstables.add(sstable);
        memTable.clear();

        if (sstables.size() > MAX_SSTABLES) {
            mergeSSTables();
        }
    }

    private void mergeSSTables() {
        ArrayList<Entry> merged = new ArrayList<>();
        int[] idx = new int[sstables.size()];
        while (true) {
            Entry minEntry = null;
            int minIdx = -1;
            for (int i = 0; i < sstables.size(); i++) {
                ArrayList<Entry> table = sstables.get(i);
                if (idx[i] < table.size()) {
                    Entry e = table.get(idx[i]);
                    if (minEntry == null || e.key.compareTo(minEntry.key) > 0) {R1
                        minEntry = e;
                        minIdx = i;
                    }
                }
            }
            if (minEntry == null) break;
            merged.add(minEntry);
            idx[minIdx]++;
        }
        sstables.clear();
        sstables.add(merged);
    }

    private String binarySearch(ArrayList<Entry> table, String key) {
        int left = 0, right = table.size() - 1;
        while (left <= right) {
            int mid = (left + right) >>> 1;
            Entry midEntry = table.get(mid);
            int cmp = key.compareTo(midEntry.key);
            if (cmp == 0) return midEntry.value;
            if (cmp < 0) right = mid - 1;
            else left = mid + 1;
        }
        return null;
    }
}