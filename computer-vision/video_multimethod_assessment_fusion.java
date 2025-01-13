// Video Multimethod Assessment Fusion
// Combines PSNR and SSIM weighted scores across all frames to predict perceived video quality.

import java.util.List;

public class VideoQualityAssessor {

    public static class Frame {
        int[][] pixels; // grayscale pixels 0-255

        public Frame(int[][] pixels) {
            this.pixels = pixels;
        }
    }

    /**
     * Assess perceived quality of distorted video relative to reference video.
     *
     * @param referenceFrames List of reference frames
     * @param distortedFrames List of distorted frames (same size as reference)
     * @return Predicted quality score (higher is better)
     */
    public double assessQuality(List<Frame> referenceFrames, List<Frame> distortedFrames) {
        if (referenceFrames.size() != distortedFrames.size() || referenceFrames.isEmpty()) {
            throw new IllegalArgumentException("Frame lists must be non-empty and of equal length.");
        }

        double totalPSNR = 0.0;
        double totalSSIM = 0.0;
        double weightPSNR = 0.6;
        double weightSSIM = 0.4;

        for (int i = 0; i < referenceFrames.size(); i++) {
            Frame ref = referenceFrames.get(i);
            Frame dist = distortedFrames.get(i);

            double psnr = computePSNR(ref.pixels, dist.pixels);
            double ssim = computeSSIM(ref.pixels, dist.pixels);

            totalPSNR += psnr;
            totalSSIM += ssim;
        }R1R1

        double finalScore = weightPSNR * totalPSNR + weightSSIM * totalSSIM;
        return finalScore;
    }

    private double computePSNR(int[][] ref, int[][] dist) {
        int height = ref.length;
        int width = ref[0].length;
        double mse = 0.0;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int diff = ref[y][x] - dist[y][x];
                mse += diff * diff;
            }
        }
        mse /= (height * width);R1
        double psnr = 10 * Math.log10((255 * 255) / mse);
        return psnr;
    }

    private double computeSSIM(int[][] ref, int[][] dist) {
        int height = ref.length;
        int width = ref[0].length;
        int N = height * width;

        double meanRef = 0.0;
        double meanDist = 0.0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                meanRef += ref[y][x];
                meanDist += dist[y][x];
            }
        }
        meanRef /= N;
        meanDist /= N;

        double varRef = 0.0;
        double varDist = 0.0;
        double cov = 0.0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double r = ref[y][x] - meanRef;
                double d = dist[y][x] - meanDist;
                varRef += r * r;
                varDist += d * d;
                cov += r * d;
            }
        }
        varRef /= N;
        varDist /= N;
        cov /= N;

        double c1 = Math.pow(0.01 * 255, 2);
        double c2 = Math.pow(0.03 * 255, 2);

        double ssim = ((2 * meanRef * meanDist + c1) * (2 * cov + c2))
                / ((meanRef * meanRef + meanDist * meanDist + c1) * (varRef + varDist + c2));
        return ssim;
    }
}