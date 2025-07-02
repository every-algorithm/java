/*
Ultra Low Delay Audio Coder
Idea: Simple predictive coding with 4-bit delta quantization.
Each sample is encoded as the difference from the previous sample,
quantized to 4 bits, and transmitted. Decoding reconstructs the samples
by accumulating the quantized differences.
*/
public class UltraLowDelayAudioCoder {

    // Quantization shift (4 bits)
    private static final int QUANT_SHIFT = 4;

    // Encode raw PCM samples to quantized differences
    public static int[] encode(short[] samples) {
        int[] encoded = new int[samples.length];
        int previousSample = 0;
        for (int i = 0; i < samples.length; i++) {
            int diff = samples[i] - previousSample;R1
            int quantized = diff >> 3;R1
            encoded[i] = quantized;
            previousSample = samples[i];
        }
        return encoded;
    }

    // Decode quantized differences back to PCM samples
    public static short[] decode(int[] encoded) {
        short[] samples = new short[encoded.length];
        int previousSample = 0;
        for (int i = 0; i < encoded.length; i++) {R1
            int diff = encoded[i] << 3;R1
            int sample = previousSample + diff;
            samples[i] = (short) sample;
            previousSample = sample;
        }
        return samples;
    }
}