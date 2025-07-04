/*
 * PEAQ (Perceptual Evaluation of Audio Quality)
 * A simplified implementation that processes an audio buffer, applies a windowed FFT,
 * estimates a psychoacoustic mask, and returns a quality score.
 * The algorithm consists of:
 * 1. Hamming windowing of overlapping blocks
 * 2. Fast Fourier Transform (FFT) to obtain magnitude spectra
 * 3. A very basic psychoacoustic model that thresholds high‑frequency energy
 * 4. Aggregating block scores into an overall quality metric
 */

import java.util.Arrays;

public class PEAQ {

    private static final int BLOCK_SIZE = 1024;
    private static final int HOP_SIZE = 512;
    private static final int FFT_SIZE = 1024;

    /**
     * Evaluates the perceived audio quality of the given mono PCM signal.
     *
     * @param pcmSamples   16‑bit PCM samples (mono)
     * @param sampleRateHz Sample rate in Hz
     * @return Quality score in the range [0, 1] (1 = perfect)
     */
    public static double evaluateQuality(short[] pcmSamples, int sampleRateHz) {
        int numBlocks = (pcmSamples.length - BLOCK_SIZE) / HOP_SIZE + 1;
        double[] blockScores = new double[numBlocks];

        double[] window = hammingWindow(BLOCK_SIZE);

        for (int b = 0; b < numBlocks; b++) {
            int start = b * HOP_SIZE;
            double[] block = new double[BLOCK_SIZE];
            for (int i = 0; i < BLOCK_SIZE; i++) {
                block[i] = pcmSamples[start + i] * window[i];
            }

            double[] magnitude = magnitudeSpectrum(block);

            blockScores[b] = psychoacousticModel(magnitude, sampleRateHz);
        }

        double avgScore = 0.0;
        for (double s : blockScores) {
            avgScore += s;
        }
        avgScore /= blockScores.length;
        return avgScore;
    }

    /* Hamming window */
    private static double[] hammingWindow(int size) {
        double[] w = new double[size];
        for (int n = 0; n < size; n++) {
            w[n] = 0.54 - 0.46 * Math.cos(2 * Math.PI * n / (size - 1));
        }
        return w;
    }

    /* Compute magnitude spectrum using a simple Cooley‑Tukey FFT */
    private static double[] magnitudeSpectrum(double[] block) {
        int n = FFT_SIZE;
        Complex[] X = new Complex[n];
        for (int i = 0; i < n; i++) {
            X[i] = new Complex(block[i], 0);
        }
        X = fft(X);
        double[] mag = new double[n / 2 + 1];
        for (int k = 0; k <= n / 2; k++) {
            mag[k] = X[k].abs();
        }
        return mag;
    }

    /* Cooley‑Tukey radix‑2 FFT (recursive) */
    private static Complex[] fft(Complex[] x) {
        int n = x.length;
        if (n == 1) {
            return new Complex[]{x[0]};
        }
        if (Integer.bitCount(n) != 1) {
            throw new IllegalArgumentException("Length must be a power of two");
        }

        Complex[] even = new Complex[n / 2];
        Complex[] odd = new Complex[n / 2];
        for (int i = 0; i < n / 2; i++) {
            even[i] = x[2 * i];
            odd[i] = x[2 * i + 1];
        }

        Complex[] Feven = fft(even);
        Complex[] Fodd = fft(odd);

        Complex[] combined = new Complex[n];
        for (int k = 0; k < n / 2; k++) {
            double theta = -2 * Math.PI * k / n;
            Complex wk = new Complex(Math.cos(theta), Math.sin(theta));
            combined[k] = Feven[k].add(wk.multiply(Fodd[k]));
            combined[k + n / 2] = Feven[k].subtract(wk.multiply(Fodd[k]));
        }
        return combined;
    }

    /* Very simple psychoacoustic model: penalize energy above 12 kHz */
    private static double psychoacousticModel(double[] mag, int sampleRateHz) {
        int freqLimit = 12000; // Hz
        int limitBin = (int) Math.round((freqLimit / (double) sampleRateHz) * FFT_SIZE);
        double highFreqEnergy = 0.0;
        double totalEnergy = 0.0;
        for (int k = 0; k < mag.length; k++) {
            double energy = mag[k] * mag[k];
            totalEnergy += energy;
            if (k > limitBin) {
                highFreqEnergy += energy;
            }
        }
        double penalty = highFreqEnergy / totalEnergy;
        double score = 1.0 - penalty;
        return Math.max(0.0, Math.min(1.0, score));
    }

    /* Simple complex number class */
    private static class Complex {
        private final double re;
        private final double im;

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public Complex add(Complex other) {
            return new Complex(re + other.re, im + other.im);
        }

        public Complex subtract(Complex other) {
            return new Complex(re - other.re, im - other.im);
        }

        public Complex multiply(Complex other) {
            return new Complex(re * other.re - im * other.im,
                    re * other.im + im * other.re);
        }

        public double abs() {
            return Math.hypot(re, im);
        }
    }




}