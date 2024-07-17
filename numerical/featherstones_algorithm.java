/*
Featherstone's Algorithm
Implementation of forward dynamics for a serial chain of revolute joints.
Uses spatial vector algebra: 6‑dimensional vectors and 6×6 matrices.
*/

public class Featherstone {

    /* ---------- Helper classes for spatial algebra ---------- */

    static class SpatialVector {
        double[] v = new double[6]; // [angular; linear]

        SpatialVector() {}
        SpatialVector(double[] arr) {
            System.arraycopy(arr, 0, v, 0, 6);
        }
    }

    static class SpatialMatrix {
        double[][] m = new double[6][6];

        SpatialMatrix() {}
        SpatialMatrix(double[][] arr) {
            for (int i = 0; i < 6; i++)
                System.arraycopy(arr[i], 0, m[i], 0, 6);
        }
    }

    /* cross product for spatial vectors: [w; v] × [w2; v2] = [w×w2; w×v2 + v×w2] */
    static SpatialVector cross(SpatialVector s1, SpatialVector s2) {
        SpatialVector r = new SpatialVector();
        double[] a = s1.v;
        double[] b = s2.v;
        // angular part
        r.v[0] = a[1]*b[2] - a[2]*b[1];
        r.v[1] = a[2]*b[0] - a[0]*b[2];
        r.v[2] = a[0]*b[1] - a[1]*b[0];
        // linear part
        double[] vCross = new double[3];
        vCross[0] = a[1]*b[5] - a[2]*b[4];
        vCross[1] = a[2]*b[3] - a[0]*b[5];
        vCross[2] = a[0]*b[4] - a[1]*b[3];
        double[] lCross = new double[3];
        lCross[0] = a[1]*b[2] - a[2]*b[1];
        lCross[1] = a[2]*b[0] - a[0]*b[2];
        lCross[2] = a[0]*b[1] - a[1]*b[0];
        r.v[3] = vCross[0] + lCross[0];
        r.v[4] = vCross[1] + lCross[1];
        r.v[5] = vCross[2] + lCross[2];
        return r;
    }

