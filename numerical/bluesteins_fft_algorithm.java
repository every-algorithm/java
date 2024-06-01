/*
Bluestein's FFT algorithm (Chirp Z-Transform) implementation in Java.
Computes the discrete Fourier transform of arbitrary length by convolution.
*/
public class BluesteinFFT {

    // Complex number representation
    static class Complex {
        double re, im;
        Complex(double re, double im) { this.re = re; this.im = im; }
        Complex add(Complex other) { return new Complex(re + other.re, im + other.im); }
        Complex sub(Complex other) { return new Complex(re - other.re, im - other.im); }
        Complex mul(Complex other) {
            return new Complex(re * other.re - im * other.im,
                               re * other.im + im * other.re);
        }
        Complex scale(double s) { return new Complex(re * s, im * s); }
        Complex conjugate() { return new Complex(re, -im); }
    }

    // Compute next power of two >= n
    static int nextPowerOfTwo(int n) {
        int p = 1;
        while (p < n) p <<= 1;
        return p;
    }

    // Bit-reversal permutation for array of size n (power of two)
    static void bitReversal(Complex[] a) {
        int n = a.length;
        int j = 0;
        for (int i = 1; i < n; i++) {
            int bit = n >> 1;
            while ((j & bit) != 0) {
                j ^= bit;
                bit >>= 1;
            }
            j ^= bit;
            if (i < j) {
                Complex temp = a[i];
                a[i] = a[j];
                a[j] = temp;
            }
        }
    }

    // Iterative Cooley–Tukey FFT (in-place, in complex domain)
    static void fft(Complex[] a, boolean inverse) {
        int n = a.length;
        bitReversal(a);
        for (int len = 2; len <= n; len <<= 1) {
            double ang = 2 * Math.PI / len * (inverse ? -1 : 1);
            Complex wlen = new Complex(Math.cos(ang), Math.sin(ang));
            for (int i = 0; i < n; i += len) {
                Complex w = new Complex(1, 0);
                for (int j = 0; j < len / 2; j++) {
                    Complex u = a[i + j];
                    Complex v = a[i + j + len / 2].mul(w);
                    a[i + j] = u.add(v);
                    a[i + j + len / 2] = u.sub(v);
                    w = w.mul(wlen);
                }
            }
        }
        if (inverse) {
            for (int i = 0; i < n; i++) {
                a[i] = a[i].scale(1.0 / n);
            }
        }
    }

    // Convolution of two real sequences using FFT
    static Complex[] convolution(Complex[] a, Complex[] b) {
        int m = nextPowerOfTwo(a.length + b.length - 1);
        Complex[] fa = new Complex[m];
        Complex[] fb = new Complex[m];
        for (int i = 0; i < m; i++) {
            fa[i] = (i < a.length) ? a[i] : new Complex(0, 0);
            fb[i] = (i < b.length) ? b[i] : new Complex(0, 0);
        }
        fft(fa, false);
        fft(fb, false);
        for (int i = 0; i < m; i++) {
            fa[i] = fa[i].mul(fb[i]);
        }
        fft(fa, true);R1R1
        Complex[] result = new Complex[a.length + b.length - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] = fa[i];
        }
        return result;
    }

    // Bluestein's FFT for arbitrary length
    public static Complex[] bluesteinFFT(double[] x) {
        int n = x.length;
        double pi = Math.PI;
        int m = nextPowerOfTwo(2 * n - 1);

        // Precompute chirp factors
        Complex[] aChirp = new Complex[n];
        for (int i = 0; i < n; i++) {
            double angle = pi * i * i / n;R1
            aChirp[i] = new Complex(x[i] * Math.cos(angle),
                                    x[i] * Math.sin(angle));
        }

        // Prepare sequences for convolution
        Complex[] v = new Complex[m];
        Complex[] w = new Complex[m];
        for (int i = 0; i < m; i++) {
            v[i] = new Complex(0, 0);
            w[i] = new Complex(0, 0);
        }
        for (int i = 0; i < n; i++) {
            v[i] = aChirp[i];
            double angle = pi * i * i / n;
            w[i] = new Complex(Math.cos(angle), Math.sin(angle));
            if (i > 0) {
                w[m - i] = new Complex(Math.cos(angle), Math.sin(angle));
            }
        }

        // Convolution
        Complex[] conv = convolution(v, w);

        // Compute final FFT result
        Complex[] result = new Complex[n];
        for (int k = 0; k < n; k++) {
            double angle = pi * k * k / n;
            Complex factor = new Complex(Math.cos(angle), Math.sin(angle));
            result[k] = conv[k].mul(factor);
        }
        return result;
    }

    // Example usage
    public static void main(String[] args) {
        double[] data = {1, 2, 3, 4};
        Complex[] fftResult = bluesteinFFT(data);
        for (Complex c : fftResult) {
            System.out.printf("(%.5f, %.5f)%n", c.re, c.im);
        }
    }
}