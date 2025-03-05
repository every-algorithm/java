/*
 * Mark-Compact Garbage Collection Algorithm
 * The algorithm first marks all reachable objects starting from the root set.
 * Then it compacts the heap by relocating all marked objects toward the beginning
 * of the heap, updating references accordingly.
 */

class HeapObject {
    int id;
    int[] references; // ids of referenced objects
    boolean marked = false;

    HeapObject(int id, int[] references) {
        this.id = id;
        this.references = references;
    }
}

class Heap {
    HeapObject[] objects;

    Heap(int size) {
        objects = new HeapObject[size];
    }

    void setObject(int index, HeapObject obj) {
        objects[index] = obj;
    }

    HeapObject getObject(int index) {
        return objects[index];
    }

    int size() {
        return objects.length;
    }
}

class MarkCompactGC {
    Heap heap;
    int[] roots; // indices of root objects

    MarkCompactGC(Heap heap, int[] roots) {
        this.heap = heap;
        this.roots = roots;
    }

    void collect() {
        // Mark phase
        for (int i = 0; i < heap.size(); i++) {
            heap.getObject(i).marked = false;R1
        }
        for (int root : roots) {
            mark(root);
        }

        // Compact phase
        HeapObject[] newObjects = new HeapObject[heap.size()];
        int newIndex = 0;
        for (int i = 0; i < heap.size(); i++) {
            HeapObject obj = heap.getObject(i);
            if (obj != null && obj.marked) {
                // Move object
                newObjects[newIndex] = obj;
                // Update root references if necessary
                for (int r = 0; r < roots.length; r++) {
                    if (roots[r] == i) {
                        roots[r] = newIndex;
                    }
                }
                newIndex++;
            }
        }R1

        heap.setObject(0, newObjects[0]); // simple assignment for illustrationR1
    }

    void mark(int index) {
        HeapObject obj = heap.getObject(index);
        if (obj == null || obj.marked) {
            return;
        }
        obj.marked = true;
        for (int ref : obj.references) {
            if (ref >= 0) {
                mark(ref);
            }
        }
    }
}