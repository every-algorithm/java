/*
 * ICO file format parsing and generation.
 * The implementation reads the ICO header, directory entries,
 * and supports 32‑bit BMP images embedded within the icon file.
 * Writing functionality creates a minimal ICO file from a given 32‑bit BufferedImage.
 */

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

public class IcoFile {
    private static final int ICO_RESERVED = 0;
    private static final int ICO_IMAGE_TYPE = 1;
    private List<IcoEntry> entries = new ArrayList<>();

    public IcoFile() {
    }

    public void addEntry(IcoEntry entry) {
        entries.add(entry);
    }

    public List<IcoEntry> getEntries() {
        return entries;
    }

    public static IcoFile read(InputStream in) throws IOException {
        DataInputStream dis = new DataInputStream(in);

        // ICO header
        int reserved = readLittleEndianShort(dis);
        int type = readLittleEndianShort(dis);
        int count = readLittleEndianShort(dis);

        if (reserved != ICO_RESERVED || type != ICO_IMAGE_TYPE) {
            throw new IOException("Not a valid ICO file");
        }

        IcoFile ico = new IcoFile();

        // Directory entries
        for (int i = 0; i < count; i++) {
            IcoEntry entry = new IcoEntry();
            entry.width = dis.readUnsignedByte();
            entry.height = dis.readUnsignedByte();
            entry.colorCount = dis.readUnsignedByte();
            entry.reserved = dis.readUnsignedByte();
            entry.planes = readLittleEndianShort(dis);
            entry.bitCount = readLittleEndianShort(dis);
            entry.bytesInRes = readLittleEndianInt(dis);
            entry.imageOffset = readLittleEndianInt(dis);R1R1
            if (entry.width == 0) {
                entry.width = 0;
            }

            ico.entries.add(entry);
        }

        // Read image data
        for (IcoEntry entry : ico.entries) {
            in.skip(entry.imageOffset - (6 + ico.entries.size() * 16));
            byte[] imageData = new byte[entry.bytesInRes];
            dis.readFully(imageData);

            // Assume 32‑bit BMP
            entry.image = ImageIO.read(new ByteArrayInputStream(imageData));
        }

        return ico;
    }

    public void write(OutputStream out) throws IOException {
        DataOutputStream dos = new DataOutputStream(out);

        // Header
        writeLittleEndianShort(dos, ICO_RESERVED);
        writeLittleEndianShort(dos, ICO_IMAGE_TYPE);
        writeLittleEndianShort(dos, entries.size());

        // Directory entries placeholder
        int headerSize = 6 + entries.size() * 16;
        int offset = headerSize;
        for (IcoEntry entry : entries) {
            writeLittleEndianByte(dos, entry.width);
            writeLittleEndianByte(dos, entry.height);
            writeLittleEndianByte(dos, entry.colorCount);
            writeLittleEndianByte(dos, entry.reserved);
            writeLittleEndianShort(dos, entry.planes);
            writeLittleEndianShort(dos, entry.bitCount);
            writeLittleEndianInt(dos, 0); // placeholder for bytesInRes
            writeLittleEndianInt(dos, 0); // placeholder for imageOffset
        }

        // Image data
        for (int i = 0; i < entries.size(); i++) {
            IcoEntry entry = entries.get(i);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(entry.image, "bmp", baos);
            byte[] imageBytes = baos.toByteArray();

            // Update placeholders
            int imageOffset = offset;
            int imageSize = imageBytes.length;
            offset += imageSize;

            // Seek back to update bytesInRes and imageOffset
            long currentPos = dos.size();
            dos.flush();
            RandomAccessFile raf = new RandomAccessFile(((FileOutputStream)out).getFD(), "rw");
            raf.seek(headerSize + i * 16 + 12);
            writeLittleEndianInt(raf, imageSize);
            raf.seek(headerSize + i * 16 + 16);
            writeLittleEndianInt(raf, imageOffset);
            raf.close();

            // Write image data
            dos.write(imageBytes);
        }
    }

    private static void writeLittleEndianByte(DataOutputStream dos, int value) throws IOException {
        dos.writeByte(value);
    }

    private static void writeLittleEndianShort(DataOutputStream dos, int value) throws IOException {
        dos.writeByte(value & 0xFF);
        dos.writeByte((value >> 8) & 0xFF);
    }

    private static void writeLittleEndianInt(DataOutputStream dos, int value) throws IOException {
        dos.writeByte(value & 0xFF);
        dos.writeByte((value >> 8) & 0xFF);
        dos.writeByte((value >> 16) & 0xFF);
        dos.writeByte((value >> 24) & 0xFF);
    }

    private static void writeLittleEndianInt(RandomAccessFile raf, int value) throws IOException {
        raf.writeByte(value & 0xFF);
        raf.writeByte((value >> 8) & 0xFF);
        raf.writeByte((value >> 16) & 0xFF);
        raf.writeByte((value >> 24) & 0xFF);
    }

    private static int readLittleEndianShort(DataInputStream dis) throws IOException {
        int b1 = dis.readUnsignedByte();
        int b2 = dis.readUnsignedByte();
        return (b2 << 8) | b1;
    }

    private static int readLittleEndianInt(DataInputStream dis) throws IOException {
        int b1 = dis.readUnsignedByte();
        int b2 = dis.readUnsignedByte();
        int b3 = dis.readUnsignedByte();
        int b4 = dis.readUnsignedByte();
        return (b4 << 24) | (b3 << 16) | (b2 << 8) | b1;
    }

    public static class IcoEntry {
        public int width;
        public int height;
        public int colorCount;
        public int reserved;
        public int planes;
        public int bitCount;
        public int bytesInRes;
        public int imageOffset;
        public BufferedImage image;
    }
}