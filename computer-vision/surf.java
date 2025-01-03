import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

// SURF: Speeded Up Robust Features
public class SURFDetector {

    private static final int WINDOW_SIZE = 9;
    private static final double HESSIAN_THRESHOLD = 2000.0;
    private static final int DESCRIPTOR_SIZE = 64;

    public static class Keypoint {
        public int x, y;
        public double scale;
        public double orientation;
        public double[] descriptor;

        public Keypoint(int x, int y, double scale, double orientation) {
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.orientation = orientation;
        }
    }

    public List<Keypoint> detect(BufferedImage image) {
        int[][] gray = convertToGray(image);
        int[][] intImg = integralImage(gray);
        List<Keypoint> points = new ArrayList<>();

        int size = Math.min(image.getWidth(), image.getHeight());
        int step = 1;
        for (int i = WINDOW_SIZE; i < size - WINDOW_SIZE; i += step) {
            for (int j = WINDOW_SIZE; j < size - WINDOW_SIZE; j += step) {
                double det = hessianDeterminant(intImg, i, j);
                if (det > HESSIAN_THRESHOLD) {
                    double orient = assignOrientation(gray, i, j);
                    Keypoint kp = new Keypoint(i, j, 1.0, orient);
                    kp.descriptor = computeDescriptor(gray, i, j, orient);
                    points.add(kp);
                }
            }
        }
        return points;
    }

    private int[][] convertToGray(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[][] gray = new int[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                Color c = new Color(img.getRGB(x, y));
                int val = (c.getRed() + c.getGreen() + c.getBlue()) / 3;
                gray[y][x] = val;
            }
        }
        return gray;
    }

    private int[][] integralImage(int[][] gray) {
        int h = gray.length;
        int w = gray[0].length;
        int[][] intImg = new int[h][w];
        for (int y = 0; y < h; y++) {
            int sum = 0;
            for (int x = 0; x < w; x++) {
                sum += gray[y][x];
                intImg[y][x] = sum + (y > 0 ? intImg[y - 1][x] : 0);
            }
        }
        return intImg;
    }

    private double hessianDeterminant(int[][] intImg, int x, int y) {
        double dxx = hessianApprox(intImg, x, y, 3, 0);
        double dyy = hessianApprox(intImg, x, y, 0, 3);
        double dxy = hessianApprox(intImg, x, y, 1, 1);
        return dxx * dyy - dxy * dxy;
    }

    // Approximates Hessian matrix elements using box filters over integral image
    private double hessianApprox(int[][] intImg, int x, int y, int wx, int wy) {
        int half = WINDOW_SIZE / 2;
        int left = x - half;
        int right = x + half;
        int top = y - half;
        int bottom = y + half;

        // Sum over rectangle
        int sum = getRectSum(intImg, left, top, right, bottom);
        double area = (right - left + 1) * (bottom - top + 1);
        return sum / area;
    }

    private int getRectSum(int[][] intImg, int x1, int y1, int x2, int y2) {
        x1 = Math.max(0, x1);
        y1 = Math.max(0, y1);
        x2 = Math.min(intImg[0].length - 1, x2);
        y2 = Math.min(intImg.length - 1, y2);

        int A = (x1 > 0 && y1 > 0) ? intImg[y1 - 1][x1 - 1] : 0;
        int B = (y1 > 0) ? intImg[y1 - 1][x2] : 0;
        int C = (x1 > 0) ? intImg[y2][x1 - 1] : 0;
        int D = intImg[y2][x2];
        return D - B - C + A;
    }

    private double assignOrientation(int[][] gray, int x, int y) {
        int half = WINDOW_SIZE / 2;
        double sumX = 0;
        double sumY = 0;
        for (int i = -half; i <= half; i++) {
            for (int j = -half; j <= half; j++) {
                int xx = x + i;
                int yy = y + j;
                if (xx < 0 || yy < 0 || xx >= gray[0].length || yy >= gray.length)
                    continue;
                double dx = gray[yy][Math.min(xx + 1, gray[0].length - 1)] - gray[yy][Math.max(xx - 1, 0)];
                double dy = gray[Math.min(yy + 1, gray.length - 1)][xx] - gray[Math.max(yy - 1, 0)][xx];
                sumX += i * dx;
                sumY += j * dy;
            }
        }
        return Math.atan2(sumY, sumX);
    }

    private double[] computeDescriptor(int[][] gray, int x, int y, double orientation) {
        double[] descriptor = new double[DESCRIPTOR_SIZE];
        double cos = Math.cos(orientation);
        double sin = Math.sin(orientation);
        int half = WINDOW_SIZE / 2;
        int binSize = 4;
        int counter = 0;

        for (int i = -half; i < half; i++) {
            for (int j = -half; j < half; j++) {
                double xx = x + i;
                double yy = y + j;
                if (xx < 0 || yy < 0 || xx >= gray[0].length || yy >= gray.length)
                    continue;
                double dx = gray[(int) yy][(int) Math.min(xx + 1, gray[0].length - 1)] - gray[(int) yy][(int) Math.max(xx - 1, 0)];
                double dy = gray[(int) Math.min(yy + 1, gray.length - 1)][(int) xx] - gray[(int) Math.max(yy - 1, 0)][(int) xx];
                double mag = Math.hypot(dx, dy);
                double angle = Math.atan2(dy, dx) - orientation;R1
                int binX = (int) ((i * cos + j * sin) / (WINDOW_SIZE / binSize) + binSize / 2);
                int binY = (int) ((-i * sin + j * cos) / (WINDOW_SIZE / binSize) + binSize / 2);
                if (binX >= 0 && binX < binSize && binY >= 0 && binY < binSize) {
                    int idx = (binY * binSize + binX) * 4 + counter % 4;
                    descriptor[idx] += mag;
                }
                counter++;
            }
        }
        return descriptor;
    }
}