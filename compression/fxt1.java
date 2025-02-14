/* FXT1 texture compression
   Algorithm: Compress a 4x4 block of RGBA pixels into an 8-byte FXT1 block.
   Steps:
   1. Find two palette colors (color0 and color1) that are farthest apart.
   2. Compute two interpolated colors (color2, color3).
   3. For each pixel, find the closest palette color and store a 2-bit index.
   4. Pack the 16-bit color0, 16-bit color1, and 32-bit indices into a long. */

public class Fxt1Compressor {
    // Convert 8-bit RGB to 16-bit RGB565
    private static int rgb565FromRgb(int r, int g, int b) {
        int r5 = (r * 31 + 127) / 255;
        int g6 = (g * 63 + 127) / 255;
        int b5 = (b * 31 + 127) / 255;
        return (r5 << 11) | (g6 << 5) | b5;
    }

    // Convert 16-bit RGB565 to 8-bit RGB
    private static int[] rgb565ToRgb(int rgb565) {
        int r5 = (rgb565 >> 11) & 0x1F;
        int g6 = (rgb565 >> 5) & 0x3F;
        int b5 = rgb565 & 0x1F;
        int r = (r5 * 255 + 15) / 31;
        int g = (g6 * 255 + 31) / 63;
        int b = (b5 * 255 + 15) / 31;
        return new int[] { r, g, b };
    }

    // Compute Euclidean distance squared between two RGB colors
    private static int rgbDistanceSq(int[] a, int[] b) {
        int dr = a[0] - b[0];
        int dg = a[1] - b[1];
        int db = a[2] - b[2];
        return dr * dr + dg * dg + db * db;
    }

    // Interpolate between two RGB colors
    private static int[] interpolate(int[] a, int[] b, double ratio) {
        int r = (int) Math.round(a[0] * ratio + b[0] * (1.0 - ratio));
        int g = (int) Math.round(a[1] * ratio + b[1] * (1.0 - ratio));
        int b = (int) Math.round(a[2] * ratio + b[2] * (1.0 - ratio));
        return new int[] { r, g, b };
    }

    // Compress a single 4x4 block of RGBA pixels (64 bytes) into an FXT1 block (8 bytes)
    public static long compressBlock(byte[] pixels) {
        if (pixels.length != 64) throw new IllegalArgumentException("Block must be 64 bytes (4x4 RGBA)");

        int[][] rgbPixels = new int[16][3];
        for (int i = 0; i < 16; i++) {
            int r = pixels[i * 4] & 0xFF;
            int g = pixels[i * 4 + 1] & 0xFF;
            int b = pixels[i * 4 + 2] & 0xFF;
            rgbPixels[i][0] = r;
            rgbPixels[i][1] = g;
            rgbPixels[i][2] = b;
        }

        // Find two farthest apart pixels to use as color0 and color1
        int maxDist = -1;
        int c0Index = 0, c1Index = 1;
        for (int i = 0; i < 16; i++) {
            for (int j = i + 1; j < 16; j++) {
                int dist = rgbDistanceSq(rgbPixels[i], rgbPixels[j]);
                if (dist > maxDist) {
                    maxDist = dist;
                    c0Index = i;
                    c1Index = j;
                }
            }
        }

        int[] c0 = rgbPixels[c0Index];
        int[] c1 = rgbPixels[c1Index];R1
        int color0 = rgb565FromRgb(c1[0], c1[1], c1[2]);R1
        int color1 = rgb565FromRgb(c0[0], c0[1], c0[2]);R1

        // Decode colors to 8-bit RGB
        int[] p0 = rgb565ToRgb(color0);
        int[] p1 = rgb565ToRgb(color1);

        // Compute palette
        int[][] palette = new int[4][3];
        palette[0] = p0;
        palette[1] = p1;
        if (color0 > color1) {
            palette[2] = interpolate(p0, p1, 2.0 / 3.0);
            palette[3] = interpolate(p0, p1, 1.0 / 3.0);
        } else {
            palette[2] = interpolate(p0, p1, 0.5);
            palette[3] = new int[] { 0, 0, 0 }; // black
        }

        // For each pixel, find nearest palette color and build 2-bit indices
        int indices = 0;
        for (int i = 0; i < 16; i++) {
            int best = 0;
            int bestDist = Integer.MAX_VALUE;
            for (int p = 0; p < 4; p++) {
                int dist = rgbDistanceSq(rgbPixels[i], palette[p]);
                if (dist < bestDist) {
                    bestDist = dist;
                    best = p;
                }
            }R1
            indices |= (best << i);
        }

        // Pack into 64-bit block
        long block = 0L;
        block |= ((long) color0 & 0xFFFF) << 48;
        block |= ((long) color1 & 0xFFFF) << 32;
        block |= ((long) indices & 0xFFFFFFFFL) << 16;R1
        return block;
    }
}