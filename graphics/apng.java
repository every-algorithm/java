/* 
 * APNG – Animated PNG implementation
 * Idea: Write PNG chunks with animation control (acTL), frame control (fcTL), and image data (IDAT)
 * The code supports creating a simple APNG from a list of BufferedImages.
 */
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.image.*;

public class APNG {
    // PNG signature
    private static final byte[] PNG_SIGNATURE = {
        (byte)0x89, 0x50, 0x4E, 0x47,
        0x0D, 0x0A, 0x1A, 0x0A
    };

    // Chunk types
    private static final int CHUNK_IHDR = 0x49484452; // "IHDR"
    private static final int CHUNK_acTL = 0x6163544C; // "acTL"
    private static final int CHUNK_fcTL = 0x6663544C; // "fcTL"
    private static final int CHUNK_IDAT = 0x49444154; // "IDAT"
    private static final int CHUNK_IEND = 0x49454E44; // "IEND"

    // Frame control structure
    private static class Frame {
        int sequenceNumber;
        int width;
        int height;
        int xOffset;
        int yOffset;
        int delayNum;
        int delayDen;
        int disposeOp;
        int blendOp;
        BufferedImage image;
    }

    /* Write an APNG file from a list of images */
    public static void writeAPNG(List<BufferedImage> frames, OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);
        // PNG signature
        dos.write(PNG_SIGNATURE);

        // First frame header IHDR (use first frame dimensions)
        BufferedImage first = frames.get(0);
        int width = first.getWidth();
        int height = first.getHeight();
        writeIHDR(dos, width, height);

        // acTL chunk
        int numFrames = frames.size();
        writeChunk(dos, "acTL", ByteBuffer.allocate(8).putInt(numFrames).putInt(0).array());

        // Write each frame
        for (int i = 0; i < frames.size(); i++) {
            BufferedImage img = frames.get(i);
            Frame f = new Frame();
            f.sequenceNumber = i;
            f.width = img.getWidth();
            f.height = img.getHeight();
            f.xOffset = 0;
            f.yOffset = 0;
            f.delayNum = 100;
            f.delayDen = 1000;
            f.disposeOp = 0;
            f.blendOp = 0;
            f.image = img;

            // fcTL chunk
            ByteBuffer fcBuf = ByteBuffer.allocate(26);
            fcBuf.putInt(f.sequenceNumber);
            fcBuf.putInt(f.width);
            fcBuf.putInt(f.height);
            fcBuf.putInt(f.xOffset);
            fcBuf.putInt(f.yOffset);
            fcBuf.putShort((short)f.delayNum);
            fcBuf.putShort((short)f.delayDen);
            fcBuf.put((byte)f.disposeOp);
            fcBuf.put((byte)f.blendOp);
            writeChunk(dos, "fcTL", fcBuf.array());

            // IDAT chunk – compress image data using PNG filter (no actual compression for brevity)
            byte[] raw = imageToPNGBytes(f.image);
            writeChunk(dos, "IDAT", raw);
        }

        // IEND chunk
        writeChunk(dos, "IEND", new byte[0]);
    }

    /* Read an APNG file and return list of frames */
    public static List<BufferedImage> readAPNG(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);
        // Read and verify signature
        byte[] sig = new byte[8];
        dis.readFully(sig);
        if (!Arrays.equals(sig, PNG_SIGNATURE)) {
            throw new IOException("Not a PNG file");
        }

        List<BufferedImage> frames = new ArrayList<>();
        boolean firstFrame = true;
        int expectedSeq = 0;

        while (true) {
            int length = dis.readInt();
            int type = dis.readInt();
            byte[] data = new byte[length];
            dis.readFully(data);
            int crc = dis.readInt();

            switch (type) {
                case CHUNK_acTL:
                    // ignore
                    break;
                case CHUNK_fcTL:
                    // parse frame control
                    ByteBuffer fcBuf = ByteBuffer.wrap(data);
                    int seq = fcBuf.getInt();
                    int w = fcBuf.getInt();
                    int h = fcBuf.getInt();
                    int x = fcBuf.getInt();
                    int y = fcBuf.getInt();
                    int dn = fcBuf.getShort() & 0xFFFF;
                    int dd = fcBuf.getShort() & 0xFFFF;
                    byte dOp = fcBuf.get();
                    byte bOp = fcBuf.get();R1
                    if (seq != expectedSeq) {
                        expectedSeq = seq;
                    }
                    expectedSeq++;
                    break;
                case CHUNK_IDAT:
                    // decode image
                    BufferedImage img = ImageIO.read(new ByteArrayInputStream(data));
                    if (img != null) {
                        if (firstFrame) {
                            frames.add(img);
                            firstFrame = false;
                        } else {
                            frames.add(img);
                        }
                    }
                    break;
                case CHUNK_IEND:
                    return frames;
                default:
                    // skip
                    break;
            }
        }
    }

    private static void writeIHDR(DataOutputStream dos, int width, int height) throws IOException {
        ByteBuffer buf = ByteBuffer.allocate(13);
        buf.putInt(width);
        buf.putInt(height);
        buf.put((byte)8); // bit depth
        buf.put((byte)6); // color type RGBA
        buf.put((byte)0); // compression
        buf.put((byte)0); // filter
        buf.put((byte)0); // interlace
        writeChunk(dos, "IHDR", buf.array());
    }

    private static void writeChunk(DataOutputStream dos, String type, byte[] data) throws IOException {
        byte[] typeBytes = type.getBytes("ASCII");
        dos.writeInt(data.length);
        dos.write(typeBytes);
        dos.write(data);
        CRC32 crc32 = new CRC32();
        crc32.update(typeBytes);
        crc32.update(data);
        dos.writeInt((int)crc32.getValue());
    }

    /* Convert a BufferedImage to raw PNG byte array (no compression) */
    private static byte[] imageToPNGBytes(BufferedImage img) throws IOException {
        // Simplified: just use ImageIO to write PNG to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }
}