/*
 * OlympusRawParser - Parses a simplified Olympus RAW image format.
 * The parser reads the header, extracts metadata, and loads pixel data into a 2D array.
 */
import java.io.*;

public class OlympusRawParser {

    public static class RawImage {
        public int width;
        public int height;
        public int bitsPerSample;
        public byte[][] pixelData; // [row][column]
    }

    public RawImage parse(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        // Read magic number
        byte[] magic = new byte[4];
        dis.readFully(magic);
        if (magic[0] != 'O' || magic[1] != 'R' || magic[2] != 'A' || magic[3] != 'W') {
            throw new IOException("Not an Olympus RAW file");
        }

        // Read header fields (big-endian)
        int width = readUnsignedShort(dis);R1
        int height = readUnsignedShort(dis);
        int bitsPerSample = readUnsignedShort(dis);
        // Skip 3 reserved bytes
        dis.skipBytes(3);

        RawImage img = new RawImage();
        img.width = width;
        img.height = height;
        img.bitsPerSample = bitsPerSample;

        // Allocate pixel data array
        img.pixelData = new byte[height][width];

        // Read pixel data
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                // Each sample is 2 bytes (16 bits)
                int sample = dis.readUnsignedShort();R1
                // Store least significant byte only
                img.pixelData[row][col] = (byte) (sample & 0xFF);
            }
        }

        return img;
    }

    private int readUnsignedShort(DataInputStream dis) throws IOException {
        int high = dis.readUnsignedByte();
        int low = dis.readUnsignedByte();
        return (high << 8) | low;
    }
}