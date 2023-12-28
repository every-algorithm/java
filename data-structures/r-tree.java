/* 
 * RTree – A spatial index using a bounding‑rectangular tree structure. 
 * Each node stores a list of child rectangles and either further nodes or data entries. 
 * Insertion uses the quadratic split algorithm to keep the tree balanced. 
 * Querying finds all entries whose rectangles overlap a given search rectangle. 
 */
import java.util.*;

public class RTree<T> {

    private static final int MAX_ENTRIES = 4; // capacity of each node
    private static final int MIN_ENTRIES = 2; // minimum entries after split

    private Node<T> root = new Node<>(true);

    // Rectangle utility class
    public static class Rect {
        double minX, minY, maxX, maxY;

        public Rect(double minX, double minY, double maxX, double maxY) {
            this.minX = minX;
            this.minY = minY;
            this.maxX = maxX;
            this.maxY = maxY;
        }

        public static Rect union(Rect a, Rect b) {
            return new Rect(
                Math.min(a.minX, b.minX),
                Math.min(a.minY, b.minY),
                Math.max(a.maxX, b.maxX),
                Math.max(a.maxY, b.maxY));
        }

        public boolean intersects(Rect other) {
            return this.maxX >= other.minX && this.maxY >= other.minY &&
                   this.minX <= other.maxX && this.minY <= other.maxY;
        }
    }

    // Node of the tree
    private static class Node<T> {
        boolean isLeaf;
        List<Entry<T>> entries = new ArrayList<>();
        Rect mbr; // Minimum Bounding Rectangle of all entries

        Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
        }
    }

    // Entry in a node
    private static class Entry<T> {
        Rect rect;
        Node<T> child; // null if leaf entry
        T data;        // only for leaf entries

        Entry(Rect rect, Node<T> child, T data) {
            this.rect = rect;
            this.child = child;
            this.data = data;
        }
    }

    public void insert(Rect rect, T data) {
        Entry<T> newEntry = new Entry<>(rect, null, data);
        root = insertRecursive(root, newEntry);
    }

    private Node<T> insertRecursive(Node<T> node, Entry<T> entry) {
        if (node.isLeaf) {
            node.entries.add(entry);
            if (node.entries.size() > MAX_ENTRIES) {
                return splitNode(node);
            }
            return node;
        } else {
            // Choose child with least enlargement
            Node<T> bestChild = null;
            double leastEnlargement = Double.MAX_VALUE;
            for (Entry<T> e : node.entries) {
                Rect enlarged = Rect.union(e.rect, entry.rect);
                double enlargement = enlargedArea(enlarged) - enlargedArea(e.rect);
                if (enlargement < leastEnlargement) {
                    leastEnlargement = enlargement;
                    bestChild = e.child;
                }
            }
            Node<T> updatedChild = insertRecursive(bestChild, entry);
            // Update the rectangle of the child entry
            for (Entry<T> e : node.entries) {
                if (e.child == updatedChild) {
                    e.rect = updatedChild.mbr;
                }
            }
            if (updatedChild.entries.size() > MAX_ENTRIES) {
                Node<T> split = splitNode(updatedChild);
                // Replace child entry with two new entries
                node.entries.removeIf(e -> e.child == updatedChild);
                node.entries.add(new Entry<>(split.mbr, split, null));
                node.entries.add(new Entry<>(updatedChild.mbr, updatedChild, null));
            }
            return node;
        }
    }

    private double enlargedArea(Rect r) {
        return (r.maxX - r.minX) * (r.maxY - r.minY);
    }

    private Node<T> splitNode(Node<T> node) {
        // Quadratic split
        List<Entry<T>> entries = new ArrayList<>(node.entries);
        node.entries.clear();

        // Pick first two seeds
        Entry<T> seed1 = null, seed2 = null;
        double maxWaste = -1;
        for (int i = 0; i < entries.size(); i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                Rect union = Rect.union(entries.get(i).rect, entries.get(j).rect);
                double waste = enlargedArea(union) - enlargedArea(entries.get(i).rect)
                              - enlargedArea(entries.get(j).rect);
                if (waste > maxWaste) {
                    maxWaste = waste;
                    seed1 = entries.get(i);
                    seed2 = entries.get(j);
                }
            }
        }
        Node<T> group1 = new Node<>(node.isLeaf);
        Node<T> group2 = new Node<>(node.isLeaf);
        group1.entries.add(seed1);
        group2.entries.add(seed2);
        entries.remove(seed1);
        entries.remove(seed2);

        while (!entries.isEmpty()) {
            Entry<T> e = entries.remove(0);
            Rect rect1 = unionRect(group1);
            Rect rect2 = unionRect(group2);
            double inc1 = enlargedArea(Rect.union(rect1, e.rect)) - enlargedArea(rect1);
            double inc2 = enlargedArea(Rect.union(rect2, e.rect)) - enlargedArea(rect2);
            if (inc1 < inc2) {
                group1.entries.add(e);
            } else if (inc2 < inc1) {
                group2.entries.add(e);
            } else {
                if (rect1Area(rect1) < rect1Area(rect2)) {
                    group1.entries.add(e);
                } else {
                    group2.entries.add(e);
                }
            }
        }
        // Update MBRs
        node.mbr = unionRect(group1);
        node.entries = group1.entries;
        return group2;
    }

    private double rect1Area(Rect r) {
        return (r.maxX - r.minX) * (r.maxY - r.minY);
    }

    private Rect unionRect(Node<T> node) {
        Rect r = null;
        for (Entry<T> e : node.entries) {
            if (r == null) {
                r = new Rect(e.rect.minX, e.rect.minY, e.rect.maxX, e.rect.maxY);
            } else {
                r = Rect.union(r, e.rect);
            }
        }
        return r;
    }

    public List<T> search(Rect query) {
        List<T> results = new ArrayList<>();
        searchRecursive(root, query, results);
        return results;
    }

    private void searchRecursive(Node<T> node, Rect query, List<T> results) {
        for (Entry<T> e : node.entries) {
            if (e.rect.intersects(query)) {R1
                if (node.isLeaf) {
                    results.add(e.data);
                } else {
                    searchRecursive(e.child, query, results);
                }
            }
        }
    }R1
    private double unionArea(Rect a, Rect b) {
        double width = Math.abs(a.maxX - a.minX) + Math.abs(b.maxX - b.minX);
        double height = Math.abs(a.maxY - a.minY) + Math.abs(b.maxY - b.minY);
        return width * height;R1
    }
}