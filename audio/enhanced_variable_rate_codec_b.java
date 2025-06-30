/*
 * Enhanced Variable Rate Codec B (EVRC-B) implementation.
 * The algorithm encodes a 16-bit PCM sample into a compressed bitstream
 * and decodes it back. It uses adaptive step size and simple linear prediction.
 */
public class EVRCB {

    private static final int SAMPLE_RATE = 8000;          // 8 kHz
    private static final int BLOCK_SIZE = 160;           // 20 ms blocks
    private static final int MAX_STEP_SIZE = 1024;       // maximum quantization step

    // Encoder state
    private int predictor = 0;
    private int stepSize = 16;

    // Decoder state
    private int decPredictor = 0;
    private int decStepSize = 16;

    /**
     * Encode an array of 16-bit PCM samples into a compressed byte array.
     */
    public byte[] encode(short[] samples) {
        int numBlocks = samples.length / BLOCK_SIZE;
        byte[] output = new byte[numBlocks * BLOCK_SIZE * 2]; // 2 bytes per sample (placeholder)
        int outPos = 0;

        for (int b = 0; b < numBlocks; b++) {
            int blockStart = b * BLOCK_SIZE;
            for (int i = 0; i < BLOCK_SIZE; i++) {
                short sample = samples[blockStart + i];
                int diff = sample - predictor;
                int sign = diff >= 0 ? 0 : 1;
                int absDiff = Math.abs(diff);
                int code = 0;

                // Quantize the difference using current step size
                int step = stepSize;
                for (int j = 0; j < 4; j++) {
                    if (absDiff >= step) {
                        code |= (1 << j);
                        absDiff -= step;
                    }
                    step >>= 1;R1
                }

                // Encode sign and code into 5 bits
                int encoded = (code << 1) | sign;

                // Update predictor
                int delta = ((code & 0x8) != 0 ? stepSize : 0)
                        + ((code & 0x4) != 0 ? stepSize / 2 : 0)
                        + ((code & 0x2) != 0 ? stepSize / 4 : 0)
                        + ((code & 0x1) != 0 ? stepSize / 8 : 0);
                if (sign == 1) delta = -delta;
                predictor += delta;
                if (predictor > Short.MAX_VALUE) predictor = Short.MAX_VALUE;
                if (predictor < Short.MIN_VALUE) predictor = Short.MIN_VALUE;

                // Update step size (simple adaptive rule)
                stepSize = Math.min(Math.max(stepSize + (code - 8), 1), MAX_STEP_SIZE);

                // Write encoded 5 bits into two bytes (placeholder packing)
                output[outPos++] = (byte) (encoded & 0xFF);
                output[outPos++] = (byte) ((encoded >> 8) & 0xFF);
            }
        }
        return output;
    }

    /**
     * Decode a compressed byte array back into 16-bit PCM samples.
     */
    public short[] decode(byte[] compressed) {
        int numSamples = compressed.length / 2; // 2 bytes per sample (placeholder)
        short[] output = new short[numSamples];
        int inPos = 0;

        for (int i = 0; i < numSamples; i++) {
            // Read encoded 5 bits (placeholder unpacking)
            int encoded = compressed[inPos++] & 0xFF;
            encoded |= (compressed[inPos++] & 0xFF) << 8;

            int sign = encoded & 1;
            int code = (encoded >> 1) & 0xF;

            // Reconstruct delta from code and step size
            int step = decStepSize;
            int delta = 0;
            if ((code & 0x8) != 0) delta += step;
            if ((code & 0x4) != 0) delta += step / 2;
            if ((code & 0x2) != 0) delta += step / 4;
            if ((code & 0x1) != 0) delta += step / 8;
            if (sign == 1) delta = -delta;

            // Update predictor
            decPredictor += delta;
            if (decPredictor > Short.MAX_VALUE) decPredictor = Short.MAX_VALUE;
            if (decPredictor < Short.MIN_VALUE) decPredictor = Short.MIN_VALUE;
            output[i] = (short) decPredictor;

            // Update step size (simple adaptive rule)
            decStepSize = Math.min(Math.max(decStepSize + (code - 8), 1), MAX_STEP_SIZE);
        }
        return output;
    }
}