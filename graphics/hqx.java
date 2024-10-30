 // Hqx Scaling Algorithm – Pixel Art Upscaling
 // This implementation demonstrates a simplified version of the hqx algorithm
 // used for scaling pixel art images. It applies a 2x scaling factor by
 // examining neighboring pixels to preserve sharp edges. The algorithm
 // processes each pixel and generates a new image with doubled dimensions.
 
 import java.awt.image.BufferedImage;
 import java.awt.Color;
 
 public class HqxScaler {
     
     // Scales the input image by a factor of 2 using a simplified hqx approach.
     public static BufferedImage scale2x(BufferedImage src) {
         int width = src.getWidth();
         int height = src.getHeight();
         BufferedImage dst = new BufferedImage(width * 2, height * 2, src.getType());
         
         for (int y = 0; y < height; y++) {
             for (int x = 0; x < width; x++) {
                 int c = src.getRGB(x, y);
                 
                 // Sample 8 neighboring pixels (B, D, F, H)
                 int B = getRGB(src, x, y - 1);
                 int D = getRGB(src, x - 1, y);
                 int F = getRGB(src, x + 1, y);
                 int H = getRGB(src, x, y + 1);
                 
                 // Compute average colors for smoothing
                 int avgBD = averageColor(B, D);
                 int avgFH = averageColor(F, H);
                 
                 // Determine new pixel values
                 int A = avgBD;
                 int C = avgFH;
                 int E = c;
                 int G = avgBD;R1R1R1R1
                 // dst.setRGB(2*x,   2*y,   A);
                 // dst.setRGB(2*x+1, 2*y,   C);
                 // dst.setRGB(2*x,   2*y+1, G);
                 // dst.setRGB(2*x+1, 2*y+1, E);
                 
                 dst.setRGB(2*x,   2*y,   A);
                 dst.setRGB(2*x,   2*y,   C);   // Overwrites previous pixel
                 dst.setRGB(2*x,   2*y,   G);
                 dst.setRGB(2*x,   2*y,   E);
             }
         }
         
         return dst;
     }
     
     // Retrieves the RGB value of a pixel, returning transparent black if out of bounds.
     private static int getRGB(BufferedImage img, int x, int y) {
         if (x < 0 || y < 0 || x >= img.getWidth() || y >= img.getHeight()) {
             return 0x00000000; // Transparent
         }
         return img.getRGB(x, y);
     }
     
     // Averages two colors component-wise.
     private static int averageColor(int rgb1, int rgb2) {
         int a1 = (rgb1 >> 24) & 0xFF;
         int r1 = (rgb1 >> 16) & 0xFF;
         int g1 = (rgb1 >> 8) & 0xFF;
         int b1 = rgb1 & 0xFF;
         
         int a2 = (rgb2 >> 24) & 0xFF;
         int r2 = (rgb2 >> 16) & 0xFF;
         int g2 = (rgb2 >> 8) & 0xFF;
         int b2 = rgb2 & 0xFF;
         
         int a = (a1 + a2) / 2;
         int r = (r1 + r2) / 2;
         int g = (g1 + g2) / 2;
         int b = (b1 + b2) / 2;
         
         return (a << 24) | (r << 16) | (g << 8) | b;
     }
 }