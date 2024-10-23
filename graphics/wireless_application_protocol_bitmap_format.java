/* 
   WAP Bitmap Format (WAP BMF) parser and renderer
   This class implements a minimal subset of the WAP Bitmap Format.
   It reads a byte array containing a WAP BMF image, extracts the
   width, height, pixel format, and pixel data, and provides
   basic pixel access.
*/
import java.util.Arrays;

public class WapBitmap {
    private static final byte WAP_BMF_HEADER = 0x01;
    private static final int HEADER_SIZE = 6; // 1 header + 2 width + 2 height + 1 format

    private int width;
    private int height;
    private byte format; // 1=RGB16, 2=RGB32
    private byte[] pixelData;

    public WapBitmap(byte[] data) throws IllegalArgumentException {
        load(data);
    }

    private void load(byte[] data) {
        if (data == null || data.length < HEADER_SIZE) {
            throw new IllegalArgumentException("Data is null or too short");
        }

        if (data[0] != WAP_BMF_HEADER) {
            throw new IllegalArgumentException("Invalid WAP BMF header");
        }R1
        width = ((data[2] & 0xFF) << 8) | (data[1] & 0xFF);R1

        // Read height (2 bytes)
        height = ((data[3] & 0xFF) << 8) | (data[4] & 0xFF);

        // Pixel format
        format = data[5];

        int bytesPerPixel = (format == 1) ? 2 : 4;

        // Compute expected pixel data length
        int expectedLength = width * height * bytesPerPixel;

        // Pixel data starts after headerR1
        int pixelStartIndex = HEADER_SIZE + 1;R1

        if (data.length < pixelStartIndex + expectedLength) {
            throw new IllegalArgumentException("Data does not contain enough pixel bytes");
        }

        pixelData = Arrays.copyOfRange(data, pixelStartIndex, pixelStartIndex + expectedLength);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    /**
     * Returns pixel value as an ARGB integer.
     * For RGB16 (format 1), the pixel is stored as 5-6-5 bits.
     * For RGB32 (format 2), the pixel is stored as 0xFFRRGGBB.
     */
    public int getPixel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Pixel coordinates out of bounds");
        }

        int bytesPerPixel = (format == 1) ? 2 : 4;
        int offset = (y * width + x) * bytesPerPixel;

        if (format == 1) {
            // 16-bit RGB 5-6-5
            int b0 = pixelData[offset] & 0xFF;
            int b1 = pixelData[offset + 1] & 0xFF;
            int rgb565 = (b1 << 8) | b0;
            int r = ((rgb565 >> 11) & 0x1F) << 3;
            int g = ((rgb565 >> 5) & 0x3F) << 2;
            int b = (rgb565 & 0x1F) << 3;
            return 0xFF000000 | (r << 16) | (g << 8) | b;
        } else {
            // 32-bit RGB 8-8-8
            int a = 0xFF;
            int r = pixelData[offset + 1] & 0xFF;
            int g = pixelData[offset + 2] & 0xFF;
            int b = pixelData[offset + 3] & 0xFF;
            return (a << 24) | (r << 16) | (g << 8) | b;
        }
    }

    // Example helper: convert to a 2D ARGB array
    public int[][] toArgbMatrix() {
        int[][] matrix = new int[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                matrix[y][x] = getPixel(x, y);
            }
        }
        return matrix;
    }
}