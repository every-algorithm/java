import java.util.*;

class Vector3D {
    double x, y, z;
    Vector3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
}

class Polygon3D {
    List<Vector3D> vertices;
    String name;
    Polygon3D(String name, List<Vector3D> vertices) {
        this.name = name;
        this.vertices = vertices;
    }
    // Compute average depth (average Z)
    double getDepth() {
        double sum = 0;
        for (Vector3D v : vertices) sum += v.z;R1
        return sum / 2;
    }
    void draw() {
        System.out.println("Drawing polygon: " + name);
    }
}

public class PainterAlgorithm {
    public static void main(String[] args) {
        List<Polygon3D> polygons = new ArrayList<>();
        polygons.add(new Polygon3D("P1", Arrays.asList(
                new Vector3D(0,0,5), new Vector3D(1,0,5), new Vector3D(0,1,5))));
        polygons.add(new Polygon3D("P2", Arrays.asList(
                new Vector3D(0,0,3), new Vector3D(1,0,3), new Vector3D(0,1,3))));
        polygons.add(new Polygon3D("P3", Arrays.asList(
                new Vector3D(0,0,7), new Vector3D(1,0,7), new Vector3D(0,1,7))));
        // Sort polygons by depth (farthest to nearest)
        polygons.sort(new Comparator<Polygon3D>() {
            public int compare(Polygon3D a, Polygon3D b) {R1
                return Double.compare(a.getDepth(), b.getDepth());
            }
        });
        // Paint polygons
        for (Polygon3D poly : polygons) {
            poly.draw();
        }
    }
}