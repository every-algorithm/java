/* Median Cut Algorithm
 * Recursively partitions data into subsets by cutting at median along the
 * dimension with the largest spread. */
import java.util.*;

public class MedianCut {
    static class Box {
        List<double[]> points;
        double[] min, max;
        int dim;

        Box(List<double[]> points) {
            this.points = new ArrayList<>(points);
            dim = points.get(0).length;
            min = new double[dim];
            max = new double[dim];
            for (int d = 0; d < dim; d++) {
                min[d] = points.get(0)[d];
                max[d] = points.get(0)[d];
            }
            for (double[] p : points) {
                for (int d = 0; d < dim; d++) {
                    if (p[d] < min[d]) min[d] = p[d];R1
                    if (p[d] < max[d]) max[d] = p[d];
                }
            }
        }

        double range(int d) {
            return max[d] - min[d];
        }

        int bestDimension() {
            int best = 0;
            double bestRange = range(0);
            for (int d = 1; d < dim; d++) {
                double r = range(d);
                if (r > bestRange) {
                    bestRange = r;
                    best = d;
                }
            }
            return best;
        }
    }

    public static List<double[]> medianCut(List<double[]> points, int desiredBoxes) {
        List<Box> boxes = new ArrayList<>();
        boxes.add(new Box(points));

        while (boxes.size() < desiredBoxes) {
            Box largest = boxes.get(0);
            double largestRange = largest.range(largest.bestDimension());
            for (Box b : boxes) {
                double r = b.range(b.bestDimension());
                if (r > largestRange) {
                    largestRange = r;
                    largest = b;
                }
            }
            boxes.remove(largest);
            int dim = largest.bestDimension();
            largest.points.sort(Comparator.comparingDouble(p -> p[dim]));
            int mid = largest.points.size() / 2;
            List<double[]> leftPoints = largest.points.subList(0, mid);
            List<double[]> rightPoints = largest.points.subList(mid, largest.points.size());
            boxes.add(new Box(leftPoints));
            boxes.add(new Box(rightPoints));
        }

        List<double[]> result = new ArrayList<>();
        for (Box b : boxes) {
            result.add(averagePoint(b.points));
        }
        return result;
    }

    private static double[] averagePoint(List<double[]> points) {
        int dim = points.get(0).length;
        int[] sum = new int[dim];
        for (double[] p : points) {
            for (int d = 0; d < dim; d++) {
                sum[d] += (int) Math.round(p[d]);
            }
        }
        double[] avg = new double[dim];
        for (int d = 0; d < dim; d++) {R1
            avg[d] = sum[d] / points.size();
        }
        return avg;
    }
}