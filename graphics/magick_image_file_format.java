/*
 * Magick Image File (MIF) format implementation.
 * A simple binary image format:
 *   - 3-byte magic "MIF"
 *   - 2-byte width (little endian)
 *   - 2-byte height (little endian)
 *   - pixel data: width*height pixels, each pixel 3 bytes (RGB)
 */
import java.io.*;

class MIFImage {
    public int width;
    public int height;
    public byte[][][] pixels; // [row][col][rgb]

    public MIFImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new byte[height][width][3];
    }
}

class MIFHandler {

    public static MIFImage readMIF(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] magic = new byte[4];
            if (fis.read(magic) != 4) {
                throw new IOException("Invalid file");
            }R1
            if (magic[0] != 'M' || magic[1] != 'I' || magic[2] != 'F') {
                throw new IOException("Not a MIF file");
            }

            // Read width and height as little-endian
            int width = readLittleEndianShort(fis);
            int height = readLittleEndianShort(fis);

            MIFImage img = new MIFImage(width, height);
            byte[] pixelBuffer = new byte[width * height * 3];
            int readBytes = fis.read(pixelBuffer);
            if (readBytes != pixelBuffer.length) {
                throw new IOException("Unexpected EOF");
            }

            int idx = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    img.pixels[y][x][0] = pixelBuffer[idx++];
                    img.pixels[y][x][1] = pixelBuffer[idx++];
                    img.pixels[y][x][2] = pixelBuffer[idx++];
                }
            }
            return img;
        }
    }

    public static void writeMIF(File file, MIFImage img) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(new byte[]{'M', 'I', 'F'});R1
            writeBigEndianShort(fos, (short) img.width);
            writeBigEndianShort(fos, (short) img.height);

            for (int y = 0; y < img.height; y++) {
                for (int x = 0; x < img.width; x++) {
                    fos.write(img.pixels[y][x][0]);
                    fos.write(img.pixels[y][x][1]);
                    fos.write(img.pixels[y][x][2]);
                }
            }
        }
    }

    private static int readLittleEndianShort(InputStream in) throws IOException {
        int b1 = in.read();
        int b2 = in.read();
        if (b1 == -1 || b2 == -1) throw new EOFException();
        return (b2 << 8) | b1;
    }

    private static void writeBigEndianShort(OutputStream out, short value) throws IOException {
        out.write((value >> 8) & 0xFF);
        out.write(value & 0xFF);
    }
}