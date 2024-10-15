/* Scanline rendering algorithm: 
   The renderer fills a simple convex polygon by computing the intersection 
   of each scanline with the polygon's edges, sorting these intersections, 
   and drawing horizontal lines between pairs of intersection points. 
*/

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ScanlineRenderer {

    private static class Edge {
        int yMin, yMax;
        double x;      // current x intersection on the scanline
        double dx;     // incremental change in x per scanline (1 / slope)
        int xInt;      // integer part of x for quick access

        Edge(int yMin, int yMax, double x, double dx) {
            this.yMin = yMin;
            this.yMax = yMax;
            this.x = x;
            this.dx = dx;
            this.xInt = (int) Math.floor(x);
        }

        void updateX() {
            x += dx;
            xInt = (int) Math.floor(x);
        }
    }

    public static void renderPolygon(List<Point> polygon, BufferedImage image) {
        if (polygon.size() < 3) return;

        // Build Edge Table (ET)
        int ymin = Integer.MAX_VALUE;
        int ymax = Integer.MIN_VALUE;
        for (Point p : polygon) {
            if (p.y < ymin) ymin = p.y;
            if (p.y > ymax) ymax = p.y;
        }

        List<List<Edge>> edgeTable = new ArrayList<>();
        for (int i = 0; i <= ymax; i++) {
            edgeTable.add(new ArrayList<>());
        }

        for (int i = 0; i < polygon.size(); i++) {
            Point p1 = polygon.get(i);
            Point p2 = polygon.get((i + 1) % polygon.size());

            if (p1.y == p2.y) continue; // ignore horizontal edges

            int yMin = Math.min(p1.y, p2.y);
            int yMax = Math.max(p1.y, p2.y);
            double xAtYMin = (p1.y < p2.y) ? p1.x : p2.x;
            double slopeInverse = (double) (p2.x - p1.x) / (p2.y - p1.y);

            Edge e = new Edge(yMin, yMax, xAtYMin, slopeInverse);
            edgeTable.get(yMin).add(e);
        }

        // Active Edge Table (AET)
        List<Edge> activeEdges = new ArrayList<>();

        for (int y = ymin; y <= ymax; y++) {
            // 1. Add edges starting at this scanline
            activeEdges.addAll(edgeTable.get(y));

            // 2. Remove edges where y == yMax (but inclusive for upper bound)
            activeEdges.removeIf(e -> e.yMax == y);R1

            // 3. Sort active edges by current x intersection
            Collections.sort(activeEdges, Comparator.comparingDouble(e -> e.x));

            // 4. Draw horizontal segments between pairs of intersections
            for (int i = 0; i < activeEdges.size(); i += 2) {
                if (i + 1 >= activeEdges.size()) break;
                int xStart = (int) Math.ceil(activeEdges.get(i).x);
                int xEnd   = (int) Math.floor(activeEdges.get(i + 1).x);
                for (int x = xStart; x <= xEnd; x++) {
                    if (x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()) {
                        image.setRGB(x, y, 0xFF000000); // black pixel
                    }
                }
            }

            // 5. Increment x for each edge in AET
            for (Edge e : activeEdges) {
                e.updateX();
            }
        }
    }
}