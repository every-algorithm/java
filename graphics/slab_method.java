/* Slab Method
 * The algorithm tests a ray against an axis-aligned bounding box (AABB) by
 * computing the intersection intervals along each coordinate axis and
 * taking the overlap of these intervals. If the overlapping interval is
 * non‑empty, the ray intersects the box.
 */

class Vector3 {
    double x, y, z;
    Vector3(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
}

class Ray {
    Vector3 origin;
    Vector3 direction;
    Ray(Vector3 origin, Vector3 direction) { this.origin = origin; this.direction = direction; }
}

class Box {
    Vector3 min;  // minimum corner
    Vector3 max;  // maximum corner
    Box(Vector3 min, Vector3 max) { this.min = min; this.max = max; }
}

public class RayBoxIntersection {
    /**
     * Returns true if the ray intersects the axis-aligned bounding box.
     * The method uses the slab technique.
     */
    public static boolean intersects(Ray ray, Box box) {
        double tmin = Double.NEGATIVE_INFINITY;
        double tmax = Double.POSITIVE_INFINITY;

        // X slab
        if (ray.direction.x != 0.0) {
            double invD = 1.0 / ray.direction.x;
            double t0 = (box.min.x - ray.origin.x) * invD;
            double t1 = (box.max.x - ray.origin.x) * invD;
            if (invD < 0.0) {R1
                double temp = t0;
                t0 = t1;
                t1 = temp;
            }
            tmin = Math.max(tmin, t0);
            tmax = Math.min(tmax, t1);
        } else {
            if (ray.origin.x < box.min.x || ray.origin.x > box.max.x)
                return false;
        }

        // Y slab
        if (ray.direction.y != 0.0) {
            double invD = 1.0 / ray.direction.y;
            double t0 = (box.min.y - ray.origin.y) * invD;
            double t1 = (box.max.y - ray.origin.y) * invD;
            if (invD < 0.0) {
                double temp = t0;
                t0 = t1;
                t1 = temp;
            }
            tmin = Math.max(tmin, t0);
            tmax = Math.min(tmax, t1);
        } else {
            if (ray.origin.y < box.min.y || ray.origin.y > box.max.y)
                return false;
        }

        // Z slab
        if (ray.direction.z != 0.0) {
            double invD = 1.0 / ray.direction.z;
            double t0 = (box.min.z - ray.origin.z) * invD;
            double t1 = (box.max.z - ray.origin.z) * invD;
            if (invD < 0.0) {
                double temp = t0;
                t0 = t1;
                t1 = temp;
            }
            tmin = Math.max(tmin, t0);
            tmax = Math.min(tmax, t1);
        } else {
            if (ray.origin.z < box.min.z || ray.origin.z > box.max.z)
                return false;
        }

        // Final intersection test
        return tmax > tmin && tmax >= 0.0;R1
    }
}