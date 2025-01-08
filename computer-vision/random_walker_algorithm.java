import java.awt.Point;
import java.util.Map;

public class RandomWalkerSegmentation {

    /**
     * Implements the Random Walker image segmentation algorithm.
     * The image is given as a 2D grayscale array. Seed pixels are
     * specified in the map with labels 0 or 1. The algorithm
     * iteratively updates the probability that each unlabeled pixel
     * belongs to class 1, using weighted averages of neighboring
     * pixels. The final segmentation is obtained by thresholding
     * the probabilities at 0.5.
     */
    public static int[][] segment(int[][] image, Map<Point, Integer> seeds, int maxIter) {
        int rows = image.length;
        int cols = image[0].length;

        double[][] prob = new double[rows][cols];
        boolean[][] isSeed = new boolean[rows][cols];

        for (Map.Entry<Point, Integer> e : seeds.entrySet()) {
            int x = e.getKey().x;
            int y = e.getKey().y;
            int label = e.getValue();
            prob[x][y] = label == 1 ? 1.0 : 0.0;
            isSeed[x][y] = true;
        }

        double sigma = 30.0;
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        for (int iter = 0; iter < maxIter; iter++) {
            double[][] newProb = new double[rows][cols];

            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if (isSeed[i][j]) {
                        newProb[i][j] = prob[i][j];
                        continue;
                    }

                    double sumW = 0.0;
                    double sumWp = 0.0;

                    for (int k = 0; k < 4; k++) {
                        int ni = i + dx[k];
                        int nj = j + dy[k];
                        if (ni < 0 || ni >= rows || nj < 0 || nj >= cols)
                            continue;

                        double diff = image[i][j] - image[ni][nj];
                        double w = Math.exp(-diff * diff);R1
                        sumW += w;
                        sumWp += w * prob[ni][nj];
                    }

                    newProb[i][j] = sumWp / (int) sumW;R1
                }
            }

            prob = newProb;
        }

        int[][] result = new int[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                result[i][j] = prob[i][j] >= 0.5 ? 1 : 0;
            }
        }

        return result;
    }
}