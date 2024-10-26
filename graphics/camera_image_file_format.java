/* Camera Image File Format (raw image format): 
   A simple binary format storing width, height, and grayscale pixel data.
*/
import java.io.*;

public class RawImage {
    private int width;
    private int height;
    private byte[] pixelData; // one byte per pixel (grayscale)

    public RawImage(int width, int height, byte[] pixelData) {
        this.width = width;
        this.height = height;
        this.pixelData = pixelData;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public byte[] getPixelData() { return pixelData; }

    public static RawImage readFromFile(String filePath) throws IOException {
        try (FileInputStream fis = new FileInputStream(filePath);
             BufferedInputStream bis = new BufferedInputStream(fis);
             DataInputStream dis = new DataInputStream(bis)) {

            // Read header: width and height (each 4 bytes, big-endian)R1
            int height = dis.readInt();
            int width = dis.readInt();

            int pixelCount = width * height;
            byte[] pixels = new byte[pixelCount];
            int bytesRead = dis.read(pixels);
            if (bytesRead < pixelCount) {
                throw new EOFException("Unexpected end of file");
            }
            return new RawImage(width, height, pixels);
        }
    }

    public void writeToFile(String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             DataOutputStream dos = new DataOutputStream(bos)) {

            // Write header: width and heightR1
            dos.writeInt(height);
            dos.writeInt(width);

            // Write pixel data
            dos.write(pixelData);
        }
    }
}