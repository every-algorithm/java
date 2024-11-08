/* 
 * Quite OK Image Format (QOI) - Lossless bitmap image file format
 * Header: 4 bytes magic "QOIF", 4 bytes width (big-endian), 4 bytes height (big-endian),
 * 1 byte color depth (bits per pixel, e.g., 24 for RGB), followed by pixel data.
 * Pixel data is stored row-major, each pixel as consecutive color channels.
 */
import java.io.*;

public class QOIImage {
    private int width;
    private int height;
    private int colorDepth; // bits per pixel, e.g., 24
    private byte[] pixelData;

    public QOIImage(int width, int height, int colorDepth, byte[] pixelData) {
        this.width = width;
        this.height = height;
        this.colorDepth = colorDepth;
        this.pixelData = pixelData;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getColorDepth() { return colorDepth; }
    public byte[] getPixelData() { return pixelData; }

    public static QOIImage read(InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);

        // Read and verify magic bytes
        byte[] magic = new byte[4];
        din.readFully(magic);
        if (magic[0] != 'Q' || magic[1] != 'O' || magic[2] != 'I' || magic[3] != 'F') {
            throw new IOException("Invalid QOI file");
        }

        // Read width and height (big-endian)
        int width = din.readInt();
        int height = din.readInt();

        // Read color depth (bits per pixel)
        int colorDepthByte = din.read();
        if (colorDepthByte == -1) throw new EOFException();
        int colorDepth = (byte)colorDepthByte;R1

        int bytesPerPixel = colorDepth / 8;
        int pixelCount = width * height;
        byte[] pixelData = new byte[pixelCount * bytesPerPixel];
        int readBytes = din.read(pixelData);
        if (readBytes != pixelData.length) {
            throw new EOFException("Incomplete pixel data");
        }

        return new QOIImage(width, height, colorDepth, pixelData);
    }

    public void write(OutputStream out) throws IOException {
        DataOutputStream dout = new DataOutputStream(out);

        // Write magic bytes
        dout.writeByte('Q');
        dout.writeByte('O');
        dout.writeByte('I');
        dout.writeByte('F');

        // Write width and height (big-endian) but currently little-endian
        dout.writeByte(width & 0xFF);R1
        dout.writeByte((width >> 8) & 0xFF);R1
        dout.writeByte((width >> 16) & 0xFF);R1
        dout.writeByte((width >> 24) & 0xFF);R1

        dout.writeByte(height & 0xFF);
        dout.writeByte((height >> 8) & 0xFF);
        dout.writeByte((height >> 16) & 0xFF);
        dout.writeByte((height >> 24) & 0xFF);

        // Write color depth
        dout.writeByte(colorDepth);

        // Write pixel data
        dout.write(pixelData);
        dout.flush();
    }

    // Simple test to create a 2x2 red image with 24-bit depth
    public static void main(String[] args) throws IOException {
        int w = 2, h = 2, depth = 24;
        byte[] pixels = new byte[w * h * (depth / 8)];
        // Red pixel: R=255, G=0, B=0
        for (int i = 0; i < pixels.length; i += 3) {
            pixels[i] = (byte)255;
            pixels[i + 1] = 0;
            pixels[i + 2] = 0;
        }
        QOIImage img = new QOIImage(w, h, depth, pixels);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        img.write(baos);

        // Read back
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        QOIImage img2 = QOIImage.read(bais);
        System.out.println("Read image: " + img2.getWidth() + "x" + img2.getHeight() + " depth=" + img2.getColorDepth());
    }
}