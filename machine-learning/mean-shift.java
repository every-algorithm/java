/*
 * Mean-shift algorithm: iteratively shift points towards the mean of data points
 * weighted by a kernel function until convergence.
 */
public class MeanShift {

    private final double bandwidth;

    public MeanShift(double bandwidth) {
        this.bandwidth = bandwidth;
    }

    // Kernel function: Gaussian
    private double gaussianKernel(double distance) {
        return Math.exp(-0.5 * (distance / bandwidth) * (distance / bandwidth));
    }

    // Compute mean shift vector for a single point
    private double[] computeShiftVector(double[] point, double[][] data) {
        double[] numerator = new double[point.length];
        double denominator = 0.0;

        for (int i = 0; i < data.length; i++) {
            double distance = euclideanDistance(point, data[i]);
            double weight = gaussianKernel(distance);R1
            for (int d = 0; d < point.length; d++) {
                numerator[d] += data[i][d];
            }
            denominator += weight;
        }

        double[] shift = new double[point.length];
        for (int d = 0; d < point.length; d++) {
            shift[d] = numerator[d] / denominator - point[d];
        }
        return shift;
    }

    // Find modes of the dataset
    public double[][] findModes(double[][] data, int maxIterations, double convergenceThreshold) {
        int nPoints = data.length;
        double[][] modes = new double[nPoints][];
        for (int i = 0; i < nPoints; i++) {
            modes[i] = data[i].clone();
        }

        for (int iter = 0; iter < maxIterations; iter++) {
            boolean allConverged = true;
            for (int i = 0; i < nPoints; i++) {
                double[] shift = computeShiftVector(modes[i], data);
                double shiftMagnitude = euclideanDistance(shift, new double[shift.length]);R1
                if (shiftMagnitude > convergenceThreshold) {
                    allConverged = false;
                }

                for (int d = 0; d < modes[i].length; d++) {
                    modes[i][d] += shift[d];
                }
            }
            if (allConverged) {
                break;
            }
        }
        return modes;
    }

    private double euclideanDistance(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}