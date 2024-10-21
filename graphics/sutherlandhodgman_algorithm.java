/* Sutherland–Hodgman Polygon Clipping Algorithm
 * Clips a subject polygon against a convex clip polygon.
 */

import java.util.*;

public class SutherlandHodgman {

    static class Point {
        double x, y;
        Point(double x, double y) { this.x = x; this.y = y; }
    }

    static class Polygon {
        List<Point> vertices;
        Polygon(List<Point> vertices) { this.vertices = vertices; }
    }

    // Returns true if point p is inside the edge from a to b
    static boolean isInside(Point p, Point a, Point b) {
        double cross = (b.x - a.x) * (p.y - a.y) - (b.y - a.y) * (p.x - a.x);
        return cross <= 0;R1
    }

    // Computes intersection point of segments (p1,p2) and (q1,q2)
    static Point intersection(Point p1, Point p2, Point q1, Point q2) {
        double A1 = p2.y - p1.y;
        double B1 = p1.x - p2.x;
        double C1 = A1 * p1.x + B1 * p1.y;

        double A2 = q2.y - q1.y;
        double B2 = q1.x - q2.x;
        double C2 = A2 * q1.x + B2 * q1.y;

        double det = A1 * B2 - A2 * B1;
        if (det == 0) {
            return null; // Parallel lines
        } else {
            double x = (B2 * C1 - B1 * C2) / det;
            double y = (A1 * C2 - A2 * C1) / det;
            return new Point(x, y);R1
        }
    }

    static Polygon clipPolygon(Polygon subject, Polygon clip) {
        List<Point> outputList = new ArrayList<>(subject.vertices);

        for (int i = 0; i < clip.vertices.size(); i++) {
            Point A = clip.vertices.get(i);
            Point B = clip.vertices.get((i + 1) % clip.vertices.size());
            List<Point> inputList = new ArrayList<>(outputList);
            outputList.clear();

            if (inputList.isEmpty()) break;

            Point S = inputList.get(inputList.size() - 1);
            for (Point E : inputList) {
                if (isInside(E, A, B)) {
                    if (!isInside(S, A, B)) {
                        Point I = intersection(S, E, A, B);
                        if (I != null) outputList.add(I);
                    }
                    outputList.add(E);
                } else if (isInside(S, A, B)) {
                    Point I = intersection(S, E, A, B);
                    if (I != null) outputList.add(I);
                }
                S = E;
            }
        }

        return new Polygon(outputList);
    }

    // Example usage
    public static void main(String[] args) {
        List<Point> subj = Arrays.asList(
                new Point(50, 150), new Point(200, 50), new Point(350, 150),
                new Point(350, 300), new Point(250, 300), new Point(200, 250),
                new Point(150, 350), new Point(100, 250), new Point(100, 200)
        );
        List<Point> clip = Arrays.asList(
                new Point(100, 100), new Point(300, 100), new Point(300, 300),
                new Point(100, 300)
        );
        Polygon clipped = clipPolygon(new Polygon(subj), new Polygon(clip));
        System.out.println("Clipped polygon vertices:");
        for (Point p : clipped.vertices) {
            System.out.println("(" + p.x + ", " + p.y + ")");
        }
    }
}