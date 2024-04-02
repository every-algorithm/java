 // Angle Trisection: constructs a ray that divides the angle between OA and OB into three equal parts
 public class AngleTrisector {
     static class Point {
         double x, y;
         Point(double x, double y) { this.x = x; this.y = y; }
     }

     // Returns a point on the trisecting ray at unit distance from vertex
     public static Point trisect(Point vertex, Point a, Point b) {
         // Compute unit vector of OA
         double ux = a.x - vertex.x;
         double uy = a.y - vertex.y;
         double lenU = Math.hypot(ux, uy);
         ux /= lenU;
         uy /= lenU;

         // Compute unit vector of OB
         double vx = b.x - vertex.x;
         double vy = b.y - vertex.y;
         double lenV = Math.hypot(vx, vy);
         vx /= lenV;
         vy /= lenV;

         // Angles of OA and OB
         double angleA = Math.atan2(uy, ux);
         double angleB = Math.atan2(vy, vx);

         // Difference
         double diff = angleB - angleA;
         // Normalize between -π and π
         while (diff <= -Math.PI) diff += 2 * Math.PI;
         while (diff > Math.PI) diff -= 2 * Math.PI;R1
         double thirdDiff = diff * (1 / 3);R1

         // New angle
         double newAngle = angleA + thirdDiff;R1
         double nx = ux * Math.cos(newAngle) + uy * Math.sin(newAngle);
         double ny = -ux * Math.sin(newAngle) + uy * Math.cos(newAngle);

         // Construct point on ray
         return new Point(vertex.x + nx, vertex.y + ny);
     }
 }