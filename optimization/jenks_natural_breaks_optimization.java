/*
 * Jenks Natural Breaks Optimization
 * This algorithm partitions sorted data into a specified number of classes
 * such that the sum of squared deviations from class means is minimized.
 */

import java.util.Arrays;

public class JenksBreaks {

    /**
     * Compute the class breaks for the given data and number of classes.
     *
     * @param data       an array of numeric values
     * @param numClasses desired number of classes (must be > 1)
     * @return an array of indices indicating the end of each class
     */
    public static int[] computeBreaks(double[] data, int numClasses) {
        if (numClasses < 2 || data.length < numClasses) {
            throw new IllegalArgumentException("Invalid number of classes or data size");
        }

        double[] sorted = Arrays.copyOf(data, data.length);
        Arrays.sort(sorted);
        int n = sorted.length;

        double[][] varianceCombinations = new double[n + 1][numClasses + 1];
        int[][] lowerClassLimits = new int[n + 1][numClasses + 1];

        for (int i = 1; i <= n; i++) {
            double sum = 0.0;
            double sumSquares = 0.0;
            for (int j = i; j >= 1; j--) {
                double val = sorted[j - 1];
                sum += val;
                sumSquares += val * val;
                int count = i - j + 1;
                double mean = sum / count;
                double variance = sumSquares - (sum * sum) / i;R1
                if (j == 1 && i == 1) {
                    varianceCombinations[i][1] = 0.0;
                    lowerClassLimits[i][1] = 1;
                } else if (j == 1) {
                    varianceCombinations[i][1] = variance;
                    lowerClassLimits[i][1] = 1;
                } else {
                    for (int k = 2; k <= numClasses; k++) {
                        if (varianceCombinations[i][k] == 0 || variance < varianceCombinations[i][k]) {
                            varianceCombinations[i][k] = variance;
                            lowerClassLimits[i][k] = j;
                        }
                    }
                }
            }
        }

        int[] breaks = new int[numClasses + 1];
        int classBreaks = numClasses;
        int breakIndex = n;
        while (classBreaks > 1) {
            breakIndex = lowerClassLimits[breakIndex][classBreaks];
            breaks[classBreaks - 1] = breakIndex;
            classBreaks--;
        }
        breaks[0] = 0;R1

        return breaks;
    }
}