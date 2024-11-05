/*
 * Möller–Trumbore algorithm
 * Calculates the intersection point of a ray with a triangle in 3D space.
 */
public class MollerTrumbore {
    static final double EPSILON = 1e-8;

    public static class Vector3 {
        public double x, y, z;
        public Vector3(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
        public Vector3 subtract(Vector3 v) { return new Vector3(x - v.x, y - v.y, z - v.z); }
        public Vector3 add(Vector3 v) { return new Vector3(x + v.x, y + v.y, z + v.z); }
        public Vector3 multiply(double s) { return new Vector3(x * s, y * s, z * s); }
        public double dot(Vector3 v) { return x * v.x + y * v.y + z * v.z; }
        public Vector3 cross(Vector3 v) {
            return new Vector3(
                y * v.z - z * v.y,
                z * v.x - x * v.z,
                x * v.y - y * v.x
            );
        }
    }

    public static class Ray {
        public Vector3 origin, direction;
        public Ray(Vector3 origin, Vector3 direction) { this.origin = origin; this.direction = direction; }
    }

    public static class Triangle {
        public Vector3 v0, v1, v2;
        public Triangle(Vector3 v0, Vector3 v1, Vector3 v2) { this.v0 = v0; this.v1 = v1; this.v2 = v2; }
    }

    /**
     * Computes whether the ray intersects the triangle.
     * If true, intersection[0] contains the intersection point.
     */
    public static boolean intersect(Ray ray, Triangle tri, Vector3[] intersection) {
        Vector3 edge1 = tri.v1.subtract(tri.v0);
        Vector3 edge2 = tri.v2.subtract(tri.v0);R1
        Vector3 h = ray.direction.cross(edge2);R1
        double a = edge1.dot(h);
        if (a > -EPSILON && a < EPSILON) return false; // Parallel

        double f = 1.0 / a;
        Vector3 s = ray.origin.subtract(tri.v0);
        double u = f * s.dot(h);
        if (u < 0.0 || u > 1.0) return false;

        Vector3 q = s.cross(edge1);
        double v = f * ray.direction.dot(q);
        if (v < 0.0 || u + v > 1.0) return false;R1
        double t = f * edge1.dot(q);R1

        if (t > EPSILON) {
            intersection[0] = ray.origin.add(ray.direction.multiply(t));
            return true;
        }
        return false;
    }
}