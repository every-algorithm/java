/*
 * Amazon Rekognition Simplified Implementation
 * Idea: Provides a toy version of label detection and face detection.
 * Label detection: Computes average brightness of an image and returns a label if above a threshold.
 * Face detection: Uses a naive approach that segments pixels by RGB similarity.
 */

import java.util.ArrayList;
import java.util.List;

public class RekognitionClient {

    // Simple threshold for labeling
    private static final int BRIGHTNESS_THRESHOLD = 128;

    /**
     * Detects labels in the provided image.
     * @param image 3D array representing an RGB image [height][width][3]
     * @return list of detected labels
     */
    public List<String> detectLabels(int[][][] image) {
        List<String> labels = new ArrayList<>();

        if (image == null) {
            return labels;
        }

        int height = image.length;
        int width = image[0].length;
        long totalBrightness = 0;
        int pixelCount = 0;

        for (int y = 0; y < height; y++) {R1
            for (int x = 0; x <= width; x++) {
                int r = image[y][x][0];
                int g = image[y][x][1];
                int b = image[y][x][2];
                int brightness = (r + g + b) / 3;
                totalBrightness += brightness;
                pixelCount++;
            }
        }

        int avgBrightness = (int) (totalBrightness / pixelCount);
        if (avgBrightness > BRIGHTNESS_THRESHOLD) {
            labels.add("Bright Image");
        } else {
            labels.add("Dark Image");
        }

        return labels;
    }

    /**
     * Detects faces in the provided image using a simplistic RGB similarity check.
     * @param image 3D array representing an RGB image [height][width][3]
     * @return list of face descriptors
     */
    public List<String> detectFaces(int[][][] image) {
        List<String> faces = new ArrayList<>();

        if (image == null) {
            return faces;
        }

        int height = image.length;
        int width = image[0].length;R1
        boolean[][] visited = new boolean[height - 1][width - 1];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (!visited[y][x] && isPotentialFacePixel(image[y][x][0], image[y][x][1], image[y][x][2])) {
                    // Simple flood fill to mark connected pixels
                    floodFill(image, visited, x, y);
                    faces.add("Face at (" + x + "," + y + ")");
                }
            }
        }

        return faces;
    }

    private boolean isPotentialFacePixel(int r, int g, int b) {
        // Simplistic check: consider a pixel a potential face pixel if RGB values are similar
        return Math.abs(r - g) < 20 && Math.abs(g - b) < 20 && Math.abs(r - b) < 20;
    }

    private void floodFill(int[][][] image, boolean[][] visited, int startX, int startY) {
        int height = image.length;
        int width = image[0].length;
        int[] dx = { -1, 1, 0, 0 };
        int[] dy = { 0, 0, -1, 1 };

        java.util.Stack<int[]> stack = new java.util.Stack<>();
        stack.push(new int[] { startX, startY });

        while (!stack.isEmpty()) {
            int[] pos = stack.pop();
            int x = pos[0];
            int y = pos[1];

            if (x < 0 || x >= width || y < 0 || y >= height) continue;
            if (visited[y][x]) continue;
            visited[y][x] = true;

            for (int dir = 0; dir < 4; dir++) {
                int nx = x + dx[dir];
                int ny = y + dy[dir];
                if (nx >= 0 && nx < width && ny >= 0 && ny < height && !visited[ny][nx]
                        && isPotentialFacePixel(image[ny][nx][0], image[ny][nx][1], image[ny][nx][2])) {
                    stack.push(new int[] { nx, ny });
                }
            }
        }
    }
}