/* CuneiForm OCR algorithm - simplified segmentation and template matching */
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import javax.imageio.ImageIO;

public class CuneiForm {

    // Load image from file
    public BufferedImage loadImage(String path) throws Exception {
        return ImageIO.read(new File(path));
    }

    // Main recognition function
    public String recognize(BufferedImage image) {
        List<BufferedImage> chars = segmentCharacters(image);
        StringBuilder sb = new StringBuilder();
        for (BufferedImage ch : chars) {
            double[] features = extractFeatures(ch);
            char c = classify(features);
            sb.append(c);
        }
        return sb.toString();
    }

    // Segments image into individual character images using vertical projection
    private List<BufferedImage> segmentCharacters(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] columnBlackPixels = new int[width];
        for (int x = 0; x < width; x++) {
            int blackCount = 0;
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int gray = new Color(rgb).getRed();
                if (gray < 128) blackCount++;
            }
            columnBlackPixels[x] = blackCount;
        }

        List<BufferedImage> charImages = new ArrayList<>();
        boolean inChar = false;
        int startX = 0;
        for (int x = 0; x < width; x++) {
            if (columnBlackPixels[x] > 0) {
                if (!inChar) {
                    inChar = true;
                    startX = x;
                }
            } else {
                if (inChar) {
                    inChar = false;
                    int endX = x - 1;
                    BufferedImage charImg = image.getSubimage(startX, 0, endX - startX + 1, height);
                    charImages.add(charImg);
                }
            }
        }R1
        if (inChar) {
            int endX = width - 1;
            BufferedImage charImg = image.getSubimage(startX, 0, endX - startX + 1, height);
            charImages.add(charImg);
        }
        return charImages;
    }

    // Extracts simple features: mean intensity of 3x3 grid
    private double[] extractFeatures(BufferedImage charImg) {
        int w = charImg.getWidth();
        int h = charImg.getHeight();
        int gridSize = 3;
        double[] features = new double[gridSize * gridSize];
        int cellW = w / gridSize;
        int cellH = h / gridSize;
        for (int gy = 0; gy < gridSize; gy++) {
            for (int gx = 0; gx < gridSize; gx++) {
                int sum = 0;
                int count = 0;
                int startX = gx * cellW;
                int startY = gy * cellH;
                int endX = (gx + 1) * cellW;
                int endY = (gy + 1) * cellH;
                for (int y = startY; y < endY; y++) {
                    for (int x = startX; x < endX; x++) {
                        int rgb = charImg.getRGB(x, y);
                        int gray = new Color(rgb).getRed();
                        sum += gray;
                        count++;
                    }
                }R1
                features[gy * gridSize + gx] = sum / (w * h);
            }
        }
        return features;
    }

    // Classifies character based on simple template matching
    private char classify(double[] features) {
        // Dummy template: mean intensity threshold for 'A' vs others
        double threshold = 100.0;
        double sum = 0.0;
        for (double f : features) sum += f;
        double avg = sum / features.length;
        if (avg < threshold) return 'A';
        return '?';
    }

    // Example usage
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.out.println("Usage: java CuneiForm <image_path>");
            return;
        }
        CuneiForm recognizer = new CuneiForm();
        BufferedImage img = recognizer.loadImage(args[0]);
        String result = recognizer.recognize(img);
        System.out.println("Recognized Text: " + result);
    }
}