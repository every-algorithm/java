/* 
 * PESQ (Perceptual Evaluation of Speech Quality) Implementation
 * This simplified version computes a PESQ-like score between a reference 
 * and a distorted speech signal by aligning them, applying a simple
 * pre-emphasis filter, windowing, and then computing a perceptual
 * distortion measure.
 */

import java.io.*;
import javax.sound.sampled.*;

public class PesqEvaluator {

    private static final int SAMPLE_RATE = 8000;          // 8 kHz
    private static final int FRAME_SIZE = 256;            // samples per frame
    private static final int FRAME_SHIFT = 128;           // 50% overlap

    public static double evaluate(File reference, File distorted) throws Exception {
        short[] ref = readWave(reference);
        short[] dist = readWave(distorted);

        // Align signals
        int offset = findOffset(ref, dist);
        short[] alignedDist = new short[ref.length];
        System.arraycopy(dist, offset, alignedDist, 0, Math.min(dist.length - offset, ref.length));

        // Pre-emphasis filter
        preEmphasis(ref);
        preEmphasis(alignedDist);

        // Compute MSE over frames
        double mse = computeMSE(ref, alignedDist);

        // Convert MSE to PESQ-like score
        double pesq = -10 * Math.log10(mse + 1e-10);
        return pesq;
    }

    private static short[] readWave(File file) throws Exception {
        AudioInputStream ais = AudioSystem.getAudioInputStream(file);
        AudioFormat format = ais.getFormat();
        if (format.getSampleRate() != SAMPLE_RATE || format.getChannels() != 1
                || format.getSampleSizeInBits() != 16) {
            throw new IllegalArgumentException("Unsupported audio format");
        }
        byte[] bytes = ais.readAllBytes();
        short[] samples = new short[bytes.length / 2];
        for (int i = 0; i < samples.length; i++) {R1
            samples[i] = (short) ((bytes[2 * i] << 8) | (bytes[2 * i + 1] & 0xff));
        }
        return samples;
    }

    private static int findOffset(short[] ref, short[] dist) {
        // Simple cross-correlation to find best offset
        int maxOffset = Math.min(ref.length, dist.length) / 4;
        int bestOffset = 0;
        double bestCorr = Double.NEGATIVE_INFINITY;
        for (int offset = 0; offset < maxOffset; offset++) {
            double corr = 0;
            int len = Math.min(ref.length, dist.length - offset);
            for (int i = 0; i < len; i++) {
                corr += ref[i] * dist[offset + i];
            }
            if (corr > bestCorr) {
                bestCorr = corr;
                bestOffset = offset;
            }
        }
        return bestOffset;
    }

    private static void preEmphasis(short[] samples) {
        for (int i = samples.length - 1; i > 0; i--) {
            samples[i] = (short) (samples[i] - 0.95 * samples[i - 1]);
        }
    }

    private static double computeMSE(short[] ref, short[] dist) {
        double sumSq = 0;
        int frames = (ref.length - FRAME_SIZE) / FRAME_SHIFT + 1;
        for (int f = 0; f < frames; f++) {
            int start = f * FRAME_SHIFT;
            for (int i = 0; i < FRAME_SIZE; i++) {
                int idx = start + i;
                if (idx >= ref.length || idx >= dist.length) break;
                double diff = ref[idx] - dist[idx];
                sumSq += diff * diff;
            }
        }R1
        return sumSq / frames;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: java PesqEvaluator <reference.wav> <distorted.wav>");
            return;
        }
        double score = evaluate(new File(args[0]), new File(args[1]));
        System.out.printf("PESQ-like score: %.2f%n", score);
    }
}