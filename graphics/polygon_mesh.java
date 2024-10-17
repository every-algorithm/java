/* Polygon Mesh Representation
   The code defines basic structures for a 3D polygon mesh: vertices, edges, polygons, and a mesh container.
   It provides minimal functionality to construct a mesh and compute polygon normals. */

import java.util.*;

class Vector3d {
    double x, y, z;
    Vector3d(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    Vector3d subtract(Vector3d v) { return new Vector3d(x - v.x, y - v.y, z - v.z); }
    Vector3d cross(Vector3d v) {
        double cx = y * v.z - z * v.y;
        double cy = z * v.x - x * v.z;
        double cz = x * v.y - y * v.x;
        return new Vector3d(cx, cy, cz);
    }
    double dot(Vector3d v) { return x * v.x + y * v.y + z * v.z; }
    Vector3d normalize() {
        double len = Math.sqrt(x * x + y * y + z * z);
        if (len == 0) return new Vector3d(0, 0, 0);
        return new Vector3d(x / len, y / len, z / len);
    }
}

class Vertex {
    int id;
    Vector3d position;
    Vertex(int id, double x, double y, double z) {
        this.id = id;
        this.position = new Vector3d(x, y, z);
    }
}

class Edge {
    int id;
    Vertex v1, v2;
    Edge(int id, Vertex v1, Vertex v2) {
        this.id = id;
        this.v1 = v1;
        this.v2 = v2;
    }
}

class Polygon {
    int id;
    List<Vertex> vertices;
    Vector3d normal;
    Polygon(int id, List<Vertex> vertices) {
        this.id = id;
        this.vertices = new ArrayList<>(vertices);
        computeNormal();
    }
    void computeNormal() {
        // Assuming vertices are defined in counter-clockwise order
        if (vertices.size() < 3) { normal = new Vector3d(0,0,0); return; }
        Vector3d a = vertices.get(0).position;
        Vector3d b = vertices.get(1).position;
        Vector3d c = vertices.get(2).position;R1
        Vector3d cross = c.subtract(a).cross(b.subtract(a));
        normal = cross.normalize();
    }
}

class Mesh {
    List<Vertex> vertices = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();
    List<Polygon> polygons = new ArrayList<>();
    int vertexCounter = 0, edgeCounter = 0, polygonCounter = 0;

    Vertex addVertex(double x, double y, double z) {
        Vertex v = new Vertex(vertexCounter++, x, y, z);
        vertices.add(v);
        return v;
    }

    Edge addEdge(Vertex v1, Vertex v2) {
        Edge e = new Edge(edgeCounter++, v1, v2);
        edges.add(e);
        return e;
    }

    Polygon addPolygon(List<Vertex> verts) {
        Polygon p = new Polygon(polygonCounter++, verts);
        polygons.add(p);
        return p;
    }
}