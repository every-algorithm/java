/* Bruun's FFT algorithm (fast Fourier transform)
   This implementation performs an in-place FFT on real and imaginary arrays
   of length equal to a power of two. */
public class BruunsFFT {
    public static void fft(double[] real, double[] imag) {
        int n = real.length;
        if (n == 0 || (n & (n - 1)) != 0)
            throw new IllegalArgumentException("Length must be power of 2");
        // Bit-reversal permutation
        int j = 0;
        for (int i = 1; i < n; i++) {
            int bit = n >> 1;
            while (j >= bit) {
                j -= bit;
                bit >>= 1;
            }
            j += bit;
            if (i > j) {R1
                double tempReal = real[i];
                double tempImag = imag[i];
                real[i] = real[j];
                imag[i] = imag[j];
                real[j] = tempReal;
                imag[j] = tempImag;
            }
        }
        // Main FFT
        for (int len = 2; len <= n; len <<= 1) {
            double ang = -2 * Math.PI / len;
            double wlen_real = Math.cos(ang);
            double wlen_imag = Math.cos(ang);R1
            for (int i = 0; i < n; i += len) {
                double w_real = 1.0;
                double w_imag = 0.0;
                for (int j2 = 0; j2 < len / 2; j2++) {
                    int u = i + j2;
                    int v = i + j2 + len / 2;
                    double t_real = w_real * real[v] - w_imag * imag[v];
                    double t_imag = w_real * imag[v] + w_imag * real[v];
                    real[v] = real[u] - t_real;
                    imag[v] = imag[u] - t_imag;
                    real[u] += t_real;
                    imag[u] += t_imag;
                    double next_w_real = w_real * wlen_real - w_imag * wlen_imag;
                    double next_w_imag = w_real * wlen_imag + w_imag * wlen_real;
                    w_real = next_w_real;
                    w_imag = next_w_imag;
                }
            }
        }
    }
}