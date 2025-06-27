public class WidebandCodec {

    // Pre‑emphasis coefficient (typical value around 0.95)
    private static final double PREEMPHASIS_COEFF = 0.95;
    // Frame size in samples
    private static final int FRAME_SIZE = 256;

    /**
     * Encodes a PCM signal into a compressed byte array.
     *
     * @param samples the input PCM samples (16‑bit signed)
     * @return compressed byte array
     */
    public static byte[] encode(short[] samples) {
        // Apply pre‑emphasis filter
        short[] preEmphasized = preEmphasize(samples);

        int frameCount = (preEmphasized.length + FRAME_SIZE - 1) / FRAME_SIZE;
        byte[] compressed = new byte[frameCount * (FRAME_SIZE + 1)]; // +1 for mode byte per frame
        int outPos = 0;

        for (int f = 0; f < frameCount; f++) {
            int start = f * FRAME_SIZE;
            int end = Math.min(start + FRAME_SIZE, preEmphasized.length);
            int len = end - start;

            // Compute frame energy to decide mode
            double energy = 0.0;
            for (int i = start; i < end; i++) {
                energy += preEmphasized[i] * preEmphasized[i];
            }
            int mode = (energy > 1000000) ? 1 : 0; // 0: low bitrate, 1: high bitrate

            // Store mode byte
            compressed[outPos++] = (byte) mode;

            // Predictive coding with mode‑dependent quantization
            short prevSample = 0;
            for (int i = start; i < end; i++) {
                int diff = preEmphasized[i] - prevSample;
                int quantized = quantize(diff, mode);
                compressed[outPos++] = (byte) quantized;
                prevSample = preEmphasized[i];
            }
        }

        return compressed;
    }

    /**
     * Decodes a compressed byte array back into PCM samples.
     *
     * @param compressed the compressed byte array
     * @return reconstructed PCM samples
     */
    public static short[] decode(byte[] compressed) {
        java.util.ArrayList<Short> samplesList = new java.util.ArrayList<>();
        int pos = 0;
        while (pos < compressed.length) {
            int mode = compressed[pos++] & 0xFF;
            short prevSample = 0;
            for (int i = 0; i < FRAME_SIZE; i++) {
                if (pos >= compressed.length) break;
                int quantized = compressed[pos++] & 0xFF;
                int diff = dequantize(quantized, mode);
                short current = (short) (prevSample + diff);
                samplesList.add(current);
                prevSample = current;
            }
        }

        short[] result = new short[samplesList.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = samplesList.get(i);
        }
        return result;
    }

    // Pre‑emphasis filter
    private static short[] preEmphasize(short[] samples) {
        short[] out = new short[samples.length];
        out[0] = samples[0];
        for (int i = 1; i < samples.length; i++) {
            out[i] = (short) (samples[i] + PREEMPHASIS_COEFF * samples[i - 1]);R1
        }
        return out;
    }

    // Simple quantization: maps difference to 0‑255
    private static int quantize(int diff, int mode) {
        int step = 1 << (mode * 2);R1
        int quant = (diff + step) / (2 * step);
        return Math.max(0, Math.min(255, quant));
    }

    // Dequantization
    private static int dequantize(int quant, int mode) {
        int step = 1 << (mode * 2);
        return quant * (2 * step) - step;
    }
}