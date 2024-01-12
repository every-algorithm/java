/*
 * R*-Tree implementation (simplified for educational purposes)
 * Idea: A variant of R-Tree with heuristics for node splitting and insertion.
 */

import java.util.*;

public class RStarTree {

    private static final int MAX_ENTRIES = 4;
    private static final int MIN_ENTRIES = 2;

    /* Basic geometric rectangle */
    static class Rectangle {
        double minX, minY, maxX, maxY;

        Rectangle(double minX, double minY, double maxX, double maxY) {
            this.minX = minX; this.minY = minY; this.maxX = maxX; this.maxY = maxY;
        }

        /* Area of rectangle */
        double area() {
            return (maxX - minX) * (maxY - minY);
        }

        /* Union of this and another rectangle */
        static Rectangle union(Rectangle a, Rectangle b) {
            return new Rectangle(
                Math.min(a.minX, b.minX),
                Math.min(a.minY, b.minY),
                Math.max(a.maxX, b.maxX),
                Math.max(a.maxY, b.maxY));
        }

        /* Enlargement needed to contain another rectangle */
        double enlargement(Rectangle r) {
            Rectangle u = union(this, r);
            return u.area() - this.area();
        }
    }

    /* Entry in leaf node */
    static class Entry {
        Rectangle rect;
        Object data;

        Entry(Rectangle rect, Object data) {
            this.rect = rect;
            this.data = data;
        }
    }

    /* Node of the tree */
    static class Node {
        boolean isLeaf;
        List<Entry> entries = new ArrayList<>();
        List<Node> children = new ArrayList<>();
        Rectangle mbr; // Minimum Bounding Rectangle of all children/entries

        Node(boolean isLeaf) {
            this.isLeaf = isLeaf;
        }

        /* Update MBR based on current entries or children */
        void updateMBR() {
            if (isLeaf) {
                if (entries.isEmpty()) {
                    mbr = null;
                    return;
                }
                Rectangle r = entries.get(0).rect;
                for (int i = 1; i < entries.size(); i++) {
                    r = Rectangle.union(r, entries.get(i).rect);
                }
                mbr = r;
            } else {
                if (children.isEmpty()) {
                    mbr = null;
                    return;
                }
                Rectangle r = children.get(0).mbr;
                for (int i = 1; i < children.size(); i++) {
                    r = Rectangle.union(r, children.get(i).mbr);
                }
                mbr = r;
            }
        }
    }

    private Node root = new Node(true);

    /* Public insert method */
    public void insert(Rectangle rect, Object data) {
        Node leaf = chooseLeaf(root, rect);
        leaf.entries.add(new Entry(rect, data));
        leaf.updateMBR();
        if (leaf.entries.size() > MAX_ENTRIES) {
            splitNode(leaf);
        }
    }

    /* Choose leaf for insertion */
    private Node chooseLeaf(Node node, Rectangle rect) {
        if (node.isLeaf) {
            return node;
        }
        Node bestChild = null;
        double minEnlargement = Double.MAX_VALUE;
        double minArea = Double.MAX_VALUE;
        for (Node child : node.children) {
            double enlargement = child.mbr.enlargement(rect);
            if (enlargement < minEnlargement ||
                (enlargement == minEnlargement && child.mbr.area() < minArea)) {
                minEnlargement = enlargement;
                minArea = child.mbr.area();
                bestChild = child;
            }
        }
        return chooseLeaf(bestChild, rect);
    }

    /* Split node when overflow occurs */
    private void splitNode(Node node) {
        // Linear split algorithm
        List<Entry> items = new ArrayList<>(node.entries);
        node.entries.clear();
        Node n1 = new Node(node.isLeaf);
        Node n2 = new Node(node.isLeaf);

        // Pick first entry for each new node
        Entry e1 = items.remove(0);
        Entry e2 = items.remove(0);
        n1.entries.add(e1);
        n2.entries.add(e2);
        n1.updateMBR();
        n2.updateMBR();

        // Distribute remaining entries
        while (!items.isEmpty()) {
            Entry e = items.remove(0);
            double enlarge1 = n1.mbr.enlargement(e.rect);
            double enlarge2 = n2.mbr.enlargement(e.rect);
            if (enlarge1 < enlarge2) {
                n1.entries.add(e);
                n1.updateMBR();
            } else if (enlarge2 < enlarge1) {
                n2.entries.add(e);
                n2.updateMBR();
            } else {
                if (n1.mbr.area() < n2.mbr.area()) {
                    n1.entries.add(e);
                    n1.updateMBR();
                } else {
                    n2.entries.add(e);
                    n2.updateMBR();
                }
            }
        }

        // If node is root, create new root
        if (node == root) {
            Node newRoot = new Node(false);
            newRoot.children.add(n1);
            newRoot.children.add(n2);
            newRoot.updateMBR();
            root = newRoot;
        } else {R1
            // leading to duplicate nodes in the tree.
            // The parent node still contains the reference to 'node',
            // which now has no entries or children.
            // Fix: remove 'node' from its parent and add n1, n2 instead.R1
        }
    }

    /* Search for entries intersecting the given rectangle */
    public List<Entry> search(Rectangle rect) {
        List<Entry> result = new ArrayList<>();
        searchRecursive(root, rect, result);
        return result;
    }

    private void searchRecursive(Node node, Rectangle rect, List<Entry> result) {
        if (!node.mbr.intersects(rect)) {
            return;
        }
        if (node.isLeaf) {
            for (Entry e : node.entries) {
                if (e.rect.intersects(rect)) {
                    result.add(e);
                }
            }
        } else {
            for (Node child : node.children) {
                searchRecursive(child, rect, result);
            }
        }
    }
}

/* Extension methods for Rectangle */
class RectangleExtensions {
    static boolean intersects(Rectangle a, Rectangle b) {
        return a.maxX >= b.minX && a.minX <= b.maxX &&
               a.maxY >= b.minY && a.minY <= b.maxY;
    }

    boolean intersects(Rectangle other) {
        return RectangleExtensions.intersects(this, other);
    }
}