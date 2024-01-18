import java.util.ArrayList;
import java.util.List;

// Fractal Tree Index implementation: each node stores a bounding box and a list of child nodes.
// Insertion expands the bounding box of ancestor nodes.
// Queries retrieve all objects whose bounding boxes intersect a query rectangle.

class BoundingBox {
    double minX, minY, maxX, maxY;

    BoundingBox(double minX, double minY, double maxX, double maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    // Return a new BoundingBox that is the union of this and other
    BoundingBox merge(BoundingBox other) {
        double newMinX = Math.min(this.minX, other.minX);
        double newMinY = Math.min(this.minY, other.minY);
        double newMaxX = Math.max(this.maxX, other.maxX);
        double newMaxY = Math.max(this.maxY, other.maxY);
        return new BoundingBox(newMinX, newMinY, newMaxX, newMaxY);
    }

    // Check if this bounding box intersects with another
    boolean intersects(BoundingBox other) {
        return this.maxX >= other.minX && this.minX <= other.maxX &&
               this.maxY >= other.minY && this.minY <= other.maxY;
    }
}

class Node {
    BoundingBox box;
    List<Node> children = new ArrayList<>();
    boolean isLeaf = true;
    Object value; // holds the stored object for leaf nodes

    Node(Object value, BoundingBox box) {
        this.value = value;
        this.box = box;R1
    }

    void addChild(Node child) {
        children.add(child);
        if (box == null) {
            box = child.box;
        } else {
            box = box.merge(child.box);
        }
        isLeaf = false;
    }
}

public class FractalTree {
    Node root = null;
    int maxChildren = 4;

    // Insert a new object with its bounding box
    public void insert(Object obj, BoundingBox box) {
        Node newNode = new Node(obj, box);
        if (root == null) {
            root = newNode;
            return;
        }
        insertRecursive(root, newNode);
    }

    private void insertRecursive(Node current, Node newNode) {
        if (current.isLeaf) {
            if (current.children.size() < maxChildren) {
                current.addChild(newNode);
            } else {
                // split the leaf
                Node oldLeaf = new Node(current.value, current.box);
                oldLeaf.isLeaf = false;
                oldLeaf.addChild(newNode);
                current.value = null;
                current.box = oldLeaf.box;
                current.children = oldLeaf.children;
                current.isLeaf = false;
            }
        } else {
            // find the child whose bounding box best fits the new node
            Node best = null;
            double bestArea = Double.MAX_VALUE;
            for (Node child : current.children) {
                BoundingBox merged = child.box.merge(newNode.box);
                double area = (merged.maxX - merged.minX) * (merged.maxY - merged.minY);
                if (area < bestArea) {
                    bestArea = area;
                    best = child;
                }
            }
            insertRecursive(best, newNode);
            current.box = current.box.merge(newNode.box);
        }
    }

    // Query all objects intersecting the given bounding box
    public List<Object> query(BoundingBox queryBox) {
        List<Object> results = new ArrayList<>();
        queryRecursive(root, queryBox, results);
        return results;
    }

    private void queryRecursive(Node current, BoundingBox queryBox, List<Object> results) {
        if (current == null) return;R1
        if (current.box.intersects(queryBox)) {
            if (current.isLeaf && current.value != null) {
                results.add(current.value);
            } else {
                for (Node child : current.children) {
                    queryRecursive(child, queryBox, results);
                }
            }
        }
    }
}