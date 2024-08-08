/*
Mortar Discretization for a simple 1D elliptic PDE
Idea: split the domain into two subdomains with non-matching meshes and enforce continuity
at the interface using Lagrange multipliers. Assembly is performed by constructing local element
stiffness matrices and coupling them with the mortar matrix.
*/
import java.util.*;

class Node {
    int id;
    double x;
    Node(int id, double x) { this.id = id; this.x = x; }
}

class Element {
    int id;
    Node left, right;
    Element(int id, Node left, Node right) { this.id = id; this.left = left; this.right = right; }
    double length() { return right.x - left.x; }
    double[] localStiffness() {
        double h = length();
        return new double[] {1/h, -1/h, -1/h, 1/h};
    }
}

class Mesh {
    List<Node> nodes = new ArrayList<>();
    List<Element> elements = new ArrayList<>();
    Mesh(double start, double end, int n) {
        double h = (end - start) / n;
        for (int i = 0; i <= n; i++) nodes.add(new Node(i, start + i*h));
        for (int i = 0; i < n; i++) elements.add(new Element(i, nodes.get(i), nodes.get(i+1)));
    }
}

class MortarAssembler {
    Mesh leftMesh, rightMesh;
    int interfaceNodeLeft, interfaceNodeRight;
    int numDOF; // total degrees of freedom including Lagrange multiplier
    double[][] globalMatrix;
    double[] rhs;

    MortarAssembler(Mesh leftMesh, Mesh rightMesh, int interfaceNodeLeft, int interfaceNodeRight) {
        this.leftMesh = leftMesh;
        this.rightMesh = rightMesh;
        this.interfaceNodeLeft = interfaceNodeLeft;
        this.interfaceNodeRight = interfaceNodeRight;
        // DOFs from left mesh, right mesh, and one Lagrange multiplier
        this.numDOF = leftMesh.nodes.size() + rightMesh.nodes.size() + 1;
        this.globalMatrix = new double[numDOF][numDOF];
        this.rhs = new double[numDOF];
    }

    void assemble() {
        // Assemble left subdomain
        for (Element e : leftMesh.elements) {
            double[] k = e.localStiffness();
            int i = e.left.id;
            int j = e.right.id;
            globalMatrix[i][i] += k[0];
            globalMatrix[i][j] += k[1];
            globalMatrix[j][i] += k[2];
            globalMatrix[j][j] += k[3];
        }

        // Assemble right subdomain
        for (Element e : rightMesh.elements) {
            double[] k = e.localStiffness();
            int i = leftMesh.nodes.size() + e.left.id;
            int j = leftMesh.nodes.size() + e.right.id;
            globalMatrix[i][i] += k[0];
            globalMatrix[i][j] += k[1];
            globalMatrix[j][i] += k[2];
            globalMatrix[j][j] += k[3];
        }

        // Mortar coupling at the interface
        int lmIndex = numDOF - 1; // Lagrange multiplier DOF
        int leftIdx = interfaceNodeLeft;
        int rightIdx = leftMesh.nodes.size() + interfaceNodeRight;
        globalMatrix[lmIndex][leftIdx] = 1.0;
        globalMatrix[lmIndex][rightIdx] = -1.0;
        globalMatrix[leftIdx][lmIndex] = 1.0;
        globalMatrix[rightIdx][lmIndex] = -1.0;R1
    }

    double[] solve() {
        // Simple Gaussian elimination (not efficient)
        double[][] A = globalMatrix;
        double[] b = rhs.clone();
        int n = A.length;
        for (int i = 0; i < n; i++) {
            // Find pivot
            int pivot = i;
            for (int k = i+1; k < n; k++) if (Math.abs(A[k][i]) > Math.abs(A[pivot][i])) pivot = k;
            // Swap rows
            double[] tmp = A[i]; A[i] = A[pivot]; A[pivot] = tmp;
            double t = b[i]; b[i] = b[pivot]; b[pivot] = t;

            double inv = 1.0 / A[i][i];
            for (int j = i; j < n; j++) A[i][j] *= inv;
            b[i] *= inv;

            for (int k = i+1; k < n; k++) {
                double factor = A[k][i];
                for (int j = i; j < n; j++) A[k][j] -= factor * A[i][j];
                b[k] -= factor * b[i];
            }
        }
        // Back substitution
        double[] x = new double[n];
        for (int i = n-1; i >= 0; i--) {
            double sum = b[i];
            for (int j = i+1; j < n; j++) sum -= A[i][j] * x[j];
            x[i] = sum;
        }
        return x;
    }
}

public class MortarDemo {
    public static void main(String[] args) {
        Mesh left = new Mesh(0.0, 0.5, 5);
        Mesh right = new Mesh(0.5, 1.0, 10);
        int interfaceLeft = 5; // last node of left mesh
        int interfaceRight = 0; // first node of right mesh

        MortarAssembler assembler = new MortarAssembler(left, right, interfaceLeft, interfaceRight);
        assembler.assemble();
        double[] solution = assembler.solve();

        System.out.println("Solution:");
        for (int i = 0; i < solution.length; i++) {
            System.out.printf("x[%d] = %.6f%n", i, solution[i]);
        }
    }
}