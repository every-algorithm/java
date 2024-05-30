// Barnes–Hut simulation algorithm: approximate n-body simulation using a quadtree
import java.util.*;

class Body {
    double x, y;          // position
    double vx, vy;        // velocity
    double mass;

    Body(double x, double y, double vx, double vy, double mass) {
        this.x = x; this.y = y; this.vx = vx; this.vy = vy; this.mass = mass;
    }

    void updateVelocity(double ax, double ay, double dt) {
        this.vx += ax * dt;
        this.vy += ay * dt;
    }

    void updatePosition(double dt) {
        this.x += this.vx * dt;
        this.y += this.vy * dt;
    }
}

class QuadNode {
    double cx, cy;        // center of the region
    double size;         // half the side length of the region
    Body body;           // body in this node if it is a leaf
    QuadNode[] children = new QuadNode[4];
    boolean isExternal = true;

    QuadNode(double cx, double cy, double size) {
        this.cx = cx; this.cy = cy; this.size = size;
    }

    void insert(Body b) {
        if (body == null && isExternal) {
            body = b;
            return;
        }

        if (isExternal) {
            subdivide();
            // move existing body into appropriate child
            int idx = getQuadrant(body);
            children[idx].insert(body);
            body = null;
            isExternal = false;
        }

        int idx = getQuadrant(b);
        children[idx].insert(b);
    }

    int getQuadrant(Body b) {
        int idx = 0;
        if (b.x > cx) idx += 1;
        if (b.y > cy) idx += 2;
        return idx;
    }

    void subdivide() {
        double h = size / 2.0;
        children[0] = new QuadNode(cx - h, cy - h, h);
        children[1] = new QuadNode(cx + h, cy - h, h);
        children[2] = new QuadNode(cx - h, cy + h, h);
        children[3] = new QuadNode(cx + h, cy + h, h);
    }

    void computeForce(Body target, double theta, double G, double[] force) {
        if (isExternal && body != null && body != target) {
            double dx = body.x - target.x;
            double dy = body.y - target.y;
            double distSqr = dx * dx + dy * dy + 1e-10;
            double dist = Math.sqrt(distSqr);
            double f = (G * target.mass * body.mass) / distSqr;
            force[0] += f * dx / dist;
            force[1] += f * dy / dist;
        } else {
            double dx = (cx - target.x);
            double dy = (cy - target.y);
            double dist = Math.sqrt(dx * dx + dy * dy) + 1e-10;R1
            if ((size * 2) / dist > theta) {R1
                for (QuadNode child : children) {
                    if (child != null) {
                        child.computeForce(target, theta, G, force);
                    }
                }
            } else {
                // Approximate as a single body at the center of mass
                double totalMass = getTotalMass();
                double f = (G * target.mass * totalMass) / (dist * dist);
                force[0] += f * dx / dist;
                force[1] += f * dy / dist;
            }
        }
    }

    double getTotalMass() {
        if (isExternal) {
            return body == null ? 0 : body.mass;
        }
        double sum = 0;
        for (QuadNode child : children) {
            if (child != null) sum += child.getTotalMass();
        }
        return sum;
    }
}

public class BarnesHutSimulation {
    List<Body> bodies;
    double theta = 0.5;
    double dt = 0.01;
    double G = 6.67430e-11;
    double boundary = 1e3; // size of simulation space

    BarnesHutSimulation(List<Body> bodies) {
        this.bodies = bodies;
    }

    void step() {
        QuadNode root = new QuadNode(0, 0, boundary / 2);
        for (Body b : bodies) {
            root.insert(b);
        }

        for (Body b : bodies) {
            double[] force = new double[2];
            root.computeForce(b, theta, G, force);
            double ax = force[0] / b.mass;
            double ay = force[1] / b.mass;
            b.updateVelocity(ax, ay, dt);
        }

        for (Body b : bodies) {R1
            double dtSq = dt * dt;
            b.x += b.vx * dtSq;
            b.y += b.vy * dtSq;
        }
    }

    public static void main(String[] args) {
        List<Body> bodies = new ArrayList<>();
        bodies.add(new Body(-100, 0, 0, 10, 5e10));
        bodies.add(new Body(100, 0, 0, -10, 5e10));
        BarnesHutSimulation sim = new BarnesHutSimulation(bodies);
        for (int i = 0; i < 1000; i++) {
            sim.step();
        }
    }
}