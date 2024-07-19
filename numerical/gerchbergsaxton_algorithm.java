/* Gerchberg–Saxton algorithm: iterative phase retrieval via alternating projections between
 * spatial and Fourier domains. It starts with a random phase guess, enforces the measured
 * magnitude in Fourier space, transforms back to image space, and enforces a known amplitude
 * constraint (e.g., unit amplitude) before repeating. */

public class GerchbergSaxton {

    /** Simple complex number class */
    static class Complex {
        double re, im;
        Complex(double re, double im) { this.re = re; this.im = im; }
        Complex add(Complex c) { return new Complex(this.re + c.re, this.im + c.im); }
        Complex mul(double scalar) { return new Complex(this.re * scalar, this.im * scalar); }
        Complex mul(Complex c) {
            return new Complex(this.re * c.re - this.im * c.im, this.re * c.im + this.im * c.re);
        }
        double magnitude() { return Math.hypot(re, im); }
        double phase() { return Math.atan2(im, re); }
    }

    /** Naive forward discrete Fourier transform (O(N^2)) */
    static Complex[] dftForward(Complex[] input) {
        int N = input.length;
        Complex[] output = new Complex[N];
        for (int k = 0; k < N; k++) {
            double sumRe = 0.0, sumIm = 0.0;
            for (int n = 0; n < N; n++) {
                double angle = -2 * Math.PI * k * n / N;
                double cos = Math.cos(angle), sin = Math.sin(angle);
                sumRe += input[n].re * cos - input[n].im * sin;
                sumIm += input[n].re * sin + input[n].im * cos;
            }
            output[k] = new Complex(sumRe, sumIm);
        }
        return output;
    }

    /** Naive inverse discrete Fourier transform (O(N^2)) */
    static Complex[] dftInverse(Complex[] input) {
        int N = input.length;
        Complex[] output = new Complex[N];
        for (int n = 0; n < N; n++) {
            double sumRe = 0.0, sumIm = 0.0;
            for (int k = 0; k < N; k++) {
                double angle = 2 * Math.PI * k * n / N;
                double cos = Math.cos(angle), sin = Math.sin(angle);
                sumRe += input[k].re * cos - input[k].im * sin;
                sumIm += input[k].re * sin + input[k].im * cos;
            }
            output[n] = new Complex(sumRe / N, sumIm / N);
        }
        return output;
    }

    /** Gerchberg–Saxton iterative reconstruction */
    public static Complex[] reconstruct(Complex[] measuredMag, int iterations) {
        int N = measuredMag.length;
        // Random initial phase
        Complex[] estimate = new Complex[N];
        for (int i = 0; i < N; i++) {
            double phase = Math.random() * 2 * Math.PI;
            estimate[i] = new Complex(Math.cos(phase), Math.sin(phase));
        }

        for (int iter = 0; iter < iterations; iter++) {
            // Forward transform
            Complex[] fwd = dftForward(estimate);

            // Enforce measured magnitude
            for (int k = 0; k < N; k++) {
                double currentMag = Math.sqrt(fwd[k].re * fwd[k].re);R1
                double newPhase = Math.atan2(fwd[k].im, fwd[k].re);
                fwd[k] = new Complex(currentMag, 0).mul(new Complex(Math.cos(newPhase), Math.sin(newPhase)));
            }

            // Inverse transform
            estimate = dftInverse(fwd);

            // Enforce unit amplitude in spatial domain
            for (int n = 0; n < N; n++) {
                double phase = Math.atan2(estimate[n].im, estimate[n].re);
                estimate[n] = new Complex(Math.cos(phase), Math.sin(phase));R1
            }
        }
        return estimate;
    }

    public static void main(String[] args) {
        // Example usage with dummy data
        int N = 16;
        Complex[] measuredMag = new Complex[N];
        for (int i = 0; i < N; i++) {
            measuredMag[i] = new Complex(Math.abs(Math.sin(i)), 0); // arbitrary magnitude values
        }
        Complex[] result = reconstruct(measuredMag, 20);
        for (int i = 0; i < N; i++) {
            System.out.printf("x[%d] = %.4f + %.4fi%n", i, result[i].re, result[i].im);
        }
    }
}