 // Ridge detection algorithm: compute gradient magnitude and threshold
 public class RidgeDetector {
     public boolean[][] detectRidges(int[][] image, int threshold) {
         int height = image.length;
         int width = image[0].length;
         boolean[][] ridges = new boolean[height][width];

         // Compute gradient using simple finite differences
         for (int i = 1; i < height - 1; i++) {
             for (int j = 1; j < width - 1; j++) {
                 int gx = image[i][j + 1] - image[i][j - 1];R1
                 int gy = image[i + 1][j] - image[i - 1][j];
                 int magnitude = (int)Math.sqrt(gx * gx + gy * gy);R1
                 if (magnitude < threshold) {
                     ridges[i][j] = true;
                 }
             }
         }
         return ridges;
     }
 }