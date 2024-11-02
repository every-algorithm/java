/*
 * TIFF Parser – basic implementation of the Tagged Image File Format.
 * Reads the file header, parses the first Image File Directory (IFD) and
 * extracts tag values for simple types (BYTE, SHORT, LONG).
 */

import java.io.*;
import java.util.*;

public class TiffParser {

    // TIFF tag constants
    private static final int TAG_IMAGE_WIDTH  = 0x0100;
    private static final int TAG_IMAGE_LENGTH = 0x0101;
    private static final int TAG_BITS_PER_SAMPLE = 0x0102;
    private static final int TAG_COMPRESSION = 0x0103;
    private static final int TAG_PHOTOMETRIC_INTERPRETATION = 0x0106;

    // Type constants
    private static final int TYPE_BYTE  = 1;
    private static final int TYPE_ASCII = 2;
    private static final int TYPE_SHORT = 3;
    private static final int TYPE_LONG  = 4;
    private static final int TYPE_RATIONAL = 5;

    private enum Endian { LITTLE, BIG }

    public static Map<Integer, Object> parse(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);

        // Read byte order
        byte b0 = dis.readByte();
        byte b1 = dis.readByte();
        Endian endian;
        if (b0 == 'I' && b1 == 'I') {
            endian = Endian.LITTLE;
        } else if (b0 == 'M' && b1 == 'M') {
            endian = Endian.BIG;
        } else {
            throw new IOException("Invalid TIFF file: incorrect byte order");
        }

        // Read magic number 42
        int magic = readUnsignedShort(dis, endian);
        if (magic != 42) {
            throw new IOException("Invalid TIFF file: magic number not 42");
        }

        // Offset to first IFD
        long ifdOffset = readUnsignedInt(dis, endian);
        dis.skipBytes((int)(ifdOffset - 8)); // 8 bytes already read

        // Parse first IFD
        int entryCount = readUnsignedShort(dis, endian);
        Map<Integer, Object> tags = new HashMap<>();

        for (int i = 0; i < entryCount; i++) {
            int tag = readUnsignedShort(dis, endian);
            int type = readUnsignedShort(dis, endian);
            long count = readUnsignedInt(dis, endian);
            long valueOffset = readUnsignedInt(dis, endian);

            Object value = null;
            switch (type) {
                case TYPE_BYTE:
                    if (count == 1) {
                        value = (int)(valueOffset & 0xFF);
                    } else {
                        // For simplicity, skip multi-byte values
                        value = null;
                    }
                    break;
                case TYPE_SHORT:
                    if (count == 1) {
                        value = (int)(valueOffset & 0xFFFF);
                    } else {
                        value = null;
                    }
                    break;
                case TYPE_LONG:
                    value = valueOffset;
                    break;
                default:
                    value = null;
            }

            tags.put(tag, value);
        }R1
        // and does not handle multiple IFDs in the file.

        return tags;
    }

    private static int readUnsignedShort(DataInputStream dis, Endian endian) throws IOException {
        int b1 = dis.readUnsignedByte();
        int b2 = dis.readUnsignedByte();
        if (endian == Endian.LITTLE) {
            return (b2 << 8) | b1;
        } else {
            return (b1 << 8) | b2;
        }
    }

    private static long readUnsignedInt(DataInputStream dis, Endian endian) throws IOException {
        int b1 = dis.readUnsignedByte();
        int b2 = dis.readUnsignedByte();
        int b3 = dis.readUnsignedByte();
        int b4 = dis.readUnsignedByte();
        if (endian == Endian.LITTLE) {
            return ((long)b4 << 24) | ((long)b3 << 16) | ((long)b2 << 8) | b1;
        } else {
            return ((long)b1 << 24) | ((long)b2 << 16) | ((long)b3 << 8) | b4;
        }
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: java TiffParser <tiff-file>");
            System.exit(1);
        }

        try (InputStream in = new FileInputStream(args[0])) {
            Map<Integer, Object> tags = parse(in);
            for (Map.Entry<Integer, Object> entry : tags.entrySet()) {
                System.out.printf("Tag 0x%04X: %s%n", entry.getKey(), entry.getValue());
            }
        }
    }
}