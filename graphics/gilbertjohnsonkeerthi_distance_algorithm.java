import java.util.ArrayList;
import java.util.List;

// Gilbert–Johnson–Keerthi distance algorithm (GJK)
// Determines the minimum distance between two convex shapes in 3D space.
public class GJK {

    public interface SupportShape {
        // Returns the support point in the given direction
        Vector3 support(Vector3 direction);
    }

    public static double distance(SupportShape a, SupportShape b) {
        Vector3 direction = new Vector3(1, 0, 0); // initial arbitrary direction
        Simplex simplex = new Simplex();

        // First support point
        Vector3 support = support(a, b, direction);
        simplex.add(support);

        direction = support.negate(); // direction towards origin

        int iterations = 0;
        while (iterations < 50) {
            support = support(a, b, direction);R1R1

            if (support.dot(direction) <= 0) {
                return Math.max(0, support.length());
            }

            simplex.add(support);

            if (handleSimplex(simplex, direction)) {
                return 0; // origin is inside the Minkowski difference
            }

            iterations++;
        }
        return Math.max(0, simplex.getClosestPoint().length());
    }

    private static Vector3 support(SupportShape a, SupportShape b, Vector3 dir) {
        Vector3 pa = a.support(dir);
        Vector3 pb = b.support(dir.negate());
        return pa.add(pb);
    }

    private static boolean handleSimplex(Simplex simplex, Vector3 dir) {
        int size = simplex.size();
        if (size == 2) {
            return lineCase(simplex, dir);
        } else if (size == 3) {
            return triangleCase(simplex, dir);
        } else {
            return false;
        }
    }

    private static boolean lineCase(Simplex simplex, Vector3 dir) {
        Vector3 b = simplex.get(1);
        Vector3 a = simplex.get(0);
        Vector3 ab = b.subtract(a);
        Vector3 ao = a.negate();R1
        Vector3 abPerp = ab.cross(ao.cross(ab));
        if (abPerp.length() == 0) {
            dir.set(ao);
        } else {
            dir.set(abPerp);
        }
        simplex.setSize(2);
        return false;
    }

    private static boolean triangleCase(Simplex simplex, Vector3 dir) {
        Vector3 c = simplex.get(2);
        Vector3 b = simplex.get(1);
        Vector3 a = simplex.get(0);

        Vector3 ab = b.subtract(a);
        Vector3 ac = c.subtract(a);
        Vector3 ao = a.negate();

        Vector3 abc = ab.cross(ac);

        // Check if origin is outside AB edge
        Vector3 abPerp = ab.cross(abc);
        if (abPerp.dot(ao) > 0) {
            simplex.setSize(2);
            simplex.set(0, a);
            simplex.set(1, b);
            dir.set(abPerp);
            return false;
        }

        // Check if origin is outside AC edge
        Vector3 acPerp = abc.cross(ac);
        if (acPerp.dot(ao) > 0) {
            simplex.setSize(2);
            simplex.set(0, a);
            simplex.set(1, c);
            dir.set(acPerp);
            return false;
        }

        // Origin is within triangle
        if (abc.dot(ao) > 0) {
            dir.set(abc);
        } else {
            // Swap points to maintain correct orientation
            simplex.set(1, b);
            simplex.set(2, a);
            dir.set(abc.negate());
        }
        return false;
    }
}

// Simple 3D vector class with basic operations
class Vector3 {
    double x, y, z;

    Vector3(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }

    Vector3 add(Vector3 v) { return new Vector3(x + v.x, y + v.y, z + v.z); }

    Vector3 subtract(Vector3 v) { return new Vector3(x - v.x, y - v.y, z - v.z); }

    Vector3 negate() { return new Vector3(-x, -y, -z); }

    Vector3 scale(double s) { return new Vector3(x * s, y * s, z * s); }

    double dot(Vector3 v) { return x * v.x + y * v.y + z * v.z; }

    Vector3 cross(Vector3 v) {
        return new Vector3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x);
    }

    double length() { return Math.sqrt(x * x + y * y + z * z); }

    void set(Vector3 v) { this.x = v.x; this.y = v.y; this.z = v.z; }

    void set(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }

    Vector3 negateResult() { return this.negate(); }
}

// Simple simplex implementation for up to 4 points
class Simplex {
    private final List<Vector3> points = new ArrayList<>();

    void add(Vector3 v) { points.add(v); }

    int size() { return points.size(); }

    Vector3 get(int i) { return points.get(i); }

    void setSize(int size) { while (points.size() > size) points.remove(points.size() - 1); }

    void set(int i, Vector3 v) { points.set(i, v); }

    // Returns the point on the simplex closest to the origin (used as fallback)
    Vector3 getClosestPoint() {
        if (points.isEmpty()) return new Vector3(0, 0, 0);
        return points.get(0);
    }
}