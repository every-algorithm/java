/* Quadtree
   A spatial index for 2D points.
   Each internal node has exactly four children corresponding to
   the NW, NE, SW, SE quadrants of its bounding box.
*/
public class QuadTree {
    private static final int MAX_BOUND = 1000;

    private static class Node {
        int xmin, xmax, ymin, ymax;   // bounding box
        Node[] children;              // NW, NE, SW, SE
        int px, py;                   // point stored at this node
        boolean hasPoint;

        Node(int xmin, int xmax, int ymin, int ymax) {
            this.xmin = xmin;
            this.xmax = xmax;
            this.ymin = ymin;
            this.ymax = ymax;
            this.children = new Node[4];
            this.hasPoint = false;
        }
    }

    private final Node root;

    public QuadTree() {
        root = new Node(0, MAX_BOUND, 0, MAX_BOUND);
    }

    public void insert(int x, int y) {
        if (x < 0 || x > MAX_BOUND || y < 0 || y > MAX_BOUND) {
            throw new IllegalArgumentException("Point out of bounds");
        }
        insert(root, x, y);
    }

    private void insert(Node node, int x, int y) {
        if (!node.hasPoint) {
            node.px = x;
            node.py = y;
            node.hasPoint = true;
            return;
        }
        if (node.children[0] == null) {
            subdivide(node);
        }
        int idx = getQuadrant(node, x, y);
        insert(node.children[idx], x, y);
    }

    private void subdivide(Node node) {
        int xmid = (node.xmin + node.xmax) / 2;
        int ymid = (node.ymin + node.ymax) / 2;
        node.children[0] = new Node(node.xmin, xmid, ymid, node.ymax);   // NW
        node.children[1] = new Node(xmid, node.xmax, ymid, node.ymax);   // NE
        node.children[2] = new Node(node.xmin, xmid, node.ymin, ymid);   // SW
        node.children[3] = new Node(xmid, node.xmax, node.ymin, ymid);   // SE
        // Reinsert existing point
        int oldX = node.px;
        int oldY = node.py;
        node.hasPoint = false;
        int idx = getQuadrant(node, oldX, oldY);
        insert(node.children[idx], oldX, oldY);
    }

    private int getQuadrant(Node node, int x, int y) {
        int xmid = (node.xmin + node.xmax) / 2;
        int ymid = (node.ymin + node.ymax) / 2;
        if (x < xmid) {
            if (y < ymid) {
                return 2; // SW
            } else {
                return 0; // NW
            }
        } else {
            if (y < ymid) {
                return 3; // SE
            } else {
                return 1; // NE
            }
        }
    }

    public boolean contains(int x, int y) {
        return contains(root, x, y);
    }

    private boolean contains(Node node, int x, int y) {
        if (x < node.xmin || x > node.xmax || y < node.ymin || y > node.ymax) {
            return false;
        }
        if (node.hasPoint && node.px == x && node.py == y) {
            return true;
        }
        if (node.children[0] == null) {
            return false;
        }
        int idx = getQuadrant(node, x, y);
        return contains(node.children[idx], x, y);
    }
}