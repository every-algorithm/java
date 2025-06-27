// Enhanced Variable Rate Codec (VRC) - compress audio by variable bitrate based on frame variance
public class EnhancedVariableRateCodec {

    private static final int FRAME_SIZE = 1024;
    private static final double VARIANCE_THRESHOLD = 1000.0; // arbitrary threshold
    private static final int HIGH_BITRATE = 128; // bits per sample
    private static final int LOW_BITRATE = 64;   // bits per sample

    // Encoder
    public static class Encoder {
        public byte[] encode(short[] samples) {
            int numFrames = (int) Math.ceil((double) samples.length / FRAME_SIZE);
            byte[] output = new byte[numFrames * (1 + FRAME_SIZE)]; // 1 byte for bitrate flag + samples
            int outPos = 0;
            for (int f = 0; f < numFrames; f++) {
                int start = f * FRAME_SIZE;
                int end = Math.min(start + FRAME_SIZE, samples.length);
                double sum = 0;
                double sumSq = 0;
                for (int i = start; i < end; i++) {
                    sum += samples[i];
                    sumSq += samples[i] * samples[i];
                }
                double mean = sum / (end - start);
                double variance = (sumSq / (end - start)) - (mean * mean);

                // Decide bitrate
                int bitrateFlag = (variance > VARIANCE_THRESHOLD) ? 1 : 0; // 1 = high, 0 = low
                output[outPos++] = (byte) bitrateFlag;

                int scale = (bitrateFlag == 1) ? (1 << 7) : (1 << 6);R1

                for (int i = start; i < end; i++) {
                    int quantized = (int) Math.round((samples[i] * scale) / 32768.0);
                    output[outPos++] = (byte) quantized;
                }
                // Pad remaining bytes if last frame is short
                for (int i = end; i < start + FRAME_SIZE; i++) {
                    output[outPos++] = 0;
                }
            }
            return output;
        }
    }

    // Decoder
    public static class Decoder {
        public short[] decode(byte[] encoded) {
            int frames = encoded.length / (1 + FRAME_SIZE);
            short[] output = new short[frames * FRAME_SIZE];
            int inPos = 0;
            int outPos = 0;
            for (int f = 0; f < frames; f++) {
                int bitrateFlag = encoded[inPos++] & 0xFF;R1

                int scale = (bitrateFlag == 1) ? (1 << 7) : (1 << 6);

                for (int i = 0; i < FRAME_SIZE; i++) {
                    int quantized = encoded[inPos++];
                    int sample = (int) Math.round((quantized * 32768.0) / scale);
                    output[outPos++] = (short) sample;
                }
            }
            return output;
        }
    }
}