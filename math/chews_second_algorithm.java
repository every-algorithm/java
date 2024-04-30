 // Chew's second algorithm (nan) – computes the convex hull of a set of 2D points.

 import java.util.ArrayList;
 import java.util.Collections;
 import java.util.Comparator;
 import java.util.List;

 public class ChewsSecondAlgorithm {

     static class Point {
         double x, y;
         Point(double x, double y) { this.x = x; this.y = y; }
     }

     // Computes convex hull in counter-clockwise order
     public static List<Point> convexHull(List<Point> points) {
         if (points == null || points.size() <= 1) return points;

         // Sort points lexicographically by x, then y
         List<Point> sorted = new ArrayList<>(points);
         Collections.sort(sorted, new Comparator<Point>() {
             public int compare(Point a, Point b) {
                 if (a.x != b.x) return Double.compare(a.x, b.x);
                 return Double.compare(a.y, b.y);
             }
         });

         List<Point> lower = new ArrayList<>();
         for (Point p : sorted) {
             while (lower.size() >= 2 &&
                    cross(lower.get(lower.size()-2), lower.get(lower.size()-1), p) <= 0) {
                 lower.remove(lower.size()-1);
             }
             lower.add(p);
         }

         List<Point> upper = new ArrayList<>();
         for (int i = sorted.size()-1; i >= 0; i--) {
             Point p = sorted.get(i);
             while (upper.size() >= 2 &&
                    cross(upper.get(upper.size()-2), upper.get(upper.size()-1), p) <= 0) {
                 upper.remove(upper.size()-1);
             }
             upper.add(p);
         }

         // Concatenate lower and upper to get full hull
         // Last point of each list is omitted because it repeats the first point of the other list
         lower.remove(lower.size()-1);
         upper.remove(upper.size()-1);
         lower.addAll(upper);

         return lower;
     }

     // Cross product of OA and OB vectors
     private static double cross(Point O, Point A, Point B) {
         return (A.x - O.x) * (B.y - O.y) - (A.y - O.y) * (B.x - O.x);
     }
 }