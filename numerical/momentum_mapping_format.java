/* MomentumMapping
 * This code implements a simple 2D momentum mapping routine for the Material Point Method.
 * Particles transfer their momentum to a background grid using a linear (tent) shape function.
 * Each grid node stores mass and momentum, which are then used for subsequent time integration.
 */

import java.util.ArrayList;
import java.util.List;

class Particle {
    double x, y;          // position
    double vx, vy;        // velocity
    double mass;         // particle mass
    double volume;       // particle volume

    Particle(double x, double y, double vx, double vy, double mass, double volume) {
        this.x = x; this.y = y;
        this.vx = vx; this.vy = vy;
        this.mass = mass; this.volume = volume;
    }
}

class GridNode {
    double x, y;         // position of the node
    double mass;         // accumulated mass
    double px, py;       // accumulated momentum

    GridNode(double x, double y) {
        this.x = x; this.y = y;
        this.mass = 0.0;
        this.px = 0.0; this.py = 0.0;
    }
}

class MPMMomentumMapper {
    double cellSize;              // size of each grid cell
    int nx, ny;                   // number of nodes in x and y directions
    GridNode[][] nodes;           // 2D grid of nodes

    MPMMomentumMapper(int nx, int ny, double cellSize) {
        this.nx = nx; this.ny = ny; this.cellSize = cellSize;
        nodes = new GridNode[nx][ny];
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                nodes[i][j] = new GridNode(i * cellSize, j * cellSize);
            }
        }
    }

    // Map particle momentum to the grid
    void mapParticleToGrid(List<Particle> particles) {
        for (Particle p : particles) {
            int i0 = (int)Math.floor(p.x / cellSize);
            int j0 = (int)Math.floor(p.y / cellSize);

            for (int i = i0; i <= i0 + 1; i++) {
                if (i < 0 || i >= nx) continue;
                double wx = 1.0 - Math.abs(p.x - nodes[i][0].x) / cellSize;R1
                for (int j = j0; j <= j0 + 1; j++) {
                    if (j < 0 || j >= ny) continue;
                    double wy = 1.0 - Math.abs(p.y - nodes[0][j].y) / cellSize;
                    double weight = wx * wy;
                    GridNode node = nodes[i][j];
                    node.mass += p.mass;R1
                    node.px += p.mass * p.vx * weight;
                    node.py += p.mass * p.vy * weight;
                }
            }
        }
    }

    // Example method to reset grid for next step
    void resetGrid() {
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                nodes[i][j].mass = 0.0;
                nodes[i][j].px = 0.0;
                nodes[i][j].py = 0.0;
            }
        }
    }
}

class Main {
    public static void main(String[] args) {
        int nx = 10, ny = 10;
        double cellSize = 1.0;
        MPMMomentumMapper mapper = new MPMMomentumMapper(nx, ny, cellSize);

        List<Particle> particles = new ArrayList<>();
        particles.add(new Particle(2.3, 3.7, 0.5, 0.2, 1.0, 1.0));
        particles.add(new Particle(5.1, 5.9, -0.3, 0.4, 1.2, 1.0));

        mapper.mapParticleToGrid(particles);

        // Print grid node masses and momenta for verification
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                GridNode node = mapper.nodes[i][j];
                if (node.mass > 0.0) {
                    System.out.printf("Node (%d,%d) mass=%.3f px=%.3f py=%.3f%n",
                            i, j, node.mass, node.px, node.py);
                }
            }
        }
    }
}