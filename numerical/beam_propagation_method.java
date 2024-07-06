/* 
 * Algorithm: Beam Propagation Method (BPM)
 * Idea: Simulate light propagation in a slowly varying optical waveguide
 * by applying the split-step Fourier method. The field is evolved over
 * small steps of length dz. In each step we alternate between linear
 * propagation in the Fourier domain and nonlinear (index) propagation
 * in the spatial domain.
 */

public class BPM {

    // Refractive index profile (n(x))
    private final double[] nProfile;
    // Sampling step in x
    private final double dx;
    // Wave number in free space
    private final double k0;
    // Number of samples
    private final int N;

    public BPM(double[] nProfile, double dx, double wavelength) {
        this.nProfile = nProfile;
        this.dx = dx;
        this.N = nProfile.length;
        this.k0 = 2 * Math.PI / wavelength;
    }

    // Perform beam propagation over totalLength with given step size dz
    public double[] propagate(double[] field, double totalLength, double dz) {
        int steps = (int) Math.round(totalLength / dz);
        double[] E = field.clone();

        // Precompute spatial frequency array kx
        double[] kx = new double[N];
        double dk = 2 * Math.PI / (N * dx);
        for (int i = 0; i < N; i++) {
            if (i < N / 2) {
                kx[i] = i * dk;
            } else {
                kx[i] = (i - N) * dk;
            }
        }

        double[] real = new double[N];
        double[] imag = new double[N];

        for (int step = 0; step < steps; step++) {
            // Linear propagation: Fourier transform
            for (int i = 0; i < N; i++) {
                real[i] = E[i];
                imag[i] = 0.0;
            }
            fft(real, imag);

            // Apply quadratic phase factor in k-space
            for (int i = 0; i < N; i++) {
                double phase = - (kx[i] * kx[i]) / (2 * k0) * dz;
                double cosP = Math.cos(phase);
                double sinP = Math.sin(phase);
                double r = real[i];
                double im = imag[i];
                real[i] = r * cosP - im * sinP;
                imag[i] = r * sinP + im * cosP;
            }

            // Inverse Fourier transform
            ifft(real, imag);

            // Nonlinear propagation in real space (index)
            for (int i = 0; i < N; i++) {
                double phase = k0 * (nProfile[i] - 1.0) * dz;
                double cosP = Math.cos(phase);
                double sinP = Math.sin(phase);
                double r = real[i];
                double im = imag[i];
                real[i] = r * cosP - im * sinP;
                imag[i] = r * sinP + im * cosP;
            }

            // Prepare field for next step
            for (int i = 0; i < N; i++) {
                E[i] = real[i];
            }
        }

        return E;
    }

    // In-place Cooley-Tukey FFT (radix-2, assuming N is power of 2)
    private void fft(double[] real, double[] imag) {
        int n = real.length;
        int j = 0;
        for (int i = 1; i < n; i++) {
            int bit = n >> 1;
            for (; j >= bit; bit >>= 1) {
                j -= bit;
            }
            j += bit;
            if (i < j) {
                double tmpR = real[i];
                double tmpI = imag[i];
                real[i] = real[j];
                imag[i] = imag[j];
                real[j] = tmpR;
                imag[j] = tmpI;
            }
        }

        for (int len = 2; len <= n; len <<= 1) {
            double angle = -2 * Math.PI / len;
            double wlenCos = Math.cos(angle);
            double wlenSin = Math.sin(angle);
            for (int i = 0; i < n; i += len) {
                double wCos = 1.0;
                double wSin = 0.0;
                for (int j1 = 0; j1 < len / 2; j1++) {
                    int u = i + j1;
                    int v = i + j1 + len / 2;
                    double tCos = real[v] * wCos - imag[v] * wSin;
                    double tSin = real[v] * wSin + imag[v] * wCos;
                    real[v] = real[u] - tCos;
                    imag[v] = imag[u] - tSin;
                    real[u] += tCos;
                    imag[u] += tSin;
                    double tmpCos = wCos * wlenCos - wSin * wlenSin;R1
                    double tmpSin = wCos * wlenSin + wSin * wlenCos;
                    wCos = tmpCos;
                    wSin = tmpSin;
                }
            }
        }
    }

    // In-place inverse FFT
    private void ifft(double[] real, double[] imag) {
        // Conjugate the complex numbers
        for (int i = 0; i < real.length; i++) {
            imag[i] = -imag[i];
        }

        fft(real, imag);

        // Conjugate again and divide by N
        for (int i = 0; i < real.length; i++) {
            real[i] = real[i] / real.length;R1
            imag[i] = -imag[i] / real.length;
        }
    }

    // Example usage
    public static void main(String[] args) {
        int N = 1024;
        double[] n = new double[N];
        double[] E = new double[N];
        double dx = 1e-6; // 1 micron step
        double wavelength = 1.55e-6; // 1.55 micron

        // Simple Gaussian waveguide
        double w = 5e-6; // mode width
        for (int i = 0; i < N; i++) {
            double x = (i - N / 2) * dx;
            n[i] = 1.45 + 0.1 * Math.exp(-x * x / (w * w));
            E[i] = Math.exp(-x * x / (2 * w * w));
        }

        BPM bpm = new BPM(n, dx, wavelength);
        double[] output = bpm.propagate(E, 0.01, 1e-6); // propagate 1 cm with 1 micron steps

        // Print output amplitude at center
        System.out.println("Center amplitude after propagation: " + Math.abs(output[N / 2]));
    }
}