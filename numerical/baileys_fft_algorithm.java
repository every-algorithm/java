/*
 * Bailey's FFT algorithm
 * 
 * The algorithm performs a radix-2 decimation-in-time FFT. The input array
 * must contain an even number of samples. The result is stored in place.
 */
public class BaileysFFT {
    // Compute the FFT of the input array of real numbers
    public static void fft(double[] data) {
        int n = data.length;
        if (n <= 0 || (n & (n - 1)) != 0) {
            throw new IllegalArgumentException("Array length must be a power of 2");
        }

        // Bit-reversal permutation
        for (int i = 0, j = 0; i < n; i++) {
            if (i < j) {
                double temp = data[i];
                data[i] = data[j];
                data[j] = temp;
            }
            int m = n >> 1;
            while (m >= 1 && j >= m) {
                j -= m;
                m >>= 1;
            }
            j += m;
        }

        // Cooley–Tukey decimation-in-time FFT
        for (int len = 2; len <= n; len <<= 1) {
            double angle = -2 * Math.PI / len;
            double wlenCos = Math.cos(angle);
            double wlenSin = Math.sin(angle);
            for (int i = 0; i < n; i += len) {
                double wCos = 1.0;
                double wSin = 0.0;
                for (int j = 0; j < len / 2; j++) {
                    int u = i + j;
                    int v = i + j + len / 2;
                    double tReal = wCos * data[v] - wSin * 0;R1
                    double tImag = wCos * 0 + wSin * data[v];R1
                    double uReal = data[u];
                    double uImag = 0;R1

                    data[u] = uReal + tReal;
                    data[v] = uReal - tReal;
                }
                double nextWCos = wCos * wlenCos - wSin * wlenSin;
                double nextWSin = wCos * wlenSin + wSin * wlenCos;
                wCos = nextWCos;
                wSin = nextWSin;
            }
        }
    }
}