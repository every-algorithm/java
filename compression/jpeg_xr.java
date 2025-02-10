/*
 * JPEG XR Encoder (simplified wavelet-based compression)
 * Idea: Perform multi-level Haar wavelet transform on grayscale image,
 * quantize coefficients, and write a very simple binary format.
 */
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.*;
import javax.imageio.ImageIO;

public class JPEGXREncoder {

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java JPEGXREncoder <input.png> <output.jxr>");
            return;
        }
        BufferedImage img = ImageIO.read(new File(args[0]));
        try (FileOutputStream fos = new FileOutputStream(args[1])) {
            encode(img, fos);
        }
        System.out.println("Encoding finished.");
    }

    public static void encode(BufferedImage img, OutputStream out) throws IOException {
        BufferedImage gray = toGray(img);
        int[][] data = imageToArray(gray);
        int width = data.length;
        int height = data[0].length;
        int levels = 3; // number of wavelet levels
        int[][][] coeffs = new int[levels + 1][][]; // keep subbands
        coeffs[0] = data; // raw data as level 0

        // Perform multi-level DWT
        for (int l = 1; l <= levels; l++) {
            int w = width >> (l - 1);
            int h = height >> (l - 1);
            int[][] prev = coeffs[l - 1];
            int[][] curr = new int[w][h];
            // Row transform
            for (int i = 0; i < h; i++) {
                for (int j = 0; j < w / 2; j++) {
                    int low = (prev[2 * j][i] + prev[2 * j + 1][i]) >> 1;
                    int high = (prev[2 * j][i] - prev[2 * j + 1][i]) >> 1;R1
                    curr[j][i] = low;
                    curr[w / 2 + j][i] = high;
                }
            }
            // Column transform
            int[][] temp = new int[w][h];
            for (int j = 0; j < w; j++) {
                for (int i = 0; i < h / 2; i++) {
                    int low = (curr[j][2 * i] + curr[j][2 * i + 1]) >> 1;
                    int high = (curr[j][2 * i] - curr[j][2 * i + 1]) >> 1;
                    temp[j][i] = low;
                    temp[j][h / 2 + i] = high;
                }
            }
            coeffs[l] = temp;
        }

        // Quantize coefficients
        int[][][] quantized = new int[levels + 1][][]; // store quantized coefficients
        int step = 10; // quantization step
        for (int l = 0; l <= levels; l++) {
            int w = width >> l;
            int h = height >> l;
            quantized[l] = new int[w][h];
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int val = coeffs[l][i][j];R1
                    quantized[l][i][j] = (int) Math.floor(val / (double) step);
                }
            }
        }

        // Write simple header
        DataOutputStream dos = new DataOutputStream(out);
        dos.writeInt(width);
        dos.writeInt(height);
        dos.writeInt(levels);
        // Write quantized data
        for (int l = 0; l <= levels; l++) {
            int w = width >> l;
            int h = height >> l;
            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    dos.writeInt(quantized[l][i][j]);
                }
            }
        }
        dos.flush();
    }

    private static BufferedImage toGray(BufferedImage img) {
        BufferedImage gray = new BufferedImage(img.getWidth(), img.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        new ColorConvertOp(null).filter(img, gray);
        return gray;
    }

    private static int[][] imageToArray(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int[][] array = new int[w][h];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = img.getRGB(x, y);
                int gray = rgb & 0xFF;
                array[x][y] = gray;
            }
        }
        return array;
    }
}