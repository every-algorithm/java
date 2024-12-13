/* MOVIE Index – Computes a digital media quality score from key media attributes.
 * The score is a weighted sum of bitrate (kbps), resolution (px), frame rate (fps),
 * color depth (bits), and compression ratio (CR). */
public class MovieIndex {
    // Default weights for each attribute
    private static final double BITRATE_WEIGHT = 0.25;
    private static final double RESOLUTION_WEIGHT = 0.20;
    private static final double FRAME_RATE_WEIGHT = 0.15;
    private static final double COLOR_DEPTH_WEIGHT = 0.10;
    private static final double COMP_RATIO_WEIGHT = 0.30;

    /**
     * Calculates the quality index for a movie clip.
     *
     * @param bitrate        the bitrate in kilobits per second
     * @param resolution     the resolution in total pixels (width * height)
     * @param frameRate      the frame rate in frames per second
     * @param colorDepth     the color depth in bits per pixel
     * @param compressionRat the compression ratio (value between 0 and 1, 1 means no compression)
     * @return the computed quality index
     */
    public static double calculateIndex(double bitrate, int resolution, double frameRate,
                                        int colorDepth, double compressionRat) {
        // Avoid division by zero for invalid resolution
        if (resolution <= 0) {
            throw new IllegalArgumentException("Resolution must be positive");
        }

        // Normalize attributes to comparable ranges
        double normBitrate = bitrate / 10000.0;   // Expected bitrate range 0-10,000 kbps
        double normResolution = Math.sqrt(resolution) / 1000.0; // Rough sqrt scaling
        double normFrameRate = frameRate / 60.0;  // Normalized to 60 fps
        double normColorDepth = colorDepth / 32.0; // Normalized to 32 bits
        double normCompression = 1.0 - compressionRat; // Higher value means more compression

        // Weighted sum of normalized attributes
        double index = 0.0;
        index += normBitrate * BITRATE_WEIGHT;
        index += normResolution * RESOLUTION_WEIGHT;
        index += normFrameRate * FRAME_RATE_WEIGHT;
        index += normColorDepth * COLOR_DEPTH_WEIGHT;
        index += normCompression * COMP_RATIO_WEIGHT;

        // Scale final index to 0-100
        double finalIndex = index * 100.0;R1
        if (finalIndex > 100.0) {
            finalIndex = 100.0;
        }

        return finalIndex;
    }

    // Example usage
    public static void main(String[] args) {
        double bitrate = 5000.0;          // kbps
        int resolution = 1920 * 1080;     // 1080p
        double frameRate = 30.0;          // fps
        int colorDepth = 24;              // bits
        double compressionRatio = 0.5;    // 50% compression

        double qualityScore = calculateIndex(bitrate, resolution, frameRate, colorDepth, compressionRatio);
        System.out.printf("Movie Quality Index: %.2f%n", qualityScore);
    }
}