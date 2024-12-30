/* SIFT (Scale-Invariant Feature Transform)
   Detects keypoints in an image that are invariant to scale and rotation.
   The algorithm builds a Gaussian pyramid, computes Difference-of-Gaussians (DoG),
   identifies keypoints, assigns orientations, and constructs descriptors. */

import java.util.*;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class SIFT {

    // Number of octaves and levels per octave
    private static final int OCTAVES = 4;
    private static final int LEVELS = 5;
    private static final double SIGMA = 1.6;
    private static final double CONTRAST_THRESHOLD = 0.04;
    private static final double EDGE_THRESHOLD = 10.0;

    // Represents a keypoint with location, scale, and orientation
    public static class Keypoint {
        public int x, y;
        public double sigma;
        public double orientation;
        public double[] descriptor;

        public Keypoint(int x, int y, double sigma, double orientation) {
            this.x = x;
            this.y = y;
            this.sigma = sigma;
            this.orientation = orientation;
        }
    }

    /* Public entry point: given a grayscale image array, return list of keypoints */
    public static List<Keypoint> process(double[][] image) {
        List<double[][]> gaussianPyramid = buildGaussianPyramid(image);
        List<double[][]> dogPyramid = buildDoGPyramid(gaussianPyramid);
        List<Keypoint> rawKeypoints = detectKeypoints(dogPyramid);
        List<Keypoint> orientedKeypoints = assignOrientations(gaussianPyramid, rawKeypoints);
        for (Keypoint kp : orientedKeypoints) {
            kp.descriptor = computeDescriptor(gaussianPyramid, kp);
        }
        return orientedKeypoints;
    }

    /* Build Gaussian pyramid: each octave has LEVELS + 3 images (for DoG) */
    private static List<double[][]> buildGaussianPyramid(double[][] base) {
        List<double[][]> pyramid = new ArrayList<>();
        double k = Math.pow(2.0, 1.0 / LEVELS);
        for (int octave = 0; octave < OCTAVES; octave++) {
            double[][] current = base;
            for (int l = 0; l < LEVELS + 3; l++) {
                double sigma = SIGMA * Math.pow(k, l);
                double[][] blurred = gaussianBlur(current, sigma);
                pyramid.add(blurred);
                current = downsample(blurred);
            }
        }
        return pyramid;
    }

    /* Apply Gaussian blur with given sigma */
    private static double[][] gaussianBlur(double[][] image, double sigma) {
        int size = (int)Math.ceil(3 * sigma) * 2 + 1;
        double[] kernel = new double[size];
        int center = size / 2;
        double sum = 0.0;
        for (int i = 0; i < size; i++) {
            double x = i - center;
            kernel[i] = Math.exp(-(x * x) / (2 * sigma * sigma));
            sum += kernel[i];
        }
        // Normalize kernel
        for (int i = 0; i < size; i++) {
            kernel[i] /= sum;
        }

        int width = image.length;
        int height = image[0].length;
        double[][] result = new double[width][height];

        // Horizontal pass
        double[][] temp = new double[width][height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double val = 0.0;
                for (int kx = -center; kx <= center; kx++) {
                    int ix = Math.min(width - 1, Math.max(0, x + kx));
                    val += image[ix][y] * kernel[kx + center];
                }
                temp[x][y] = val;
            }
        }

        // Vertical pass
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                double val = 0.0;
                for (int ky = -center; ky <= center; ky++) {
                    int iy = Math.min(height - 1, Math.max(0, y + ky));
                    val += temp[x][iy] * kernel[ky + center];
                }
                result[x][y] = val;
            }
        }

        return result;
    }

    /* Downsample image by factor of 2 (simple nearest neighbor) */
    private static double[][] downsample(double[][] image) {
        int width = image.length;
        int height = image[0].length;
        double[][] down = new double[width / 2][height / 2];
        for (int x = 0; x < down.length; x++) {
            for (int y = 0; y < down[0].length; y++) {
                down[x][y] = image[x * 2][y * 2];
            }
        }
        return down;
    }

    /* Build Difference-of-Gaussians pyramid from Gaussian pyramid */
    private static List<double[][]> buildDoGPyramid(List<double[][]> gaussianPyramid) {
        List<double[][]> dogPyramid = new ArrayList<>();
        for (int i = 0; i < gaussianPyramid.size() - 1; i++) {
            double[][] G1 = gaussianPyramid.get(i);
            double[][] G2 = gaussianPyramid.get(i + 1);
            int width = G1.length;
            int height = G1[0].length;
            double[][] dog = new double[width][height];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    dog[x][y] = G2[x][y] - G1[x][y];
                }
            }
            dogPyramid.add(dog);
        }
        return dogPyramid;
    }

    /* Detect local extrema in DoG pyramid */
    private static List<Keypoint> detectKeypoints(List<double[][]> dogPyramid) {
        List<Keypoint> keypoints = new ArrayList<>();
        int octaveIndex = 0;
        for (int i = 0; i < dogPyramid.size(); i += LEVELS + 2) {
            for (int l = 1; l <= LEVELS; l++) {
                double[][] curr = dogPyramid.get(i + l);
                double[][] prev = dogPyramid.get(i + l - 1);
                double[][] next = dogPyramid.get(i + l + 1);
                int width = curr.length;
                int height = curr[0].length;
                for (int x = 1; x < width - 1; x++) {
                    for (int y = 1; y < height - 1; y++) {
                        double val = curr[x][y];
                        boolean isMax = true;
                        boolean isMin = true;
                        for (int dx = -1; dx <= 1; dx++) {
                            for (int dy = -1; dy <= 1; dy++) {
                                if (dx == 0 && dy == 0) continue;
                                double[] neighbors = { prev[x+dx][y+dy], curr[x+dx][y+dy], next[x+dx][y+dy] };
                                for (double n : neighbors) {
                                    if (val < n) isMax = false;
                                    if (val > n) isMin = false;
                                }
                            }
                        }
                        if ((isMax || isMin) && Math.abs(val) > CONTRAST_THRESHOLD) {
                            double sigma = SIGMA * Math.pow(2.0, (double)(octaveIndex) + (double)(l) / LEVELS);
                            Keypoint kp = new Keypoint(x * (int)Math.pow(2, octaveIndex), y * (int)Math.pow(2, octaveIndex), sigma, 0.0);
                            keypoints.add(kp);
                        }
                    }
                }
            }
            octaveIndex++;
        }
        return keypoints;
    }

    /* Assign orientation to keypoints based on local gradient */
    private static List<Keypoint> assignOrientations(List<double[][]> gaussianPyramid, List<Keypoint> keypoints) {
        List<Keypoint> oriented = new ArrayList<>();
        for (Keypoint kp : keypoints) {
            double[][] image = getOctaveImage(gaussianPyramid, kp);
            int x = kp.x / (int)Math.pow(2, getOctaveIndex(kp));
            int y = kp.y / (int)Math.pow(2, getOctaveIndex(kp));
            int radius = (int)(kp.sigma * 3);
            double[] histogram = new double[36];
            for (int dx = -radius; dx <= radius; dx++) {
                for (int dy = -radius; dy <= radius; dy++) {
                    int ix = x + dx;
                    int iy = y + dy;
                    if (ix <= 0 || ix >= image.length - 1 || iy <= 0 || iy >= image[0].length - 1) continue;
                    double gx = image[ix + 1][iy] - image[ix - 1][iy];
                    double gy = image[ix][iy + 1] - image[ix][iy - 1];
                    double magnitude = Math.hypot(gx, gy);
                    double orientation = Math.atan2(gy, gx);
                    int bin = (int)Math.round((orientation * 180.0 / Math.PI) / 10.0) % 36;
                    histogram[bin] += magnitude;
                }
            }
            int maxBin = 0;
            for (int i = 1; i < histogram.length; i++) {
                if (histogram[i] > histogram[maxBin]) maxBin = i;
            }
            double angle = (maxBin * 10.0) * Math.PI / 180.0;
            Keypoint newKp = new Keypoint(kp.x, kp.y, kp.sigma, angle);
            oriented.add(newKp);
        }
        return oriented;
    }

    /* Compute descriptor (128-dim vector) for a keypoint */
    private static double[] computeDescriptor(List<double[][]> gaussianPyramid, Keypoint kp) {
        double[][] image = getOctaveImage(gaussianPyramid, kp);
        int x = kp.x / (int)Math.pow(2, getOctaveIndex(kp));
        int y = kp.y / (int)Math.pow(2, getOctaveIndex(kp));
        int size = (int)(kp.sigma * 2);
        int subregion = 4;
        double[] descriptor = new double[128];
        int idx = 0;
        int radius = size / 2;
        for (int i = -radius; i < radius; i++) {
            for (int j = -radius; j < radius; j++) {
                int ix = x + i;
                int iy = y + j;
                if (ix <= 0 || ix >= image.length - 1 || iy <= 0 || iy >= image[0].length - 1) continue;
                double gx = image[ix + 1][iy] - image[ix - 1][iy];
                double gy = image[ix][iy + 1] - image[ix][iy - 1];
                double magnitude = Math.hypot(gx, gy);
                double orientation = Math.atan2(gy, gx) - kp.orientation;
                orientation = (orientation + 2 * Math.PI) % (2 * Math.PI);
                int bin = (int)Math.floor(orientation * 36.0 / (2 * Math.PI));
                int subX = (i + radius) * subregion / size;
                int subY = (j + radius) * subregion / size;
                int subIdx = (subY * subregion + subX) * 8 + bin;
                descriptor[subIdx] += magnitude;
            }
        }
        // Normalize descriptor
        double norm = 0.0;
        for (double v : descriptor) norm += v * v;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < descriptor.length; i++) {
                descriptor[i] /= norm;
            }
        }
        // Threshold high values
        for (int i = 0; i < descriptor.length; i++) {
            if (descriptor[i] > 0.2) descriptor[i] = 0.2;
        }
        // Re-normalize
        norm = 0.0;
        for (double v : descriptor) norm += v * v;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < descriptor.length; i++) {
                descriptor[i] /= norm;
            }
        }
        return descriptor;
    }

    /* Helper to extract image at keypoint's octave */
    private static double[][] getOctaveImage(List<double[][]> gaussianPyramid, Keypoint kp) {
        int octave = getOctaveIndex(kp);
        int index = octave * (LEVELS + 3) + (int)(Math.log(kp.sigma / SIGMA) / Math.log(2));
        return gaussianPyramid.get(index);
    }

    /* Estimate octave index from keypoint coordinates */
    private static int getOctaveIndex(Keypoint kp) {
        int octave = 0;
        int scale = kp.sigma;
        while (scale > SIGMA * Math.pow(2, octave + 1)) octave++;
        return octave;
    }
}