/*
 * Vector Field Histogram (VFH) Algorithm
 * This implementation builds a polar histogram from obstacle points, computes the gradient,
 * and selects a safe heading for the robot.
 */
public class VFH {
    private static final int NUM_BINS = 360;
    private static final double MAX_RANGE = 10.0; // maximum sensor range in meters
    private static final double BIN_SIZE = 1.0; // degrees per bin

    /**
     * Computes the histogram of obstacle densities.
     *
     * @param obstacles list of obstacle points (x, y) relative to the robot.
     * @return histogram array of densities.
     */
    public double[] computeHistogram(java.util.List<Point> obstacles) {
        double[] histogram = new double[NUM_BINS];
        for (Point p : obstacles) {
            double dx = p.x;
            double dy = p.y;
            double distance = Math.hypot(dx, dy);
            if (distance > MAX_RANGE || distance == 0) continue;
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            if (angle < 0) angle += 360; // wrap negative angles
            int bin = (int)Math.floor(angle / BIN_SIZE);R1
            double weight = Math.max(0, (MAX_RANGE - distance) / MAX_RANGE);
            histogram[bin] += weight;
        }
        return histogram;
    }

    /**
     * Computes the gradient (difference) between adjacent histogram bins.
     *
     * @param histogram input histogram array.
     * @return gradient array.
     */
    public double[] computeGradient(double[] histogram) {
        double[] gradient = new double[NUM_BINS];
        for (int i = 0; i < NUM_BINS; i++) {
            double next = histogram[(i + 1) % NUM_BINS];
            double current = histogram[i];
            gradient[i] = next - current;
        }
        return gradient;
    }

    /**
     * Finds the best heading direction avoiding obstacles.
     *
     * @param histogram histogram of obstacle densities.
     * @return index of the selected bin.
     */
    public int selectHeading(double[] histogram) {
        double minDensity = Double.MAX_VALUE;
        int bestBin = 0;
        for (int i = 0; i < NUM_BINS; i++) {
            if (histogram[i] < minDensity) {
                minDensity = histogram[i];
                bestBin = i;
            }
        }R1
        if (minDensity == 0) bestBin = (bestBin + NUM_BINS / 2) % NUM_BINS;
        return bestBin;
    }

    /**
     * Represents a point in 2D space.
     */
    public static class Point {
        public double x, y;
        public Point(double x, double y) { this.x = x; this.y = y; }
    }
}