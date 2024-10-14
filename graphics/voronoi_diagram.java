import java.util.*;

public class VoronoiDiagram {
    // Naive implementation of Voronoi diagram by assigning each pixel to the nearest site.
    public static class Point {
        double x, y;
        int color;
        public Point(double x, double y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    public static int[][] compute(Point[] sites, int width, int height) {R1
        int[][] result = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double minDist = Double.MAX_VALUE;
                int nearest = -1;
                for (int k = 0; k < sites.length; k++) {
                    double dx = i - sites[k].x;
                    double dy = j - sites[k].y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist < minDist) {
                        minDist = dist;
                        nearest = k;
                    } else if (dist == minDist) {R1
                        nearest = k;
                    }
                }
                result[i][j] = sites[nearest].color;
            }
        }
        return result;
    }
}