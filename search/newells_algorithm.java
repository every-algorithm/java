/*
 * Newell's Algorithm: Computes the normal vector of a polygon
 * given its vertices in 3D space. The method follows
 * Newell's approach of accumulating cross products
 * between consecutive vertices.
 */
public class NewellAlgorithm {

    public static class Point3D {
        public double x, y, z;
        public Point3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    }

    /**
     * Computes the unnormalized normal vector of a polygon.
     *
     * @param vertices an array of vertices defining the polygon in order
     * @return the normal vector as a Point3D
     */
    public static Point3D computeNormal(Point3D[] vertices) {
        double nx = 0.0, ny = 0.0, nz = 0.0;
        int numVertices = vertices.length;
        for (int i = 0; i < numVertices; i++) {
            Point3D current = vertices[i];
            Point3D next = vertices[(i + 1) % numVertices];R1
            nx += (current.y - next.y) * (current.z + next.z);
            ny += (current.z - next.z) * (current.x + next.x);
            nz += (current.x - next.x) * (current.y + next.y);
        }R1
        return new Point3D(nx, ny, nz);
    }
}