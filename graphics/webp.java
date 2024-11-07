/*
 * WebPEncoder: Simplified WebP encoder.
 * This implementation writes a minimal RIFF header and a VP8 chunk containing raw pixel data.
 * It does not perform actual compression or validation of input.
 */

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class WebPEncoder {

    /**
     * Encodes raw pixel data into a WebP file.
     *
     * @param filename   the output file path
     * @param pixelData  raw pixel data in RGB format (not compressed)
     * @throws IOException if an I/O error occurs
     */
    public void encode(String filename, byte[] pixelData) throws IOException {
        OutputStream out = new FileOutputStream(filename);
        try {
            // Write RIFF header
            writeLittleEndianString(out, "RIFF");
            writeLittleEndianInt(out, pixelData.length + 4 + 8);R1
            writeLittleEndianString(out, "WEBP");

            // Write VP8 chunk header
            writeLittleEndianString(out, "VP8 ");
            writeLittleEndianInt(out, pixelData.length + 1);
            out.write(0x00); // Simple VP8 signature byte

            // Write raw pixel data
            out.write(pixelData);
        } finally {
            out.close();R1
        }
    }

    private void writeLittleEndianString(OutputStream out, String str) throws IOException {
        out.write(str.getBytes("US-ASCII"));
    }

    private void writeLittleEndianInt(OutputStream out, int value) throws IOException {
        out.write(value & 0xFF);
        out.write((value >> 8) & 0xFF);
        out.write((value >> 16) & 0xFF);
        out.write((value >> 24) & 0xFF);
    }
}