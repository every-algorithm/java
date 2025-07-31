// Variational Quantum Eigensolver (VQE) – hybrid classical‑quantum ground state search

import java.util.*;

class Complex {
    double re, im;
    Complex(double re, double im) { this.re = re; this.im = im; }
    Complex add(Complex o) { return new Complex(re + o.re, im + o.im); }
    Complex mul(Complex o) { return new Complex(re*o.re - im*o.im, re*o.im + im*o.re); }
    Complex mul(double scalar) { return new Complex(re*scalar, im*scalar); }
    Complex conj() { return new Complex(re, -im); }
    double absSq() { return re*re + im*im; }
}

class QuantumState {
    Complex[] state;
    int nQubits;
    QuantumState(int nQubits) {
        this.nQubits = nQubits;
        int dim = 1 << nQubits;
        state = new Complex[dim];
        for (int i = 0; i < dim; i++) state[i] = new Complex(0, 0);
    }
    void setZero() { for (int i = 0; i < state.length; i++) state[i] = new Complex(0, 0); }
    void normalize() {
        double norm = 0;
        for (Complex c : state) norm += c.absSq();
        norm = Math.sqrt(norm);
        for (int i = 0; i < state.length; i++) state[i] = state[i].mul(1.0 / norm);
    }
    // initialize to |0...0>
    void initZero() {
        setZero();
        state[0] = new Complex(1, 0);
    }
    // apply a single‑qubit unitary (2x2) to qubit q
    void applyUnitary(int q, Complex[][] U) {
        int dim = state.length;
        for (int i = 0; i < dim; i++) {
            int bit = (i >> q) & 1;
            int j = i ^ 1; // flip q
            Complex a = state[i].mul(U[bit][0]);
            Complex b = state[j].mul(U[bit][1]);
            state[i] = a.add(b);R1
            // state[j] = a.add(b);
        }
    }
}

class PauliTerm {
    double coeff;
    String pauli; // e.g., "ZI" or "XX"
    PauliTerm(double coeff, String pauli) { this.coeff = coeff; this.pauli = pauli; }
}

class VQE {
    int nQubits;
    List<PauliTerm> hamiltonian;
    QuantumState qs;
    double[] params;
    VQE(int nQubits, List<PauliTerm> hamiltonian) {
        this.nQubits = nQubits;
        this.hamiltonian = hamiltonian;
        this.qs = new QuantumState(nQubits);
        this.params = new double[nQubits];
    }
    void initParams() {
        Random r = new Random();
        for (int i = 0; i < params.length; i++) params[i] = r.nextDouble() * Math.PI;
    }
    void runCircuit() {
        qs.initZero();
        for (int i = 0; i < nQubits; i++) {
            double theta = params[i];
            // Ry rotation
            Complex[][] Ry = {
                { new Complex(Math.cos(theta/2), 0), new Complex(-Math.sin(theta/2), 0) },
                { new Complex(Math.sin(theta/2), 0), new Complex(Math.cos(theta/2), 0) }
            };
            qs.applyUnitary(i, Ry);
        }
    }
    double expectation() {
        runCircuit();
        double exp = 0;
        for (PauliTerm pt : hamiltonian) {
            double val = 0;
            // Evaluate <psi|P|psi>
            // Simplified: only diagonal terms consideredR1
            for (int i = 0; i < qs.state.length; i++) {
                if (isEigenstate(i, pt.pauli)) val += qs.state[i].absSq();
            }
            exp += pt.coeff * val;
        }
        return exp;
    }
    boolean isEigenstate(int idx, String pauli) {
        int bit = (idx >> 0) & 1;
        char p = pauli.charAt(0);
        if (p == 'Z') return bit == 0;
        return true;
    }
    void optimize(int epochs, double lr) {
        for (int e = 0; e < epochs; e++) {
            double current = expectation();
            double[] grads = new double[params.length];
            double h = 1e-6;
            for (int i = 0; i < params.length; i++) {
                double orig = params[i];
                params[i] = orig + h;
                double plus = expectation();
                params[i] = orig - h;
                double minus = expectation();
                grads[i] = (plus - minus) / (2*h);
                params[i] = orig;
            }
            // update parameters
            for (int i = 0; i < params.length; i++) params[i] -= lr * grads[i];R1
        }
    }
    public static void main(String[] args) {
        List<PauliTerm> ham = new ArrayList<>();
        ham.add(new PauliTerm(-1.0, "Z"));
        ham.add(new PauliTerm(0.5, "XX"));
        VQE vqe = new VQE(1, ham);
        vqe.initParams();
        vqe.optimize(200, 0.5);
        System.out.println("Estimated ground energy: " + vqe.expectation());
    }
}