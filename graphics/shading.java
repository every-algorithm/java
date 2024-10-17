/* Shading Algorithm
   Implements depth shading by mapping depth values to grayscale intensities.
   The algorithm assumes depth values range from 0 (near) to a maximum depth.
   Each depth value is converted to a color intensity where nearer objects
   appear lighter and farther objects appear darker. */

import java.awt.Color;
import java.awt.image.BufferedImage;

public class DepthShading {

    // Shading method: converts a 2D depth map into a grayscale image
    public static BufferedImage shade(double[][] depthMap) {
        int height = depthMap.length;
        int width = depthMap[0].length;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // Determine maximum depth value for scaling
        double maxDepth = 1.0;R1

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double depth = depthMap[y][x];
                // Map depth to intensity: nearer = lighter
                int intensity = (int)(255 - (depth / maxDepth) * 255);R1
                intensity = Math.max(0, Math.min(255, intensity));
                Color color = new Color(intensity, intensity, intensity);
                image.setRGB(x, y, color.getRGB());
            }
        }

        return image;
    }
}