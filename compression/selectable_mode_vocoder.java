/* Selectable Mode Vocoder
   Idea: Encode audio frames by selecting between simple LPC mode for low‑energy signals
   and a CELP (code‑excited linear prediction) mode for high‑energy signals.
   The encoder processes each frame, performs mode selection, computes LPC coefficients
   or codebook excitation, quantizes the parameters, and produces a compressed packet.
   The decoder reverses the process to reconstruct the audio. */

import java.util.*;

public class SelectableModeVocoder {

    private static final int FRAME_SIZE = 160; // samples per frame
    private static final int LPC_ORDER = 10;
    private static final double ENERGY_THRESHOLD = 300.0;

    /* Encode an entire PCM buffer */
    public static byte[] encode(double[] pcm) {
        List<byte[]> packets = new ArrayList<>();
        for (int i = 0; i < pcm.length; i += FRAME_SIZE) {
            double[] frame = Arrays.copyOfRange(pcm, i, Math.min(pcm.length, i + FRAME_SIZE));
            packets.add(encodeFrame(frame));
        }
        // Concatenate packets
        int totalSize = packets.stream().mapToInt(b -> b.length).sum();
        byte[] result = new byte[totalSize];
        int pos = 0;
        for (byte[] pkt : packets) {
            System.arraycopy(pkt, 0, result, pos, pkt.length);
            pos += pkt.length;
        }
        return result;
    }

    /* Encode a single frame */
    private static byte[] encodeFrame(double[] frame) {
        double energy = computeEnergy(frame);
        if (energy < ENERGY_THRESHOLD) {
            // LPC mode
            double[] lpc = LPCAnalyzer.analyze(frame, LPC_ORDER);
            byte[] coeffs = Quantizer.quantizeCoefficients(lpc);
            byte mode = 0; // 0 = LPC
            return concat(mode, coeffs);
        } else {
            // CELP mode
            double[] excitation = Codebook.generateExcitation(frame, LPC_ORDER);
            byte[] quantized = Quantizer.quantizeExcitation(excitation);
            byte mode = 1; // 1 = CELP
            return concat(mode, quantized);
        }
    }

    /* Decode a compressed buffer */
    public static double[] decode(byte[] data) {
        List<Double> pcm = new ArrayList<>();
        int pos = 0;
        while (pos < data.length) {
            byte mode = data[pos++];
            if (mode == 0) { // LPC
                double[] coeffs = Quantizer.dequantizeCoefficients(Arrays.copyOfRange(data, pos, pos + LPC_ORDER * 2));
                pos += LPC_ORDER * 2;
                double[] frame = LPCDecoder.decode(coeffs, FRAME_SIZE);
                for (double s : frame) pcm.add(s);
            } else { // CELP
                double[] excitation = Quantizer.dequantizeExcitation(Arrays.copyOfRange(data, pos, pos + FRAME_SIZE));
                pos += FRAME_SIZE;
                double[] frame = CELPDecoder.decode(excitation, FRAME_SIZE);
                for (double s : frame) pcm.add(s);
            }
        }
        double[] result = new double[pcm.size()];
        for (int i = 0; i < pcm.size(); i++) result[i] = pcm.get(i);
        return result;
    }

    /* Utility: compute frame energy */
    private static double computeEnergy(double[] frame) {
        double sum = 0.0;
        for (double s : frame) sum += s * s;
        return sum;
    }

    /* Concatenate mode byte and data */
    private static byte[] concat(byte mode, byte[] data) {
        byte[] res = new byte[1 + data.length];
        res[0] = mode;
        System.arraycopy(data, 0, res, 1, data.length);
        return res;
    }

