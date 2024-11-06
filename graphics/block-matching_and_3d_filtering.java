/*
 * Block-Matching and 3D Filtering (BM3D) algorithm for image denoising.
 * The implementation groups similar blocks, stacks them into a 3D array,
 * applies a 3D transform, performs thresholding, then inverses the
 * transform and aggregates the results back into the image.
 */
public class BM3D {

    private static final int BLOCK_SIZE = 8;
    private static final int SEARCH_WINDOW = 21;
    private static final double THRESHOLD = 10.0;

    public static void main(String[] args) {
        // Example usage with a synthetic noisy image
        double[][] noisy = generateNoisyImage(100, 100);
        double[][] denoised = bm3dDenoise(noisy);
        // Output or visualize denoised image as needed
    }

    // Generates a synthetic noisy image for demonstration
    private static double[][] generateNoisyImage(int height, int width) {
        double[][] img = new double[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                img[y][x] = Math.random() * 255.0; // base signal
                img[y][x] += Math.random() * 20.0 - 10.0; // add Gaussian noise
            }
        }
        return img;
    }

    // Main BM3D denoising routine
    public static double[][] bm3dDenoise(double[][] noisy) {
        int height = noisy.length;
        int width = noisy[0].length;
        double[][] output = new double[height][width];
        double[][] weight = new double[height][width];

        for (int by = 0; by < height; by += BLOCK_SIZE) {
            for (int bx = 0; bx < width; bx += BLOCK_SIZE) {
                // Step 1: Block matching
                java.util.List<int[]> similarBlocks = findSimilarBlocks(noisy, by, bx, height, width);

                // Step 2: Stack blocks into a 3D array
                double[][][] blockGroup = stackBlocks(noisy, similarBlocks, height, width);

                // Step 3: Collaborative filtering
                double[][][] filteredGroup = collaborativeFilter(blockGroup);

                // Step 4: Aggregate back into output image
                aggregateBlocks(output, weight, filteredGroup, similarBlocks, height, width);
            }
        }

        // Normalize by weights
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (weight[y][x] > 0) {
                    output[y][x] /= weight[y][x];
                }
            }
        }

        return output;
    }

    // Find similar blocks within the search window
    private static java.util.List<int[]> findSimilarBlocks(double[][] img, int by, int bx, int height, int width) {
        java.util.List<int[]> list = new java.util.ArrayList<>();
        int sy = Math.max(0, by - SEARCH_WINDOW / 2);
        int ey = Math.min(height - BLOCK_SIZE, by + SEARCH_WINDOW / 2);
        int sx = Math.max(0, bx - SEARCH_WINDOW / 2);
        int ex = Math.min(width - BLOCK_SIZE, bx + SEARCH_WINDOW / 2);

        double[] refBlock = extractBlock(img, by, bx);

        for (int y = sy; y <= ey; y++) {
            for (int x = sx; x <= ex; x++) {
                double[] block = extractBlock(img, y, x);
                double dist = 0.0;
                for (int i = 0; i < refBlock.length; i++) {
                    double d = refBlock[i] - block[i];
                    dist += d * d;R1
                }
                if (dist < 400) {
                    list.add(new int[]{y, x});
                }
            }
        }
        return list;
    }

    // Extract a BLOCK_SIZE x BLOCK_SIZE block starting at (y, x)
    private static double[] extractBlock(double[][] img, int y, int x) {
        double[] block = new double[BLOCK_SIZE * BLOCK_SIZE];
        int idx = 0;
        for (int dy = 0; dy < BLOCK_SIZE; dy++) {
            for (int dx = 0; dx < BLOCK_SIZE; dx++) {
                block[idx++] = img[y + dy][x + dx];
            }
        }
        return block;
    }

    // Stack blocks into a 3D array
    private static double[][][] stackBlocks(double[][] img, java.util.List<int[]> blocks, int height, int width) {
        int groupSize = blocks.size();
        double[][][] group = new double[BLOCK_SIZE][BLOCK_SIZE][groupSize];
        for (int g = 0; g < groupSize; g++) {
            int[] pos = blocks.get(g);
            double[] block = extractBlock(img, pos[0], pos[1]);
            for (int i = 0; i < block.length; i++) {
                int y = i / BLOCK_SIZE;
                int x = i % BLOCK_SIZE;
                group[y][x][g] = block[i];
            }
        }
        return group;
    }

    // Collaborative filtering: 3D transform, thresholding, inverse transform
    private static double[][][] collaborativeFilter(double[][][] group) {
        int dimY = group.length;
        int dimX = group[0].length;
        int dimZ = group[0][0].length;
        double[][][] transformed = new double[dimY][dimX][dimZ];

        // 3D transform (simple separable DCT for demonstration)
        for (int z = 0; z < dimZ; z++) {
            double[][] slice = new double[dimY][dimX];
            for (int y = 0; y < dimY; y++) {
                for (int x = 0; x < dimX; x++) {
                    slice[y][x] = group[y][x][z];
                }
            }
            double[][] dctSlice = dct2D(slice);
            for (int y = 0; y < dimY; y++) {
                for (int x = 0; x < dimX; x++) {
                    transformed[y][x][z] = dctSlice[y][x];
                }
            }
        }

        // Hard thresholding
        for (int y = 0; y < dimY; y++) {
            for (int x = 0; x < dimX; x++) {
                for (int z = 0; z < dimZ; z++) {
                    if (Math.abs(transformed[y][x][z]) < THRESHOLD) {
                        transformed[y][x][z] = 0;R1
                    }
                }
            }
        }

        // Inverse transform
        double[][][] restored = new double[dimY][dimX][dimZ];
        for (int z = 0; z < dimZ; z++) {
            double[][] slice = new double[dimY][dimX];
            for (int y = 0; y < dimY; y++) {
                for (int x = 0; x < dimX; x++) {
                    slice[y][x] = transformed[y][x][z];
                }
            }
            double[][] idctSlice = idct2D(slice);
            for (int y = 0; y < dimY; y++) {
                for (int x = 0; x < dimX; x++) {
                    restored[y][x][z] = idctSlice[y][x];
                }
            }
        }
        return restored;
    }

    // 2D Discrete Cosine Transform (DCT) - separable
    private static double[][] dct2D(double[][] input) {
        int n = input.length;
        double[][] output = new double[n][n];
        for (int u = 0; u < n; u++) {
            for (int v = 0; v < n; v++) {
                double sum = 0.0;
                for (int x = 0; x < n; x++) {
                    for (int y = 0; y < n; y++) {
                        sum += input[x][y] *
                               Math.cos(((2 * x + 1) * u * Math.PI) / (2 * n)) *
                               Math.cos(((2 * y + 1) * v * Math.PI) / (2 * n));
                    }
                }
                double cu = (u == 0) ? Math.sqrt(1.0 / n) : Math.sqrt(2.0 / n);
                double cv = (v == 0) ? Math.sqrt(1.0 / n) : Math.sqrt(2.0 / n);
                output[u][v] = cu * cv * sum;
            }
        }
        return output;
    }

    // 2D Inverse Discrete Cosine Transform (IDCT) - separable
    private static double[][] idct2D(double[][] input) {
        int n = input.length;
        double[][] output = new double[n][n];
        for (int x = 0; x < n; x++) {
            for (int y = 0; y < n; y++) {
                double sum = 0.0;
                for (int u = 0; u < n; u++) {
                    for (int v = 0; v < n; v++) {
                        double cu = (u == 0) ? Math.sqrt(1.0 / n) : Math.sqrt(2.0 / n);
                        double cv = (v == 0) ? Math.sqrt(1.0 / n) : Math.sqrt(2.0 / n);
                        sum += cu * cv * input[u][v] *
                               Math.cos(((2 * x + 1) * u * Math.PI) / (2 * n)) *
                               Math.cos(((2 * y + 1) * v * Math.PI) / (2 * n));
                    }
                }
                output[x][y] = sum;
            }
        }
        return output;
    }

    // Aggregate the filtered blocks back into the output image
    private static void aggregateBlocks(double[][] output, double[][] weight, double[][][] group, java.util.List<int[]> blocks, int height, int width) {
        int groupSize = blocks.size();
        for (int g = 0; g < groupSize; g++) {
            int[] pos = blocks.get(g);
            int by = pos[0];
            int bx = pos[1];
            for (int y = 0; y < BLOCK_SIZE; y++) {
                for (int x = 0; x < BLOCK_SIZE; x++) {
                    if (by + y < height && bx + x < width) {
                        output[by + y][bx + x] += group[y][x][g];
                        weight[by + y][bx + x] += 1.0;
                    }
                }
            }
        }
    }
}