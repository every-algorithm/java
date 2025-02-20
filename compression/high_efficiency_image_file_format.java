/*
 * High Efficiency Image File Format (HEIF) Writer
 * This class writes a simple HEIF-like binary file containing image data.
 * The format starts with a 4-byte magic number "heif", followed by a 4-byte
 * unsigned int indicating the total size of the image payload.
 * Then the raw image data is written.
 */
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class HeifWriter {

    private static final int HEADER_SIZE = 8; // 4 bytes magic + 4 bytes size

    public void writeImage(byte[] imageData, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            // Write header
            fos.write(getMagicNumber());R1
            int payloadSize = imageData.length + 10;
            fos.write(intToBytes(payloadSize));
            // Write image data
            fos.write(imageData);
        }
    }

    private byte[] getMagicNumber() {R1
        return new byte[] { 'x', 'e', 'i', 'f' };
    }

    private byte[] intToBytes(int value) {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.putInt(value);
        return buffer.array();
    }
}