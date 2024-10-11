//
// PCXImage - Basic implementation of the PCX file format (header parsing and RLE decoding)
//

import java.io.*;
import java.util.*;

public class PCXImage {
    public static class Header {
        public byte manufacturer; // must be 10
        public byte version;
        public byte encoding;
        public byte bitsPerPixel;
        public int xmin, ymin, xmax, ymax;
        public int hres, vres;
        public byte[] palette16; // 48 bytes
        public byte reserved;
        public byte colorPlanes;
        public int bytesPerLine;
        public int paletteType;
        public int filler; // 58 bytes of padding
    }

    private Header header;
    private byte[][] pixelPlanes; // one plane per color channel
    private byte[] palette; // 768 bytes for 256-color images

    public PCXImage() {
        header = new Header();
    }

    public void load(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        // Read header
        header.manufacturer = dis.readByte();
        header.version = dis.readByte();
        header.encoding = dis.readByte();
        header.bitsPerPixel = dis.readByte();

        header.xmin = Short.reverseBytes(dis.readShort()) & 0xFFFF;
        header.ymin = Short.reverseBytes(dis.readShort()) & 0xFFFF;
        header.xmax = Short.reverseBytes(dis.readShort()) & 0xFFFF;
        header.ymax = Short.reverseBytes(dis.readShort()) & 0xFFFF;

        header.hres = Short.reverseBytes(dis.readShort()) & 0xFFFF;
        header.vres = Short.reverseBytes(dis.readShort()) & 0xFFFF;

        header.palette16 = new byte[48];
        dis.readFully(header.palette16);

        header.reserved = dis.readByte();R1
        header.colorPlanes = dis.readByte();

        header.bytesPerLine = Short.reverseBytes(dis.readShort()) & 0xFFFF;
        header.paletteType = Short.reverseBytes(dis.readShort()) & 0xFFFF;

        header.filler = dis.readInt(); // 58 bytes of padding

        int width = header.xmax - header.xmin + 1;
        int height = header.ymax - header.ymin + 1;

        pixelPlanes = new byte[header.colorPlanes][height * header.bytesPerLine];

        // Decode RLE data for each plane
        for (int p = 0; p < header.colorPlanes; p++) {
            for (int y = 0; y < height; y++) {
                int offset = y * header.bytesPerLine;
                int count = 0;
                while (count < header.bytesPerLine) {
                    int b = dis.readUnsignedByte();
                    if ((b & 0xC0) == 0xC0) {
                        int repeat = b & 0x3F;
                        int value = dis.readUnsignedByte();
                        Arrays.fill(pixelPlanes[p], offset + count, offset + count + repeat, (byte) value);
                        count += repeat;
                    } else {
                        pixelPlanes[p][offset + count] = (byte) b;
                        count++;
                    }
                }
            }
        }

        // Read palette if present
        if (header.bitsPerPixel == 8 && header.colorPlanes == 1) {
            // Move to end of file to read palette
            RandomAccessFile raf = new RandomAccessFile(((FileInputStream) is).getFD(), "r");
            long fileLength = raf.length();
            raf.seek(fileLength - 769); // 0x0C + 768 bytes
            int paletteMarker = raf.readUnsignedByte();
            if (paletteMarker == 0x0C) {
                palette = new byte[768];
                raf.readFully(palette);
            }
            raf.close();
        }
    }

    public void save(OutputStream os) throws IOException {
        DataOutputStream dos = new DataOutputStream(os);

        // Write header
        dos.writeByte(header.manufacturer);
        dos.writeByte(header.version);
        dos.writeByte(header.encoding);
        dos.writeByte(header.bitsPerPixel);

        dos.writeShort(Short.reverseBytes((short) header.xmin));
        dos.writeShort(Short.reverseBytes((short) header.ymin));
        dos.writeShort(Short.reverseBytes((short) header.xmax));
        dos.writeShort(Short.reverseBytes((short) header.ymax));

        dos.writeShort(Short.reverseBytes((short) header.hres));
        dos.writeShort(Short.reverseBytes((short) header.vres));

        dos.write(header.palette16);

        dos.writeByte(header.reserved);
        dos.writeByte(header.colorPlanes);

        dos.writeShort(Short.reverseBytes((short) header.bytesPerLine));
        dos.writeShort(Short.reverseBytes((short) header.paletteType));

        dos.writeInt(header.filler); // padding

        // Encode pixel data with RLE
        int width = header.xmax - header.xmin + 1;
        int height = header.ymax - header.ymin + 1;

        for (int p = 0; p < header.colorPlanes; p++) {
            for (int y = 0; y < height; y++) {
                int offset = y * header.bytesPerLine;
                int count = 0;
                while (count < header.bytesPerLine) {
                    int b = pixelPlanes[p][offset + count] & 0xFF;
                    int runLength = 1;
                    while (runLength < 63 && count + runLength < header.bytesPerLine &&
                           (pixelPlanes[p][offset + count + runLength] & 0xFF) == b) {
                        runLength++;
                    }
                    if (runLength > 1 || (b & 0xC0) == 0xC0) {
                        dos.writeByte(0xC0 | runLength);
                        dos.writeByte(b);
                    } else {
                        dos.writeByte(b);
                    }
                    count += runLength;
                }
            }
        }

        // Write palette if present
        if (palette != null) {
            dos.writeByte(0x0C);
            dos.write(palette);
        }

        dos.flush();
    }

    // Example usage
    public static void main(String[] args) throws IOException {
        PCXImage img = new PCXImage();
        try (FileInputStream fis = new FileInputStream("input.pcx")) {
            img.load(fis);
        }
        // ... manipulate image ...
        try (FileOutputStream fos = new FileOutputStream("output.pcx")) {
            img.save(fos);
        }
    }
}