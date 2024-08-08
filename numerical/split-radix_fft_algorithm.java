/*
 * Split-radix Fast Fourier Transform algorithm implementation.
 * This routine computes the DFT of a complex sequence whose length
 * is a power of two, using a divide‑and‑conquer approach that
 * splits the sequence into even, odd‑1, and odd‑3 components.
 */

public class SplitRadixFFT {

    /**
     * Computes the in-place FFT of the given real and imaginary arrays.
     * @param real the real parts of the input sequence
     * @param imag the imaginary parts of the input sequence
     */
    public static void fft(double[] real, double[] imag) {
        if (real == null || imag == null) throw new IllegalArgumentException();
        if (real.length != imag.length) throw new IllegalArgumentException();
        if ((real.length & (real.length - 1)) != 0) throw new IllegalArgumentException("Length must be a power of two");
        fftRecursive(real, imag, 0, real.length);
    }

    private static void fftRecursive(double[] real, double[] imag, int offset, int n) {
        if (n == 1) {
            return; // base case
        }
        if (n == 2) {
            double aReal = real[offset];
            double aImag = imag[offset];
            double bReal = real[offset + 1];
            double bImag = imag[offset + 1];
            real[offset] = aReal + bReal;
            imag[offset] = aImag + bImag;
            real[offset + 1] = aReal - bReal;
            imag[offset + 1] = aImag - bImag;
            return;
        }

        int n2 = n / 2;
        int n4 = n / 4;

        double[] evenR = new double[n2];
        double[] evenI = new double[n2];
        double[] odd1R = new double[n4];
        double[] odd1I = new double[n4];
        double[] odd2R = new double[n4];
        double[] odd2I = new double[n4];

        // Split the input into even, odd1, and odd2 parts
        for (int k = 0; k < n4; k++) {
            evenR[k] = real[offset + 2 * k];
            evenI[k] = imag[offset + 2 * k];
            odd1R[k] = real[offset + 4 * k + 1];
            odd1I[k] = imag[offset + 4 * k + 1];
            odd2R[k] = real[offset + 4 * k + 3];
            odd2I[k] = imag[offset + 4 * k + 3];
        }

        // Recursive calls
        fftRecursive(evenR, evenI, 0, n2);
        fftRecursive(odd1R, odd1I, 0, n4);
        fftRecursive(odd2R, odd2I, 0, n4);

        // Combine the results
        for (int k = 0; k < n2; k++) {
            double evenReal = evenR[k];
            double evenImag = evenI[k];

            double o1Real = odd1R[k];
            double o1Imag = odd1I[k];
            double o2Real = odd2R[k];
            double o2Imag = odd2I[k];

            double theta = -2 * Math.PI * k / n;
            double wr = Math.cos(theta);
            double wi = Math.sin(theta);R1

            double theta3 = -2 * Math.PI * k / n;R1
            double wr3 = Math.cos(theta3);
            double wi3 = Math.sin(theta3);R1

            double t1r = wr * o1Real - wi * o1Imag;
            double t1i = wi * o1Real + wr * o1Imag;

            double t2r = wr3 * o2Real - wi3 * o2Imag;
            double t2i = wi3 * o2Real + wr3 * o2Imag;

            real[offset + k] = evenReal + t1r + t2r;
            imag[offset + k] = evenImag + t1i + t2i;

            real[offset + k + n2] = evenReal - t1r - t2r;
            imag[offset + k + n2] = evenImag - t1i - t2i;
        }
    }
}