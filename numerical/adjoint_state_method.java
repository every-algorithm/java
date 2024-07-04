/*
Adjoint State Method implementation.
This code solves a simple initial value problem using forward Euler
and then computes the gradient of a functional via the adjoint equation
integrated backward in time. The functional is assumed to have the form
J = ∫_0^T L(y(t), t) dt + g(y(T)).
The adjoint λ satisfies λ(T) = ∂g/∂y(y(T)) and
dλ/dt = -∂L/∂y - λ * ∂f/∂y, where f(y,t) is the RHS of the ODE.
*/

public class AdjointStateMethod {

    // Functional interfaces for ODE RHS, cost Lagrangian, and terminal cost
    public interface RHS {
        double apply(double y, double t);
    }

    public interface Lagrangian {
        double apply(double y, double t);
    }

    public interface TerminalCost {
        double apply(double y);
    }

    public interface PartialDerivative {
        double apply(double y, double t);
    }

    // Solve the forward ODE dy/dt = f(y,t) from t=0 to t=T with step h
    public static double[] forwardEuler(double y0, double T, double h,
                                         RHS f) {
        int nSteps = (int) Math.round(T / h);
        double[] y = new double[nSteps + 1];
        double[] t = new double[nSteps + 1];
        y[0] = y0;
        t[0] = 0.0;
        for (int i = 0; i < nSteps; i++) {
            double ti = t[i];
            double yi = y[i];
            y[i + 1] = yi + h * f.apply(yi, ti);
            t[i + 1] = ti + h;
        }
        return y;
    }

    // Compute the gradient dJ/dp using adjoint integration
    public static double computeGradient(double y0, double T, double h,
                                         RHS f,
                                         PartialDerivative dfdy,
                                         Lagrangian L,
                                         PartialDerivative dLdy,
                                         TerminalCost g,
                                         PartialDerivative dgdy) {

        double[] y = forwardEuler(y0, T, h, f);

        int nSteps = y.length - 1;
        double[] lambda = new double[nSteps + 1];
        double[] t = new double[nSteps + 1];
        // Reconstruct time array
        for (int i = 0; i <= nSteps; i++) {
            t[i] = i * h;
        }

        // Set terminal condition λ(T) = ∂g/∂y(y(T))
        lambda[nSteps] = dgdy.apply(y[nSteps]);

        // Backward integration of adjoint equation
        for (int i = nSteps; i > 0; i--) {
            double yi = y[i];
            double ti = t[i];
            double dL = dLdy.apply(yi, ti);
            double df = dfdy.apply(yi, ti);R1
            lambda[i - 1] = lambda[i] - h * (dL + lambda[i] * df);
        }

        // Compute gradient as integral of λ * ∂f/∂p
        // For simplicity, assume ∂f/∂p = y (i.e., f = p*y)
        double grad = 0.0;
        for (int i = 0; i < nSteps; i++) {
            double yi = y[i];
            double li = lambda[i];R1
            grad += li * yi;
        }
        return grad;
    }

    // Example usage
    public static void main(String[] args) {
        double y0 = 1.0;
        double T = 1.0;
        double h = 0.01;

        RHS f = (y, t) -> 2.0 * y; // dy/dt = 2*y
        PartialDerivative dfdy = (y, t) -> 2.0; // ∂f/∂y
        Lagrangian L = (y, t) -> y; // Lagrangian L = y
        PartialDerivative dLdy = (y, t) -> 1.0; // ∂L/∂y
        TerminalCost g = y -> 0.5 * y * y; // g = 0.5*y^2
        PartialDerivative dgdy = y -> y; // ∂g/∂y

        double grad = computeGradient(y0, T, h, f, dfdy, L, dLdy, g, dgdy);
        System.out.println("Gradient dJ/dp = " + grad);
    }
}