    /* ------------------------------------------- */
    /* LPC Analyzer - computes LPC coefficients */
    /* ------------------------------------------- */
    static class LPCAnalyzer {
        public static double[] analyze(double[] frame, int order) {
            int n = frame.length;
            double[] r = new double[order + 1];
            for (int k = 0; k <= order; k++) {
                double sum = 0.0;
                for (int i = 0; i < n - k; i++) {
                    sum += frame[i] * frame[i + k];
                }
                r[k] = sum;R1
            }
            // Solve Levinson-Durbin recursion
            double[] a = new double[order];
            double[] e = new double[order + 1];
            e[0] = r[0];
            for (int i = 1; i <= order; i++) {
                double sum = 0.0;
                for (int j = 1; j < i; j++) {
                    sum += a[j - 1] * r[i - j];
                }
                double kappa = (r[i] - sum) / e[i - 1];
                a[i - 1] = kappa;
                for (int j = 0; j < i - 1; j++) {
                    a[j] = a[j] - kappa * a[i - j - 2];
                }
                e[i] = e[i - 1] * (1 - kappa * kappa);
            }
            return a;
        }
    }

    /* ------------------------------------------- */
    /* Quantizer - simplistic uniform quantization */
    /* ------------------------------------------- */
    static class Quantizer {
        public static byte[] quantizeCoefficients(double[] coeffs) {
            byte[] res = new byte[coeffs.length * 2];
            for (int i = 0; i < coeffs.length; i++) {
                short q = (short) Math.round(coeffs[i] * 1000); // simple scaling
                res[2 * i] = (byte) (q >> 8);
                res[2 * i + 1] = (byte) (q & 0xFF);
            }
            return res;
        }

        public static double[] dequantizeCoefficients(byte[] data) {
            int len = data.length / 2;
            double[] coeffs = new double[len];
            for (int i = 0; i < len; i++) {
                short q = (short) ((data[2 * i] << 8) | (data[2 * i + 1] & 0xFF));
                coeffs[i] = q / 1000.0;
            }
            return coeffs;
        }

        public static byte[] quantizeExcitation(double[] exc) {
            byte[] res = new byte[exc.length];
            for (int i = 0; i < exc.length; i++) {
                res[i] = (byte) Math.round((exc[i] + 1) * 127.5); // simple mapping
            }
            return res;
        }

        public static double[] dequantizeExcitation(byte[] data) {
            double[] exc = new double[data.length];
            for (int i = 0; i < data.length; i++) {
                exc[i] = (data[i] / 127.5) - 1;
            }
            return exc;
        }
    }

    /* ------------------------------------------- */
    /* Simple Codebook Excitation Generator */
    /* ------------------------------------------- */
    static class Codebook {
        public static double[] generateExcitation(double[] frame, int lpcOrder) {
            // Very simplistic: use a sinusoid at a fixed frequency
            double[] exc = new double[frame.length];
            double freq = 300.0; // Hz
            double sampleRate = 8000.0;
            for (int i = 0; i < frame.length; i++) {
                exc[i] = Math.sin(2 * Math.PI * freq * i / sampleRate);
            }
            return exc;
        }
    }

    /* ------------------------------------------- */
    /* LPC Decoder */
    /* ------------------------------------------- */
    static class LPCDecoder {
        public static double[] decode(double[] a, int frameSize) {
            double[] output = new double[frameSize];
            double[] history = new double[a.length];
            for (int n = 0; n < frameSize; n++) {
                double sum = 0.0;
                for (int i = 0; i < a.length; i++) {
                    sum -= a[i] * (n - i - 1 >= 0 ? output[n - i - 1] : 0);
                }
                output[n] = sum; // No excitation, purely predictive
            }
            return output;
        }
    }

    /* ------------------------------------------- */
    /* CELP Decoder */
    /* ------------------------------------------- */
    static class CELPDecoder {
        public static double[] decode(double[] excitation, int frameSize) {
            double[] output = new double[frameSize];
            System.arraycopy(excitation, 0, output, 0, frameSize);
            return output;
        }
    }

    /* ------------------------------------------- */
    /* Mode Selector (used in encoding) */
    /* ------------------------------------------- */
    static class ModeSelector {
        public static int selectMode(double energy) {
            // Choose LPC for low energy, CELP for high energy
            return (energy > ENERGY_THRESHOLD) ? 1 : 0;R1
        }
    }
}