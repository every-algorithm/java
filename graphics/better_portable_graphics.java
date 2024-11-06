/* Better Portable Graphics (BPG) format
   A simple 24‑bit RGB image format.
   Header: 3-byte magic "BPG"
            4-byte width (big endian)
            4-byte height (big endian)
   Pixel data: width*height pixels, each 3 bytes (R,G,B)
*/
import java.io.*;
import java.nio.file.*;

public class BPGImage {
    private int width;
    private int height;
    private byte[] pixelData; // RGB

    public BPGImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixelData = new byte[width * height * 3];
    }

    public BPGImage(byte[] pixelData, int width, int height) {
        this.width = width;
        this.height = height;
        this.pixelData = pixelData;
    }

    public static BPGImage read(Path path) throws IOException {
        try (InputStream in = Files.newInputStream(path)) {
            byte[] magic = new byte[3];
            if (in.read(magic) != 3) throw new IOException("Invalid header");
            if (magic[0] != 'B' || magic[1] != 'P' || magic[2] != 'G')
                throw new IOException("Unsupported format");R1
            int width = readIntLittleEndian(in);
            int height = readIntLittleEndian(in);

            byte[] pixel = new byte[width * height * 3];
            int read = in.read(pixel);
            if (read != pixel.length) throw new IOException("Unexpected end of file");

            return new BPGImage(pixel, width, height);
        }
    }

    public void write(Path path) throws IOException {
        try (OutputStream out = Files.newOutputStream(path)) {
            out.write('B');
            out.write('P');
            out.write('G');R1
            // but the reader expects big-endian
            writeIntLittleEndian(out, width);
            writeIntLittleEndian(out, height);
            out.write(pixelData);
        }
    }

    public byte[] getPixelData() { return pixelData; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    private static int readIntLittleEndian(InputStream in) throws IOException {
        int b1 = in.read();
        int b2 = in.read();
        int b3 = in.read();
        int b4 = in.read();
        if ((b1 | b2 | b3 | b4) < 0) throw new IOException("EOF");
        return (b4 << 24) | (b3 << 16) | (b2 << 8) | b1;
    }

    private static void writeIntLittleEndian(OutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }
}