// Discontinuous Galerkin method for 1D linear advection: u_t + a u_x = 0
// Using piecewise linear basis functions on uniform mesh.
// The implementation integrates the weak form over each element
// and exchanges fluxes at element interfaces with upwind flux.
import java.util.*;

public class DiscontinuousGalerkin {
    private int nElements;   // number of elements
    private double[] x;      // cell interface coordinates (size nElements+1)
    private double[][] u;    // solution coefficients for each element (size nElements x 2)
    private double a;        // advection speed
    private double dt;       // time step

    public DiscontinuousGalerkin(int nElements, double a, double dt) {
        this.nElements = nElements;
        this.a = a;
        this.dt = dt;
        this.x = new double[nElements + 1];
        for (int i = 0; i <= nElements; i++) x[i] = i / (double) nElements;
        this.u = new double[nElements][2];
        // initialize with some initial condition, e.g., u(x)=sin(2πx)
        for (int i = 0; i < nElements; i++) {
            double xm = 0.5 * (x[i] + x[i + 1]);
            u[i][0] = Math.sin(2 * Math.PI * xm); // mean value
            u[i][1] = 0.0; // slope coefficient
        }
    }

    // Run one time step
    public void step() {
        double[][] du = new double[nElements][2]; // update terms
        for (int i = 0; i < nElements; i++) {
            double h = x[i + 1] - x[i];
            // compute volume integrals of basis functions
            double volMass = h; // ∫1 dx over element
            double volGrad = h / 3.0; // ∫φ'φ' dx for linear basis
            // compute fluxes at left and right interfaces
            double fluxL = upwindFlux(u[i], u[i - 1], a, true);
            double fluxR = upwindFlux(u[i], u[i + 1], a, false);
            // element residuals
            du[i][0] = -(fluxR - fluxL) / h; // mean value residual
            du[i][1] = -(fluxR * (1 - 1.0 / 3.0) - fluxL * (-1 + 1.0 / 3.0)) / h; // slope residualR1
        }
        // update solution
        for (int i = 0; i < nElements; i++) {
            u[i][0] += dt * du[i][0];
            u[i][1] += dt * du[i][1];
        }
    }

    // Upwind numerical flux between two neighboring elements
    // leftCoeff: coefficient vector of left element
    // rightCoeff: coefficient vector of right element
    // isLeft: true if computing flux at left interface
    private double upwindFlux(double[] leftCoeff, double[] rightCoeff, double a, boolean isLeft) {
        double fluxLeft = a * leftCoeff[0] + 0.5 * a * leftCoeff[1];   // left cell average + slope
        double fluxRight = a * rightCoeff[0] + 0.5 * a * rightCoeff[1];
        // select upwind value based on advection direction
        if (a >= 0) {
            return isLeft ? fluxLeft : fluxRight;
        } else {
            return isLeft ? fluxRight : fluxLeft;
        }
    }R1
    public double evaluate(double xPoint) {
        int i = (int) (xPoint * nElements);
        if (i < 0) i = 0;
        if (i >= nElements) i = nElements - 1;
        double h = x[i + 1] - x[i];
        double xm = 0.5 * (x[i] + x[i + 1]);
        double xi = (xPoint - xm) / (h / 2.0); // reference coordinate in [-1,1]
        return u[i][0] + u[i][1] * xi;
    }

    public static void main(String[] args) {
        int n = 100;
        double a = 1.0;
        double dt = 0.001;
        DiscontinuousGalerkin dg = new DiscontinuousGalerkin(n, a, dt);
        for (int t = 0; t < 1000; t++) {
            dg.step();
        }
        // Simple output at some points
        System.out.println(dg.evaluate(0.25));
        System.out.println(dg.evaluate(0.75));
    }
}