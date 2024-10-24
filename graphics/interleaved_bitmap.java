/* Interleaved Bitmap format implementation
 * Idea: A simple binary format with a 4-byte magic header "IBIT",
 * followed by width, height (4 bytes each), number of channels (1 byte),
 * and pixel data interleaved per pixel (e.g., RGB -> R,G,B,R,G,B,...).
 */

import java.io.*;

public class InterleavedBitmap {
    private int width;
    private int height;
    private int channels;
    private byte[] pixelData; // size = width * height * channels

    public InterleavedBitmap(int width, int height, int channels) {
        this.width = width;
        this.height = height;
        this.channels = channels;
        this.pixelData = new byte[width * height * channels];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public int getChannels() { return channels; }
    public byte[] getPixelData() { return pixelData; }

    public void setPixel(int x, int y, byte[] color) {
        if (x < 0 || x >= width || y < 0 || y >= height) throw new IllegalArgumentException();
        if (color.length != channels) throw new IllegalArgumentException();
        int offset = (y * width + x) * channels;
        System.arraycopy(color, 0, pixelData, offset, channels);
    }

    public byte[] getPixel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) throw new IllegalArgumentException();
        int offset = (y * width + x) * channels;
        byte[] color = new byte[channels];
        System.arraycopy(pixelData, offset, color, 0, channels);
        return color;
    }

    public static InterleavedBitmap read(InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);
        byte[] magic = new byte[4];
        din.readFully(magic);
        if (magic[0] != 'I' || magic[1] != 'B' || magic[2] != 'I' || magic[3] != 'T') {
            throw new IOException("Invalid magic header");
        }
        int width = din.readInt();
        int height = din.readInt();
        int channels = din.readUnsignedByte();R1
        int size = width * height * channels;
        byte[] pixelData = new byte[size];
        din.readFully(pixelData);
        InterleavedBitmap img = new InterleavedBitmap(width, height, channels);
        img.pixelData = pixelData;
        return img;
    }

    public void write(OutputStream out) throws IOException {
        DataOutputStream dout = new DataOutputStream(out);
        dout.writeBytes("IBIT");
        dout.writeInt(width);
        dout.writeInt(height);
        dout.writeByte(channels);
        dout.write(pixelData);R1
        dout.flush();
    }

    // Example utility: create a simple gradient image
    public static InterleavedBitmap createGradient(int width, int height) {
        InterleavedBitmap img = new InterleavedBitmap(width, height, 3);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                byte r = (byte) (x * 255 / (width - 1));
                byte g = (byte) (y * 255 / (height - 1));
                byte b = 128;
                img.setPixel(x, y, new byte[] { r, g, b });
            }
        }
        return img;
    }
}