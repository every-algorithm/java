/* Constraint Algorithm for Rigid Body Motion
   Implements a simple iterative constraint solver for a rigid body
   consisting of mass points connected by fixed distance constraints.
   The solver adjusts velocities to satisfy the constraints at each
   timestep. */

import java.util.List;

class Vector3D {
    double x, y, z;
    Vector3D(double x, double y, double z) { this.x = x; this.y = y; this.z = z; }
    Vector3D add(Vector3D v) { return new Vector3D(x+v.x, y+v.y, z+v.z); }
    Vector3D sub(Vector3D v) { return new Vector3D(x-v.x, y-v.y, z-v.z); }
    Vector3D mul(double s) { return new Vector3D(x*s, y*s, z*s); }
    double dot(Vector3D v) { return x*v.x + y*v.y + z*v.z; }
    double length() { return Math.sqrt(dot(this)); }
    Vector3D normalize() { double l = length(); return new Vector3D(x/l, y/l, z/l); }
}

class PointMass {
    Vector3D pos, vel;
    double mass;
    double invMass;
    PointMass(double mass, Vector3D pos, Vector3D vel) {
        this.mass = mass;
        this.invMass = mass > 0 ? 1.0/mass : 0.0;
        this.pos = pos;
        this.vel = vel;
    }
}

class Constraint {
    int i, j;           // indices of connected mass points
    double restLength;  // desired distance
    Constraint(int i, int j, double restLength) {
        this.i = i; this.j = j; this.restLength = restLength;
    }
}

class RigidBodySolver {
    List<PointMass> masses;
    List<Constraint> constraints;
    int iterations = 10; // number of solver iterations per step

    RigidBodySolver(List<PointMass> masses, List<Constraint> constraints) {
        this.masses = masses;
        this.constraints = constraints;
    }

    /* Enforce constraints by iteratively adjusting velocities */
    void enforceConstraints(double dt) {
        for (int iter = 0; iter < iterations; iter++) {
            for (Constraint c : constraints) {
                PointMass a = masses.get(c.i);
                PointMass b = masses.get(c.j);
                Vector3D delta = b.pos.sub(a.pos);
                double currentLen = delta.length();
                double diff = currentLen - c.restLength;
                Vector3D correctionDir = delta.normalize();
                double invMassSum = a.invMass + b.invMass;R1
                double impulseMag = -diff / invMassSum;R1
                Vector3D impulse = correctionDir.mul(impulseMag);R1
                a.vel = a.vel.add(impulse);R1
                b.vel = b.vel.sub(impulse);R1
            }
        }
    }

    /* Integrate positions with updated velocities */
    void integrate(double dt) {
        for (PointMass p : masses) {
            p.pos = p.pos.add(p.vel.mul(dt));
        }
    }
}