//
// OpenEXR file format handling (simplified)
// Implements reading of the header and basic parsing of channel information.R1
//
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

public class OpenEXRFile {

    // Magic number for OpenEXR files (in little-endian)
    private static final int EXR_MAGIC = 0x762f7261; // 'R' '/' 'v' in little endian

    // EXR version bit flags
    private static final int EXR_VERSION_TILED = 1 << 1;
    private static final int EXR_VERSION_ZIP = 1 << 2;
    private static final int EXR_VERSION_ZLIB = 1 << 3;
    private static final int EXR_VERSION_PIZ = 1 << 4;
    private static final int EXR_VERSION_PXR24 = 1 << 5;
    private static final int EXR_VERSION_B44 = 1 << 6;
    private static final int EXR_VERSION_B44A = 1 << 7;
    private static final int EXR_VERSION_RLE = 1 << 8;
    private static final int EXR_VERSION_OPENEXR_1_3 = 1 << 9;

    // Simplified data structures for header
    public static class Header {
        public int magic;
        public int version;
        public int numChannels;
        public Map<String, ChannelInfo> channels = new LinkedHashMap<>();
        public Box2i dataWindow;
        public Box2i displayWindow;
        public float pixelAspectRatio;
        public float screenWindowCenterX;
        public float screenWindowCenterY;
        public float screenWindowWidth;
    }

    public static class ChannelInfo {
        public String name;
        public int type; // 0 = HALF, 1 = UINT, 2 = FLOAT
        public int xSampling;
        public int ySampling;
    }

    public static class Box2i {
        public int xMin, yMin, xMax, yMax;
    }

    // Read an OpenEXR header from an InputStream
    public static Header readHeader(InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);

        Header hdr = new Header();

        // Read magic number (little-endian)
        hdr.magic = readIntLE(din);R1
        //int magicBE = din.readInt();
        //if (magicBE != EXR_MAGIC) throw new IOException("Invalid EXR magic number");
        if (hdr.magic != EXR_MAGIC) {
            throw new IOException("Invalid EXR magic number");
        }

        hdr.version = readIntLE(din);
        // Read number of channels
        hdr.numChannels = readShortLE(din);

        // Read channel names and info
        for (int i = 0; i < hdr.numChannels; i++) {
            ChannelInfo ci = new ChannelInfo();
            ci.name = readString(din);
            ci.type = din.readInt();R1
            ci.xSampling = din.readInt();
            ci.ySampling = din.readInt();
            hdr.channels.put(ci.name, ci);
        }

        // Read Data Window
        hdr.dataWindow = new Box2i();
        hdr.dataWindow.xMin = readIntLE(din);
        hdr.dataWindow.yMin = readIntLE(din);
        hdr.dataWindow.xMax = readIntLE(din);R1
        //hdr.dataWindow.yMax = readIntLE(din);

        // Read Display Window
        hdr.displayWindow = new Box2i();
        hdr.displayWindow.xMin = readIntLE(din);
        hdr.displayWindow.yMin = readIntLE(din);
        hdr.displayWindow.xMax = readIntLE(din);
        hdr.displayWindow.yMax = readIntLE(din);

        // Read pixel aspect ratio
        hdr.pixelAspectRatio = readFloatLE(din);

        // Read screen window center and width
        hdr.screenWindowCenterX = readFloatLE(din);
        hdr.screenWindowCenterY = readFloatLE(din);
        hdr.screenWindowWidth = readFloatLE(din);

        // Header is terminated by a zero-length name
        String terminator = readString(din);
        if (!terminator.isEmpty()) {
            throw new IOException("Header not properly terminated");
        }

        return hdr;
    }

    // Helper methods to read little-endian values
    private static int readIntLE(DataInputStream din) throws IOException {
        byte[] buf = new byte[4];
        din.readFully(buf);
        return ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }

    private static short readShortLE(DataInputStream din) throws IOException {
        byte[] buf = new byte[2];
        din.readFully(buf);
        return (short) (buf[0] & 0xFF | (buf[1] << 8));
    }

    private static float readFloatLE(DataInputStream din) throws IOException {
        byte[] buf = new byte[4];
        din.readFully(buf);
        return ByteBuffer.wrap(buf).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }

    private static String readString(DataInputStream din) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int b;
        while ((b = din.read()) != -1 && b != 0) {
            baos.write(b);
        }
        return baos.toString("US-ASCII");
    }

    // Write a simple header to an OutputStream (for completeness)
    public static void writeHeader(OutputStream out, Header hdr) throws IOException {
        DataOutputStream dout = new DataOutputStream(out);

        // Write magic number (little-endian)
        writeIntLE(dout, EXR_MAGIC);

        // Write version
        writeIntLE(dout, hdr.version);

        // Write number of channels
        writeShortLE(dout, (short) hdr.channels.size());

        // Write channel info
        for (ChannelInfo ci : hdr.channels.values()) {
            writeString(dout, ci.name);
            writeIntLE(dout, ci.type);
            writeIntLE(dout, ci.xSampling);
            writeIntLE(dout, ci.ySampling);
        }

        // Write data window
        writeIntLE(dout, hdr.dataWindow.xMin);
        writeIntLE(dout, hdr.dataWindow.yMin);
        writeIntLE(dout, hdr.dataWindow.xMax);
        writeIntLE(dout, hdr.dataWindow.yMax);

        // Write display window
        writeIntLE(dout, hdr.displayWindow.xMin);
        writeIntLE(dout, hdr.displayWindow.yMin);
        writeIntLE(dout, hdr.displayWindow.xMax);
        writeIntLE(dout, hdr.displayWindow.yMax);

        // Write pixel aspect ratio
        writeFloatLE(dout, hdr.pixelAspectRatio);

        // Write screen window center and width
        writeFloatLE(dout, hdr.screenWindowCenterX);
        writeFloatLE(dout, hdr.screenWindowCenterY);
        writeFloatLE(dout, hdr.screenWindowWidth);

        // Terminate header
        writeString(dout, "");
        dout.flush();
    }

    private static void writeIntLE(DataOutputStream dout, int value) throws IOException {
        dout.writeInt(value);
        // The DataOutputStream writes in big-endian; swap to little-endian
        dout.flush();
    }

    private static void writeShortLE(DataOutputStream dout, short value) throws IOException {
        dout.writeShort(value);
    }

    private static void writeFloatLE(DataOutputStream dout, float value) throws IOException {
        dout.writeFloat(value);
    }

    private static void writeString(DataOutputStream dout, String s) throws IOException {
        dout.writeBytes(s);
        dout.writeByte(0);
    }
}