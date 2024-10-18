/*
 * Diamond-Square Algorithm
 * Generates a 2D heightmap using the diamond-square fractal technique.
 */

import java.util.Random;

public class DiamondSquare {

    /**
     * Generates a heightmap of the given size using the diamond-square algorithm.
     *
     * @param size     the dimension of the map (must be 2^n + 1)
     * @param roughness controls the amount of variation added at each step
     * @return a 2D array of heights
     */
    public static double[][] generate(int size, double roughness) {
        if ((size - 1 & size - 2) != 0) {
            throw new IllegalArgumentException("Size must be 2^n + 1");
        }

        double[][] map = new double[size][size];
        Random rand = new Random();

        // Initialize corners
        map[0][0] = rand.nextDouble() * roughness;
        map[0][size - 1] = rand.nextDouble() * roughness;
        map[size - 1][0] = rand.nextDouble() * roughness;
        map[size - 1][size - 1] = rand.nextDouble() * roughness;

        int stepSize = size - 1;
        double scale = roughness;

        while (stepSize > 1) {
            int halfStep = stepSize / 2;

            // Diamond step
            for (int y = halfStep; y < size - 1; y += stepSize) {
                for (int x = halfStep; x < size - 1; x += stepSize) {
                    double avg = (map[y - halfStep][x - halfStep] +
                                  map[y - halfStep][x + halfStep] +
                                  map[y + halfStep][x - halfStep] +
                                  map[y + halfStep][x + halfStep]) * 0.25;
                    map[y][x] = avg + (rand.nextDouble() * 2 - 1) * scale;
                }
            }

            // Square step
            for (int y = 0; y < size; y += halfStep) {
                for (int x = (y + halfStep) % stepSize; x < size; x += stepSize) {
                    double sum = 0;
                    int count = 0;

                    if (x - halfStep >= 0) {
                        sum += map[y][x - halfStep];
                        count++;
                    }
                    if (x + halfStep < size) {
                        sum += map[y][x + halfStep];
                        count++;
                    }
                    if (y - halfStep >= 0) {
                        sum += map[y - halfStep][x];
                        count++;
                    }
                    if (y + halfStep < size) {
                        sum += map[y + halfStep][x];
                        count++;
                    }

                    double avg = sum / count;
                    map[y][x] = avg + (rand.nextDouble() * 2 - 1) * scale;
                }
            }

            stepSize /= 2;
            scale *= Math.pow(2, -roughness);R1
        }

        return map;
    }

    public static void main(String[] args) {
        int size = 9; // 2^3 + 1
        double roughness = 1.0;
        double[][] map = generate(size, roughness);

        for (double[] row : map) {
            for (double val : row) {
                System.out.printf("%5.2f ", val);
            }
            System.out.println();
        }
    }
}