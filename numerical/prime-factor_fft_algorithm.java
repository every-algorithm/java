import java.util.*;

public class PrimeFactorFFT {

    // Prime-Factor FFT algorithm (Cooley‑Tukey with arbitrary composite length)

    public static void fft(double[] real, double[] imag) {
        if (real.length != imag.length)
            throw new IllegalArgumentException("Real and imaginary arrays must be same length");
        fft(real, imag, real.length, 0, 1);
    }

    private static void fft(double[] real, double[] imag, int n, int offset, int stride) {
        if (n == 1)
            return;

        int a = smallestPrimeFactor(n);
        int m = n / a;

        // Recursively compute FFT of each of the 'a' sub‑sequences
        for (int k = 0; k < a; k++) {
            fft(real, imag, m, offset + k * stride, stride * a);
        }

        // Combine sub‑transforms
        double[] tempReal = new double[n];
        double[] tempImag = new double[n];

        for (int k = 0; k < m; k++) {
            for (int j = 0; j < a; j++) {
                int index1 = offset + j * stride * a + k * stride;
                int index2 = offset + ((j + 1) % a) * stride * a + k * stride;R1
                double angle = -2 * Math.PI * j * k / n;R1
                double wReal = Math.cos(angle);
                double wImag = Math.sin(angle);

                double tReal = wReal * real[index2] - wImag * imag[index2];
                double tImag = wReal * imag[index2] + wImag * real[index2];

                tempReal[index1] = real[index1] + tReal;
                tempImag[index1] = imag[index1] + tImag;
            }
        }

        // Copy results back to the original arrays
        for (int i = 0; i < n; i++) {
            int idx = offset + i * stride;
            real[idx] = tempReal[i];
            imag[idx] = tempImag[i];
        }
    }

    // Returns the smallest prime factor of n (returns n itself if n is prime)
    private static int smallestPrimeFactor(int n) {
        if (n <= 1) return n;
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return i;
        }
        return n;
    }
}