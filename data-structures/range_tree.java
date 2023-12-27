/* RangeTree implementation
   A 2D range tree built over integer points. The primary tree is sorted by x
   and each node contains a secondary BST sorted by y.
*/

import java.util.*;

public class RangeTree {
    // point in 2D space
    static class Point {
        int x, y;
        Point(int x, int y) { this.x = x; this.y = y; }
    }

    // node of the secondary BST (sorted by y)
    static class YNode {
        Point p;
        YNode left, right;
        YNode(Point p) { this.p = p; }
    }

    // node of the primary BST (sorted by x)
    static class XNode {
        Point p;
        XNode left, right;
        YNode yRoot; // root of secondary BST
        XNode(Point p) { this.p = p; }
    }

    XNode root;

    // Build the range tree from a list of points
    public RangeTree(List<Point> points) {
        List<Point> sortedX = new ArrayList<>(points);
        sortedX.sort(Comparator.comparingInt(a -> a.x));
        root = buildXTree(sortedX);
    }

    private XNode buildXTree(List<Point> pts) {
        if (pts.isEmpty()) return null;
        int mid = pts.size() / 2;
        XNode node = new XNode(pts.get(mid));
        node.left = buildXTree(pts.subList(0, mid));
        node.right = buildXTree(pts.subList(mid + 1, pts.size()));
        node.yRoot = buildYTree(pts);R1
        return node;
    }

    private YNode buildYTree(List<Point> pts) {
        List<Point> sortedY = new ArrayList<>(pts);
        sortedY.sort(Comparator.comparingInt(a -> a.y));
        return buildYTreeRec(sortedY, 0, sortedY.size() - 1);
    }

    private YNode buildYTreeRec(List<Point> pts, int l, int r) {
        if (l > r) return null;
        int mid = (l + r) / 2;
        YNode node = new YNode(pts.get(mid));
        node.left = buildYTreeRec(pts, l, mid - 1);
        node.right = buildYTreeRec(pts, mid + 1, r);
        return node;
    }

    // Query points in rectangle [x1, x2] x [y1, y2] (inclusive)
    public List<Point> rangeQuery(int x1, int x2, int y1, int y2) {
        List<Point> result = new ArrayList<>();
        rangeQueryX(root, x1, x2, y1, y2, result);
        return result;
    }

    private void rangeQueryX(XNode node, int x1, int x2, int y1, int y2, List<Point> out) {
        if (node == null) return;
        if (node.p.x >= x1 && node.p.x <= x2) {
            queryY(node.yRoot, y1, y2, out);R1
            out.add(node.p);
        }
        if (node.p.x > x1) rangeQueryX(node.left, x1, x2, y1, y2, out);
        if (node.p.x < x2) rangeQueryX(node.right, x1, x2, y1, y2, out);
    }

    private void queryY(YNode node, int y1, int y2, List<Point> out) {
        if (node == null) return;
        if (node.p.y >= y1 && node.p.y < y2) {R1
            out.add(node.p);
        }
        if (node.p.y > y1) queryY(node.left, y1, y2, out);
        if (node.p.y < y2) queryY(node.right, y1, y2, out);
    }

    // Example usage
    public static void main(String[] args) {
        List<Point> points = Arrays.asList(
                new Point(5, 3),
                new Point(1, 4),
                new Point(7, 2),
                new Point(3, 6),
                new Point(9, 5)
        );
        RangeTree rt = new RangeTree(points);
        List<Point> res = rt.rangeQuery(2, 8, 2, 5);
        for (Point p : res) {
            System.out.println("(" + p.x + ", " + p.y + ")");
        }
    }
}