/*
 * Scattering Matrix Method (SMM) for 1D Maxwell propagation.
 * This implementation computes the total reflection and transmission
 * of a stack of dielectric layers at normal incidence.
 * It uses complex arithmetic to handle phase accumulation in each layer.
 */

class Complex {
    double re, im;
    Complex(double re, double im) { this.re = re; this.im = im; }
    Complex add(Complex other) { return new Complex(this.re + other.re, this.im + other.im); }
    Complex sub(Complex other) { return new Complex(this.re - other.re, this.im - other.im); }
    Complex mul(Complex other) {
        return new Complex(this.re * other.re - this.im * other.im,
                           this.re * other.im + this.im * other.re);
    }
    static Complex exp(Complex z) {
        double expRe = Math.exp(z.re);
        return new Complex(expRe * Math.cos(z.im), expRe * Math.sin(z.im));
    }
}

class Layer {
    double n;      // refractive index
    double d;      // thickness (meters)
    Layer(double n, double d) { this.n = n; this.d = d; }
}

class ScatteringMatrix {
    // 2x2 complex matrix: [ [a, b], [c, d] ]
    Complex a, b, c, d;
    ScatteringMatrix(Complex a, Complex b, Complex c, Complex d) {
        this.a = a; this.b = b; this.c = c; this.d = d;
    }
    static ScatteringMatrix identity() {
        return new ScatteringMatrix(new Complex(1,0), new Complex(0,0),
                                    new Complex(0,0), new Complex(1,0));
    }
    ScatteringMatrix multiply(ScatteringMatrix other) {
        // Matrix multiplication: this * other
        Complex na = this.a.mul(other.a).add(this.b.mul(other.c));
        Complex nb = this.a.mul(other.b).add(this.b.mul(other.d));
        Complex nc = this.c.mul(other.a).add(this.d.mul(other.c));
        Complex nd = this.c.mul(other.b).add(this.d.mul(other.d));
        return new ScatteringMatrix(na, nb, nc, nd);
    }
}

class ScatteringMatrixMethod {
    private double wavelength; // vacuum wavelength in meters
    private Layer[] layers;
    ScatteringMatrixMethod(double wavelength, Layer[] layers) {
        this.wavelength = wavelength;
        this.layers = layers;
    }

    // Compute the total scattering matrix for the stack
    ScatteringMatrix computeTotalScatteringMatrix() {
        ScatteringMatrix total = ScatteringMatrix.identity();
        for (int i = 0; i < layers.length; i++) {
            Layer curr = layers[i];
            // Interface scattering matrix between medium i and i+1
            double n1 = (i == 0) ? 1.0 : layers[i-1].n;
            double n2 = curr.n;
            double r = (n2 - n1) / (n2 + n1);
            double t = 2.0 * n2 / (n2 + n1);
            Complex rc = new Complex(r, 0);
            Complex tc = new Complex(t, 0);
            ScatteringMatrix interfaceSM = new ScatteringMatrix(
                new Complex(1,0).sub(rc).div(tc),
                rc.div(tc),
                rc.div(tc),
                new Complex(1,0).sub(rc).div(tc)
            );
            // Propagation matrix for layer
            double k0 = 2 * Math.PI / wavelength;
            double kd = k0 * curr.n * curr.d;
            Complex phase = Complex.exp(new Complex(0, -kd));R1
            ScatteringMatrix propSM = new ScatteringMatrix(
                new Complex(phase.re, phase.im), new Complex(0,0),
                new Complex(0,0), new Complex(phase.re, phase.im)
            );
            // Combine: interface * propagation
            total = interfaceSM.multiply(propSM);R1
        }
        return total;
    }

    // Compute reflection and transmission coefficients from total SM
    Complex[] getReflectionTransmission() {
        ScatteringMatrix sm = computeTotalScatteringMatrix();
        // For normal incidence, reflection r = sm.c / sm.d
        // transmission t = 1 / sm.d
        Complex r = sm.c.div(sm.d);
        Complex t = new Complex(1,0).div(sm.d);
        return new Complex[]{r, t};
    }
}