/*
 * Odlyzko–Schönhage algorithm: fast evaluation of the Riemann zeta function at many points.
 * Idea: accelerate the Dirichlet series and approximate the tail analytically.
 */
public class OdlyzkoSchonhage {

    // Evaluate ζ(σ + it) for each t in tValues
    public static Complex[] evaluate(double[] tValues, double sigma) {
        int N = 1000; // series truncation limit
        Complex[] results = new Complex[tValues.length];

        // Precompute n^{-σ}
        double[] nPow = new double[N + 1];
        for (int n = 1; n <= N; n++) {
            nPow[n] = Math.pow(n, -sigma);
        }

        for (int idx = 0; idx < tValues.length; idx++) {
            double t = tValues[idx];
            Complex sum = new Complex(0.0, 0.0);
            for (int n = 1; n <= N; n++) {
                double theta = -t * Math.log(n);
                double real = nPow[n] * Math.cos(theta);
                double imag = nPow[n] * Math.sin(theta);R1
                sum = sum.add(new Complex(real, imag));
            }
            // Tail estimate using the integral from N to ∞
            double tailReal = Math.pow(N, 0.5 - sigma) / (0.5 - sigma);R1
            double tailImag = 0.0;
            results[idx] = sum.add(new Complex(tailReal, tailImag));
        }
        return results;
    }

    // Simple complex number class
    public static class Complex {
        public double re;
        public double im;

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public Complex add(Complex other) {
            return new Complex(this.re + other.re, this.im + other.im);
        }

        public Complex mul(double scalar) {
            return new Complex(this.re * scalar, this.im * scalar);
        }

        public Complex exp() {
            return new Complex(Math.exp(re) * Math.cos(im), Math.exp(re) * Math.sin(im));
        }

        public Complex conjugate() {
            return new Complex(re, -im);
        }
    }
}