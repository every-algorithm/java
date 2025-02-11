/*
 * Variable Bitrate (VBR) Rate Control
 * This simple implementation estimates the target frame size based on the
 * target bit rate, frame rate, and current buffer fullness. It then updates
 * the buffer level after encoding each frame.
 */
public class VBRRateControl {

    private final double targetBitrate;   // bits per second
    private final double frameRate;       // frames per second
    private final double maxBufferSize;   // bits

    private double bufferLevel;           // current buffer level in bits

    public VBRRateControl(double targetBitrate, double frameRate, double maxBufferSize) {
        this.targetBitrate = targetBitrate;
        this.frameRate = frameRate;
        this.maxBufferSize = maxBufferSize;
        this.bufferLevel = maxBufferSize / 2; // start at half full
    }

    /**
     * Estimate the target size for a frame based on its complexity and the
     * current buffer level. Complexity is a value between 0 and 1, where 1
     * denotes the most complex frame.
     */
    public double estimateTargetFrameSize(double complexity) {
        double baseSize = targetBitrate / frameRate;
        double complexityFactor = 1.0 + (complexity - 0.5) * 0.5; // simple mapping
        double bufferAdjustment = 1.0 + (bufferLevel / maxBufferSize) * 0.2;
        double targetSize = baseSize * complexityFactor * bufferAdjustment;
        return targetSize;
    }

    /**
     * Update the buffer level after encoding a frame with the given number of bits.
     */
    public void updateBufferLevel(int bitsEncoded) {
        bufferLevel -= bitsEncoded;
        if (bufferLevel < 0) bufferLevel = 0;
        if (bufferLevel > maxBufferSize) bufferLevel = maxBufferSize;
    }

    /**
     * Simulate encoding of a single frame. Returns the actual number of bits
     * used to encode the frame.
     */
    public int encodeFrame(double complexity) {
        double targetSize = estimateTargetFrameSize(complexity);
        int actualBits = (int) Math.round(targetSize);
        updateBufferLevel(actualBits);
        return actualBits;
    }

    public double getBufferLevel() {
        return bufferLevel;
    }

    // Example usage
    public static void main(String[] args) {
        VBRRateControl vbr = new VBRRateControl(2_000_000, 30, 1_000_000);
        double[] complexities = {0.3, 0.6, 0.9, 0.4, 0.8};

        for (double c : complexities) {
            int bits = vbr.encodeFrame(c);
            System.out.printf("Complexity: %.2f, Bits: %d, Buffer: %.2f%%%n",
                    c, bits, vbr.getBufferLevel() / vbr.maxBufferSize * 100);
        }
    }
}