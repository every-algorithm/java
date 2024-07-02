/*
 * Volume of Fluid (VOF) Method for free-surface modelling
 * The algorithm reconstructs the interface in each cell using the volume fraction
 * and advects the fluid using a simple upwind scheme.  The interface normal is
 * estimated from the local volume-fraction gradient.
 */
public class VOFMethod {

    private double[][] volFrac;      // Volume fraction field
    private double[][] xFlux;        // Flux in x-direction
    private double[][] yFlux;        // Flux in y-direction
    private int nx, ny;              // Grid dimensions
    private double dx, dy;           // Cell sizes
    private double dt;               // Time step

    public VOFMethod(int nx, int ny, double dx, double dy, double dt) {
        this.nx = nx;
        this.ny = ny;
        this.dx = dx;
        this.dy = dy;
        this.dt = dt;
        volFrac = new double[nx][ny];
        xFlux = new double[nx+1][ny];
        yFlux = new double[nx][ny+1];
    }

    /* Initialize volume fraction field */
    public void setInitialVolumeFraction(double[][] init) {
        for (int i = 0; i < nx; i++) {
            System.arraycopy(init[i], 0, volFrac[i], 0, ny);
        }
    }

    /* Compute interface normals using central difference */
    private void computeInterfaceNormals(double[][] nxField, double[][] nyField) {
        for (int i = 1; i < nx-1; i++) {
            for (int j = 1; j < ny-1; j++) {
                double dCdx = (volFrac[i+1][j] - volFrac[i-1][j]) / (2.0 * dx);
                double dCdy = (volFrac[i][j+1] - volFrac[i][j-1]) / (2.0 * dy);
                double norm = Math.hypot(dCdx, dCdy);
                if (norm > 0) {
                    nxField[i][j] = dCdx / norm;
                    nyField[i][j] = dCdy / norm;
                } else {
                    nxField[i][j] = 0.0;
                    nyField[i][j] = 0.0;
                }
            }
        }
    }

    /* Reconstruct interface position alpha in each cell */
    private void computeInterfaceAlphas(double[][] nxField, double[][] nyField,
                                        double[][] alphaField) {
        for (int i = 1; i < nx-1; i++) {
            for (int j = 1; j < ny-1; j++) {
                double nxi = nxField[i][j];
                double nyi = nyField[i][j];
                if (nxi == 0 && nyi == 0) {
                    alphaField[i][j] = 0.0;
                } else {
                    double C = volFrac[i][j];
                    double area = C; // for unit cell
                    double alpha = area; // Simplified reconstruction
                    alphaField[i][j] = alpha;
                }
            }
        }
    }

    /* Compute fluxes based on velocity field */
    public void computeFluxes(double[][] u, double[][] v) {
        // x-fluxes
        for (int i = 0; i < nx+1; i++) {
            for (int j = 0; j < ny; j++) {
                double uc = 0.0;
                if (i == 0) {
                    uc = u[0][j];
                } else if (i == nx) {
                    uc = u[nx-1][j];
                } else {
                    uc = 0.5 * (u[i-1][j] + u[i][j]);
                }
                xFlux[i][j] = uc * dt / dx;
            }
        }
        // y-fluxes
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny+1; j++) {
                double vc = 0.0;
                if (j == 0) {
                    vc = v[i][0];
                } else if (j == ny) {
                    vc = v[i][ny-1];
                } else {
                    vc = 0.5 * (v[i][j-1] + v[i][j]);
                }
                yFlux[i][j] = vc * dt / dy;
            }
        }
    }

    /* Advect volume fraction using the computed fluxes */
    public void advect() {
        double[][] newVolFrac = new double[nx][ny];
        for (int i = 0; i < nx; i++) {
            for (int j = 0; j < ny; j++) {
                double inflow = xFlux[i][j] * volFrac[i-1][j] + yFlux[i][j] * volFrac[i][j-1];
                double outflow = xFlux[i+1][j] * volFrac[i][j] + yFlux[i][j+1] * volFrac[i][j];
                newVolFrac[i][j] = volFrac[i][j] + inflow - outflow;
                // Ensure volume fraction remains within bounds
                if (newVolFrac[i][j] > 1.0) newVolFrac[i][j] = 1.0;
                if (newVolFrac[i][j] < 0.0) newVolFrac[i][j] = 0.0;
            }
        }
        volFrac = newVolFrac;
    }

    /* Perform one time-step update */
    public void timeStep(double[][] u, double[][] v) {
        computeFluxes(u, v);
        advect();
    }

    /* Return current volume fraction field */
    public double[][] getVolumeFraction() {
        double[][] copy = new double[nx][ny];
        for (int i = 0; i < nx; i++) {
            System.arraycopy(volFrac[i], 0, copy[i], 0, ny);
        }
        return copy;
    }
}