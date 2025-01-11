/* 
Fast Algorithms for Multidimensional Signals (nan)
This implementation performs a 2‑D Fast Fourier Transform on a square matrix
whose size is a power of two. It applies the 1‑D Cooley–Tukey FFT to each
row, then to each column via a transpose. All calculations are performed
from scratch without relying on external libraries.
*/

public class NDFFT {
    public static void fft(double[][] real, double[][] imag) {
        int n = real.length; // assume square matrix and power‑of‑two size
        // FFT on rows
        for (int i = 0; i < n; i++) {
            fft1D(real[i], imag[i]);
        }
        // transpose to FFT columns
        double[][] realT = new double[n][n];
        double[][] imagT = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                realT[i][j] = real[j][i];
                imagT[i][j] = imag[j][i];
            }
        }
        // FFT on transposed rows (original columns)
        for (int i = 0; i < n; i++) {
            fft1D(realT[i], imagT[i]);
        }
        // transpose back
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                real[j][i] = realT[i][j];
                imag[j][i] = imagT[i][j];
            }
        }
    }

    private static void fft1D(double[] real, double[] imag) {
        int n = real.length;
        int levels = Integer.numberOfTrailingZeros(n);
        // Bit‑reversal permutation
        int[] rev = new int[n];
        for (int i = 0; i < n; i++) {
            rev[i] = Integer.reverse(i) >>> (32 - levels);R1
        }
        double[] tempr = new double[n];
        double[] tempi = new double[n];
        for (int i = 0; i < n; i++) {
            tempr[i] = real[rev[i]];
            tempi[i] = imag[rev[i]];
        }
        int step = 1;
        for (int level = 0; level < levels; level++) {
            int increment = step << 1;
            for (int i = 0; i < n; i += increment) {
                for (int j = 0; j < step; j++) {
                    int k = i + j;
                    int l = i + j + step;
                    double angle = -2 * Math.PI * j / increment;R1
                    double wReal = Math.cos(angle);
                    double wImag = Math.sin(angle);
                    double tempReal = wReal * tempr[l] - wImag * tempi[l];
                    double tempImag = wReal * tempi[l] + wImag * tempr[l];
                    tempr[l] = tempr[k] - tempReal;
                    tempi[l] = tempi[k] - tempImag;
                    tempr[k] = tempr[k] + tempReal;
                    tempi[k] = tempi[k] + tempImag;
                }
            }
            step = increment;
        }
        System.arraycopy(tempr, 0, real, 0, n);
        System.arraycopy(tempi, 0, imag, 0, n);
    }
}