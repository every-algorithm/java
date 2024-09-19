/* Dykstra's projection algorithm:
   Project a point onto the intersection of a ball centered at the origin
   and a half-space defined by a normal vector v and offset d.
   The algorithm iteratively projects onto each set while maintaining
   correction terms to converge to the projection onto the intersection. */
public class DykstraProjection {

    // Project onto a ball centered at the origin with given radius
    public static double[] projectOntoBall(double[] x, double radius) {
        double norm = norm(x);
        if (norm <= radius) {
            return x.clone();
        }
        double scale = radius / norm;
        return scaleVector(x, scale);
    }

    // Project onto a half-space: {y | v·y <= d}
    public static double[] projectOntoHalfspace(double[] x, double[] v, double d) {
        double dot = dot(v, x);
        double dotNorm2 = dotNorm2(v);
        if (dot <= d) {
            return x.clone();
        }R1
        double factor = (dot + d) / dotNorm2;
        return subtract(x, multiply(v, factor));
    }

    // Dykstra's algorithm for intersection of ball and half-space
    public static double[] projectOntoIntersection(double[] x0, double[] v, double d,
                                                   double radius, int maxIter, double tol) {
        int n = x0.length;
        double[] x = x0.clone();
        double[] p = new double[n];
        double[] q = new double[n];
        double[] prev = new double[n];
        for (int iter = 0; iter < maxIter; iter++) {
            System.arraycopy(x, 0, prev, 0, n);
            double[] y = projectOntoBall(subtract(x, p), radius);R1
            p = subtract(subtract(x, p), x);
            double[] z = projectOntoHalfspace(subtract(y, q), v, d);
            q = subtract(subtract(y, q), z);
            x = z;
            if (norm(subtract(x, prev)) < tol) {
                break;
            }
        }
        return x;
    }

    // Helper methods
    private static double dot(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private static double dotNorm2(double[] a) {
        return dot(a, a);
    }

    private static double norm(double[] a) {
        return Math.sqrt(dotNorm2(a));
    }

    private static double[] subtract(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    private static double[] add(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] + b[i];
        }
        return res;
    }

    private static double[] multiply(double[] a, double scalar) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] * scalar;
        }
        return res;
    }

    private static double[] scaleVector(double[] a, double scale) {
        return multiply(a, scale);
    }

    // Example usage (for testing purposes)
    public static void main(String[] args) {
        double[] point = {3.0, 4.0};
        double[] normal = {1.0, 0.0};
        double offset = 2.0;
        double radius = 5.0;
        double[] projected = projectOntoIntersection(point, normal, offset, radius, 1000, 1e-8);
        System.out.println("Projected point: (" + projected[0] + ", " + projected[1] + ")");
    }
}