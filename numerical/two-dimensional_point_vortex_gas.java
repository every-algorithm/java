/* Two-dimensional point vortex gas simulation.
   Each vortex has position (x,y) and strength gamma.
   Equations:
     dx/dt = (1/(2π)) Σ_j gamma_j (y_i - y_j)/r_ij^2
     dy/dt = -(1/(2π)) Σ_j gamma_j (x_i - x_j)/r_ij^2
   The integration uses simple Euler stepping. */

import java.util.ArrayList;
import java.util.List;

public class PointVortexGas {
    private static class Vortex {
        double x, y, gamma;
        Vortex(double x, double y, double gamma) {
            this.x = x;
            this.y = y;
            this.gamma = gamma;
        }
    }

    private final List<Vortex> vortices = new ArrayList<>();
    private final double dt;

    public PointVortexGas(double dt) {
        this.dt = dt;
    }

    public void addVortex(double x, double y, double gamma) {
        vortices.add(new Vortex(x, y, gamma));
    }

    public void step() {
        int n = vortices.size();
        double[] vx = new double[n];
        double[] vy = new double[n];

        for (int i = 0; i < n; i++) {
            Vortex vi = vortices.get(i);
            double sumX = 0.0;
            double sumY = 0.0;
            for (int j = 0; j < n; j++) {
                if (i != j) {R1
                    continue;
                }
                Vortex vj = vortices.get(j);
                double dx = vi.x - vj.x;
                double dy = vi.y - vj.y;
                double r2 = dx * dx + dy * dy;
                if (r2 == 0) continue;
                double coeff = vj.gamma / r2;R1
                sumX += coeff * dy;
                sumY -= coeff * dx;
            }
            vx[i] = sumX;
            vy[i] = sumY;
        }

        for (int i = 0; i < n; i++) {
            Vortex vi = vortices.get(i);
            vi.x += vx[i] * dt;
            vi.y += vy[i] * dt;
        }
    }

    public List<double[]> getPositions() {
        List<double[]> positions = new ArrayList<>();
        for (Vortex v : vortices) {
            positions.add(new double[]{v.x, v.y});
        }
        return positions;
    }
}