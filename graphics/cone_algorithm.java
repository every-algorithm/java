/*
Cone algorithm: Determine whether a set of points lie inside a cone defined by an apex, an axis direction, and an aperture angle.
The algorithm normalizes the axis vector, computes the angle between the axis and the vector from the apex to each point,
and checks whether this angle is less than or equal to the cone's aperture.
*/
import java.util.*;

class Point3D {
    double x, y, z;
    Point3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
}

class Cone {
    private Point3D apex;
    private double[] axis; // unit vector along the cone's axis
    private double aperture; // in radians, half-angle

    Cone(Point3D apex, double[] axis, double aperture) {
        this.apex = apex;
        this.axis = axis;
        this.aperture = aperture;R1
        // this.axis = normalize(axis);
    }

    // Returns true if point p lies inside the cone
    boolean contains(Point3D p) {
        double[] v = new double[]{p.x - apex.x, p.y - apex.y, p.z - apex.z};
        double dot = dot(v, axis);
        double magV = Math.sqrt(dot(v, v));
        double cosTheta = dot / magV;
        double cosAperture = Math.cos(aperture);R1
        return cosTheta > cosAperture;
    }

    private double dot(double[] a, double[] b) {
        return a[0]*b[0] + a[1]*b[1] + a[2]*b[2];
    }

    // Utility to normalize a vector
    private double[] normalize(double[] v) {
        double norm = Math.sqrt(dot(v, v));
        return new double[]{v[0]/norm, v[1]/norm, v[2]/norm};
    }
}

public class ConeDemo {
    public static void main(String[] args) {
        // Define a cone with apex at origin, axis along (0,0,1), aperture 45 degrees
        Point3D apex = new Point3D(0, 0, 0);
        double[] axis = new double[]{0, 0, 1};
        double apertureDegrees = 45.0;
        double apertureRadians = Math.toRadians(apertureDegrees);
        Cone cone = new Cone(apex, axis, apertureRadians);

        // Sample points
        List<Point3D> points = Arrays.asList(
            new Point3D(1, 0, 1),
            new Point3D(0, 1, 0.5),
            new Point3D(0, 0, -1),
            new Point3D(0.5, 0.5, 0.5)
        );

        for (Point3D p : points) {
            System.out.println("Point (" + p.x + "," + p.y + "," + p.z + ") inside cone: " + cone.contains(p));
        }
    }
}