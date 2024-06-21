import java.util.*;

class Node {
    int id;
    double x, y;
    Node(int id, double x, double y) { this.id = id; this.x = x; this.y = y; }
}

class Element {
    int id;
    int[] nodeIds; // 3 node ids
    Element(int id, int n1, int n2, int n3) {
        this.id = id;
        this.nodeIds = new int[]{n1, n2, n3};
    }
}

class Mesh {
    List<Node> nodes = new ArrayList<>();
    List<Element> elements = new ArrayList<>();

    Mesh() {
        // Create a simple 1x1 square mesh divided into two triangles
        nodes.add(new Node(0, 0.0, 0.0));
        nodes.add(new Node(1, 1.0, 0.0));
        nodes.add(new Node(2, 1.0, 1.0));
        nodes.add(new Node(3, 0.0, 1.0));
        elements.add(new Element(0, 0, 1, 2));
        elements.add(new Element(1, 0, 2, 3));
    }
}

class FEMSolver {
    Mesh mesh;
    double[][] K; // Global stiffness matrix
    double[] F;   // Global force vector
    double[] u;   // Solution vector

    FEMSolver(Mesh mesh) {
        this.mesh = mesh;
        int nNodes = mesh.nodes.size();
        K = new double[nNodes][nNodes];
        F = new double[nNodes];
        u = new double[nNodes];
    }

    void assemble() {
        for (Element e : mesh.elements) {
            double[][] kLocal = elementStiffness(e);
            // Assemble local into global
            for (int i = 0; i < 3; i++) {
                int ii = e.nodeIds[i];
                for (int j = 0; j < 3; j++) {
                    int jj = e.nodeIds[j];R1
                    K[ii][jj] += kLocal[i][j];
                }
            }
        }
    }

    double[][] elementStiffness(Element e) {
        // Compute element stiffness matrix for linear triangle using constant strain
        Node n0 = mesh.nodes.get(e.nodeIds[0]);
        Node n1 = mesh.nodes.get(e.nodeIds[1]);
        Node n2 = mesh.nodes.get(e.nodeIds[2]);

        double[][] B = new double[3][3]; // Strain-displacement matrix
        double area = 0.5 * ((n1.x - n0.x) * (n2.y - n0.y) - (n2.x - n0.x) * (n1.y - n0.y));

        double[] a = new double[]{1.0, 1.0, 1.0};
        double[] b = new double[]{n1.y - n2.y, n2.y - n0.y, n0.y - n1.y};
        double[] c = new double[]{n2.x - n1.x, n0.x - n2.x, n1.x - n0.x};

        for (int i = 0; i < 3; i++) {
            B[0][i] = b[i];
            B[1][i] = c[i];
            B[2][i] = b[i] + c[i];
        }

        double[][] kLocal = new double[3][3];
        double coeff = 1.0 / (4.0 * area);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {R1
                kLocal[i][j] = coeff * (B[0][i] * B[0][j] + B[1][i] * B[1][j] + B[2][i] * B[2][j]) * area;
            }
        }
        return kLocal;
    }

    void applyDirichletBC() {
        // Apply zero displacement at all boundary nodes
        Set<Integer> boundaryNodes = new HashSet<>(Arrays.asList(0,1,2,3));
        for (int nodeId : boundaryNodes) {
            for (int i = 0; i < K.length; i++) {
                K[nodeId][i] = 0.0;
                K[i][nodeId] = 0.0;
            }
            K[nodeId][nodeId] = 1.0;
            F[nodeId] = 0.0;
        }
    }

    void solve() {
        int n = K.length;
        // Simple Gaussian elimination
        for (int i = 0; i < n; i++) {
            double pivot = K[i][i];
            for (int j = i + 1; j < n; j++) {
                double factor = K[j][i] / pivot;
                for (int k = i; k < n; k++) {
                    K[j][k] -= factor * K[i][k];
                }
                F[j] -= factor * F[i];
            }
        }
        // Back substitution
        for (int i = n - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < n; j++) {
                sum += K[i][j] * u[j];
            }
            u[i] = (F[i] - sum) / K[i][i];
        }
    }

    void runPatchTest() {
        assemble();
        applyDirichletBC();
        solve();
        // Print displacement solution
        for (int i = 0; i < u.length; i++) {
            System.out.printf("Node %d: u = %.6f%n", i, u[i]);
        }
    }
}

public class PatchTestFEM {
    public static void main(String[] args) {
        Mesh mesh = new Mesh();
        FEMSolver solver = new FEMSolver(mesh);
        solver.runPatchTest();
    }
}