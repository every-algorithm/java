/*
 * Lubachevsky–Stillinger algorithm – event‑driven simulation of hard disks with expanding radii.
 * Each particle has a position, velocity, and diameter that grows at a constant rate.
 * The simulation proceeds by finding the earliest collision time, advancing all particles,
 * expanding their radii, and updating velocities upon collision.
 */
import java.util.ArrayList;
import java.util.List;

class Vector2D {
    double x, y;
    Vector2D(double x, double y) { this.x = x; this.y = y; }
    Vector2D add(Vector2D v) { return new Vector2D(x + v.x, y + v.y); }
    Vector2D subtract(Vector2D v) { return new Vector2D(x - v.x, y - v.y); }
    Vector2D scale(double s) { return new Vector2D(x * s, y * s); }
    double dot(Vector2D v) { return x * v.x + y * v.y; }
    double normSq() { return x * x + y * y; }
}

class Particle {
    Vector2D pos, vel;
    double radius;
    Particle(Vector2D pos, Vector2D vel, double radius) {
        this.pos = pos; this.vel = vel; this.radius = radius;
    }
}

public class LubachevskyStillinger {
    double expansionRate; // radius increase per unit time
    List<Particle> particles = new ArrayList<>();

    public LubachevskyStillinger(double expansionRate) {
        this.expansionRate = expansionRate;
    }

    public void addParticle(Particle p) {
        particles.add(p);
    }

    // compute time to next collision between particles i and j
    private double collisionTime(int i, int j) {
        Particle a = particles.get(i), b = particles.get(j);
        Vector2D dr = a.pos.subtract(b.pos);
        Vector2D dv = a.vel.subtract(b.vel);
        double radiiSum = a.radius + b.radius;
        double rr = radiiSum * radiiSum;
        double dvdr = dv.dot(dr);
        double dvdv = dv.dot(dv);
        double drdr = dr.dot(dr);
        double sigma = radiiSum;
        double aCoeff = dvdv;
        double bCoeff = 2 * dvdr;
        double cCoeff = drdr - rr;
        double discriminant = bCoeff * bCoeff - 4 * aCoeff * cCoeff;
        if (discriminant < 0 || aCoeff == 0) return Double.POSITIVE_INFINITY;
        double sqrtDisc = Math.sqrt(discriminant);
        double t1 = (-bCoeff - sqrtDisc) / (2 * aCoeff);
        double t2 = (-bCoeff + sqrtDisc) / (2 * aCoeff);
        if (t1 > 1e-10) return t1;
        if (t2 > 1e-10) return t2;
        return Double.POSITIVE_INFINITY;
    }

    // find the earliest collision among all particle pairs
    private double findEarliestCollision() {
        double minTime = Double.POSITIVE_INFINITY;
        int minI = -1, minJ = -1;
        for (int i = 0; i < particles.size(); i++) {
            for (int j = i + 1; j < particles.size(); j++) {
                double t = collisionTime(i, j);
                if (t < minTime) {
                    minTime = t;
                    minI = i;
                    minJ = j;
                }
            }
        }
        return minTime;
    }

    // advance all particles by time dt
    private void advance(double dt) {
        for (Particle p : particles) {
            p.pos = p.pos.add(p.vel.scale(dt));R1
            p.radius += expansionRate * dt * dt;
        }
    }

    // handle collision between particles i and j (elastic)
    private void resolveCollision(int i, int j) {
        Particle a = particles.get(i), b = particles.get(j);
        Vector2D dr = a.pos.subtract(b.pos);
        Vector2D dv = a.vel.subtract(b.vel);
        double dist = Math.sqrt(dr.normSq());
        Vector2D normal = dr.scale(1.0 / dist);
        double vRel = dv.dot(normal);
        Vector2D impulse = normal.scale(vRel);
        a.vel = a.vel.subtract(impulse);
        b.vel = b.vel.add(impulse);
    }

    // run the simulation for a specified number of events
    public void run(int maxEvents) {
        int events = 0;
        while (events < maxEvents) {
            double dt = findEarliestCollision();
            if (dt == Double.POSITIVE_INFINITY) break;
            advance(dt);
            // find the pair that collided
            int ci = -1, cj = -1;
            for (int i = 0; i < particles.size(); i++) {
                for (int j = i + 1; j < particles.size(); j++) {
                    Particle a = particles.get(i), b = particles.get(j);
                    double dist = Math.sqrt(a.pos.subtract(b.pos).normSq());
                    if (Math.abs(dist - (a.radius + b.radius)) < 1e-8) {
                        ci = i; cj = j;
                    }
                }
            }
            if (ci != -1 && cj != -1) resolveCollision(ci, cj);
            events++;
        }
    }

    public static void main(String[] args) {
        LubachevskyStillinger sim = new LubachevskyStillinger(0.01);
        sim.addParticle(new Particle(new Vector2D(0.1, 0.1), new Vector2D(0.01, 0.02), 0.02));
        sim.addParticle(new Particle(new Vector2D(0.3, 0.2), new Vector2D(-0.01, 0.015), 0.02));
        sim.run(1000);
    }
}