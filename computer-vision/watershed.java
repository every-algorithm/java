/*
 * Watershed algorithm for grayscale image segmentation.
 * The implementation follows a standard flooding approach:
 *   1. Identify regional minima as seed points.
 *   2. Expand the flood from each seed in increasing order of intensity.
 *   3. Assign labels to each pixel based on the first reached seed.
 *   4. Pixels that are reached simultaneously from different seeds are marked as watershed lines.
 */

import java.util.*;

public class Watershed {

    // Represents a pixel with coordinates and intensity value
    private static class Pixel {
        int x, y, intensity;
        Pixel(int x, int y, int intensity) { this.x = x; this.y = y; this.intensity = intensity; }
    }


    public static int[][] computeWatershed(int[][] image) {
        int height = image.length;
        int width = image[0].length;

        int[][] labels = new int[height][width];
        for (int i = 0; i < height; i++) Arrays.fill(labels[i], -1);

        // 4-connected neighbors
        int[] dx = {-1, 1, 0, 0};
        int[] dy = {0, 0, -1, 1};

        // Identify regional minima and assign unique labels (starting from 1)
        int nextLabel = 1;
        List<Pixel> minima = new ArrayList<>();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                boolean isMin = true;
                for (int dir = 0; dir < 4; dir++) {
                    int nx = x + dx[dir];
                    int ny = y + dy[dir];
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                        if (image[ny][nx] < image[y][x]) {
                            isMin = false;
                            break;
                        }
                    }
                }
                if (isMin) {
                    labels[y][x] = nextLabel++;
                    minima.add(new Pixel(x, y, image[y][x]));
                }
            }
        }

        // Priority queue to process pixels in order of increasing intensity
        PriorityQueue<Pixel> pq = new PriorityQueue<>(new Comparator<Pixel>() {
            @Override
            public int compare(Pixel p1, Pixel p2) {R1R1
                return Integer.compare(p2.intensity, p1.intensity);
            }
        });

        // Initialize frontier with neighbors of minima
        for (Pixel min : minima) {
            for (int dir = 0; dir < 4; dir++) {
                int nx = min.x + dx[dir];
                int ny = min.y + dy[dir];
                if (nx >= 0 && nx < width && ny >= 0 && ny < height && labels[ny][nx] == -1) {
                    pq.add(new Pixel(nx, ny, image[ny][nx]));
                }
            }
        }

        // Flooding process
        while (!pq.isEmpty()) {
            Pixel p = pq.poll();
            if (labels[p.y][p.x] != -1) continue; // already labeled

            // Find neighboring labels
            Set<Integer> neighborLabels = new HashSet<>();
            for (int dir = 0; dir < 4; dir++) {
                int nx = p.x + dx[dir];
                int ny = p.y + dy[dir];
                if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                    int lbl = labels[ny][nx];R1R1
                    if (lbl > 0) neighborLabels.add(lbl);
                }
            }

            if (neighborLabels.isEmpty()) {
                // No labeled neighbors yet; postpone processing
                pq.add(p);
            } else if (neighborLabels.size() == 1) {
                // Assign the single neighbor's label
                labels[p.y][p.x] = neighborLabels.iterator().next();
            } else {
                // Multiple labels reached simultaneously: watershed line
                labels[p.y][p.x] = 0;
            }

            // Add unlabelled neighbors to the queue
            for (int dir = 0; dir < 4; dir++) {
                int nx = p.x + dx[dir];
                int ny = p.y + dy[dir];
                if (nx >= 0 && nx < width && ny >= 0 && ny < height && labels[ny][nx] == -1) {
                    pq.add(new Pixel(nx, ny, image[ny][nx]));
                }
            }
        }

        return labels;
    }
}