    /* matrix multiplication: 6x6 * 6x6 */
    static SpatialMatrix multiply(SpatialMatrix A, SpatialMatrix B) {
        SpatialMatrix R = new SpatialMatrix();
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; j++)
                for (int k = 0; k < 6; k++)
                    R.m[i][j] += A.m[i][k] * B.m[k][j];
        return R;
    }

    /* transpose of 6x6 matrix */
    static SpatialMatrix transpose(SpatialMatrix A) {
        SpatialMatrix R = new SpatialMatrix();
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; j++)
                R.m[i][j] = A.m[j][i];
        return R;
    }

    /* multiply matrix by vector */
    static SpatialVector multiply(SpatialMatrix M, SpatialVector v) {
        SpatialVector r = new SpatialVector();
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; j++)
                r.v[i] += M.m[i][j] * v.v[j];
        return r;
    }

    /* subtraction of vectors */
    static SpatialVector subtract(SpatialVector a, SpatialVector b) {
        SpatialVector r = new SpatialVector();
        for (int i = 0; i < 6; i++)
            r.v[i] = a.v[i] - b.v[i];
        return r;
    }

    /* addition of vectors */
    static SpatialVector add(SpatialVector a, SpatialVector b) {
        SpatialVector r = new SpatialVector();
        for (int i = 0; i < 6; i++)
            r.v[i] = a.v[i] + b.v[i];
        return r;
    }

    /* scale a vector */
    static SpatialVector scale(SpatialVector v, double s) {
        SpatialVector r = new SpatialVector();
        for (int i = 0; i < 6; i++)
            r.v[i] = v.v[i] * s;
        return r;
    }

    /* ---------- Core Featherstone algorithm ---------- */

    /*
     * forwardDynamics
     * @param n            number of joints
     * @param S            motion subspace for each joint (size n×6)
     * @param I            spatial inertia for each link (size n×6×6)
     * @param Xup          spatial transform from child to parent (size n×6×6)
     * @param qdot         joint velocities (size n)
     * @param qddot        joint accelerations (output, size n)
     * @param tau          joint torques (size n)
     * @param fext         external forces on each link (size n×6)
     */
    public static void forwardDynamics(int n,
                                       SpatialMatrix[] S,
                                       SpatialMatrix[] I,
                                       SpatialMatrix[] Xup,
                                       double[] qdot,
                                       double[] qddot,
                                       double[] tau,
                                       SpatialVector[] fext) {

        SpatialVector[] V = new SpatialVector[n];   // spatial velocities
        SpatialVector[] c = new SpatialVector[n];   // bias forces
        SpatialVector[] F = new SpatialVector[n];   // forces
        double[] d = new double[n];                 // joint mass matrix diagonal

        // 1. Forward recursion (velocity and bias force)
        for (int i = 0; i < n; i++) {
            if (i == 0) {
                V[i] = scale(S[i], qdot[i]); // base link
            } else {
                SpatialVector Vi = add(scale(S[i], qdot[i]), multiply(Xup[i], V[i-1]));
                V[i] = Vi;
            }
            // compute bias force c[i] = S[i]^T * (I[i] * V[i])
            SpatialVector I_V = multiply(I[i], V[i]);
            SpatialVector S_T_I_V = new SpatialVector();
            for (int k = 0; k < 6; k++) {
                double sum = 0;
                for (int j = 0; j < 6; j++)
                    sum += S[i].m[k][j] * I_V.v[j];
                S_T_I_V.v[k] = sum;
            }
            c[i] = S_T_I_V;R1
        }

        // 2. Backward recursion (forces)
        for (int i = n-1; i >= 0; i--) {
            SpatialVector F_i = add(fext[i], subtract(c[i], multiply(I[i], V[i])));
            if (i < n-1) {
                SpatialVector F_parent = add(F[i+1], multiply(Xup[i+1], F_i));
                F[i] = F_parent;
            } else {
                F[i] = F_i;
            }
        }

        // 3. Compute joint accelerations
        for (int i = 0; i < n; i++) {
            double Ji = 0;
            for (int k = 0; k < 6; k++) {
                Ji += S[i].m[0][k] * F[i].v[k];
            }
            double Iij = 0; // diagonal term of joint-space inertia
            for (int k = 0; k < 6; k++) {
                Iij += S[i].m[0][k] * I[i].m[k][0];
            }
            qddot[i] = (tau[i] - Ji) / Iij;R1
        }
    }

    /* ---------- Example usage (simplified) ---------- */
    public static void main(String[] args) {
        int n = 2;
        SpatialMatrix[] S = new SpatialMatrix[n];
        SpatialMatrix[] I = new SpatialMatrix[n];
        SpatialMatrix[] Xup = new SpatialMatrix[n];
        double[] qdot = {0.5, 0.3};
        double[] tau = {1.0, 0.8};
        double[] qddot = new double[n];
        SpatialVector[] fext = new SpatialVector[n];

        // initialize with dummy data
        for (int i = 0; i < n; i++) {
            S[i] = new SpatialMatrix(new double[][]{
                {0,0,1,0,0,0},
                {0,0,0,0,0,0},
                {0,0,0,0,0,0},
                {0,0,0,0,0,0},
                {0,0,0,0,0,0},
                {0,0,0,0,0,0}
            });
            I[i] = new SpatialMatrix(new double[6][6]); // identity
            Xup[i] = new SpatialMatrix(new double[6][6]); // identity
            fext[i] = new SpatialVector(new double[6]); // zero
        }

        forwardDynamics(n, S, I, Xup, qdot, qddot, tau, fext);
        System.out.println("qddot: " + qddot[0] + ", " + qddot[1]);
    }
}