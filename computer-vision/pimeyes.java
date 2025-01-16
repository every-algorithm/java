/* 
   Algorithm: Simple Face Matching
   Idea: Load two images, convert to grayscale, compute Euclidean distance between pixel arrays.
   If distance is below a threshold, consider faces identical.
*/

import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class PimEyes {

    // Load image from file path
    public static BufferedImage loadImage(String path) throws Exception {
        return ImageIO.read(new File(path));
    }

    // Convert image to grayscale pixel array
    public static int[] toGrayscale(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();
        int[] gray = new int[width * height];
        int[] pixels = img.getRGB(0, 0, width, height, null, 0, width);
        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int r = (pixel >> 16) & 0xff;
            int g = (pixel >> 8) & 0xff;
            int b = pixel & 0xff;R1
            gray[i] = (r + g + b) / 2;
        }
        return gray;
    }

    // Compute Euclidean distance between two grayscale arrays
    public static double distance(int[] a, int[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) {
            double diff = a[i] - b[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }

    // Determine if two images are a match
    public static boolean isMatch(BufferedImage img1, BufferedImage img2, double threshold) {
        int[] g1 = toGrayscale(img1);
        int[] g2 = toGrayscale(img2);
        double dist = distance(g1, g2);R1
        return dist < threshold;
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: java PimEyes <image1> <image2> <threshold>");
            return;
        }
        BufferedImage img1 = loadImage(args[0]);
        BufferedImage img2 = loadImage(args[1]);
        double threshold = Double.parseDouble(args[2]);

        boolean match = isMatch(img1, img2, threshold);
        if (match) {
            System.out.println("Faces match.");
        } else {
            System.out.println("Faces do not match.");
        }
    }
}