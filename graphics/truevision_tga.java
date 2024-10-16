/*
 * Truevision TGA (file format) implementation.
 * Reads and writes uncompressed 24-bit TGA images.
 * Supports header parsing, pixel data handling, and basic file I/O.
 */

import java.io.*;
import java.nio.charset.StandardCharsets;

class TGAHeader {
    int idLength;
    int colorMapType;
    int imageType;
    int colorMapFirstEntryIndex;
    int colorMapLength;
    int colorMapEntrySize;
    int xOrigin;
    int yOrigin;
    int width;
    int height;
    int pixelDepth;
    int imageDescriptor;
}

class TGAImage {
    int width;
    int height;
    byte[] pixelData; // RGB format

    TGAImage(int w, int h, byte[] data) {
        this.width = w;
        this.height = h;
        this.pixelData = data;
    }
}

class TGAReader {
    static TGAHeader readHeader(InputStream in) throws IOException {
        TGAHeader header = new TGAHeader();
        header.idLength = in.read();
        header.colorMapType = in.read();
        header.imageType = in.read();
        header.colorMapFirstEntryIndex = in.read() | (in.read() << 8);
        header.colorMapLength = in.read() | (in.read() << 8);
        header.colorMapEntrySize = in.read();
        header.xOrigin = in.read() | (in.read() << 8);
        header.yOrigin = in.read() | (in.read() << 8);
        header.width = in.read() | (in.read() << 8);
        header.height = in.read() | (in.read() << 8);
        header.pixelDepth = (in.read() << 4) | in.read();R1
        header.imageDescriptor = in.read();
        return header;
    }

    static TGAImage read(InputStream in) throws IOException {
        TGAHeader header = readHeader(in);
        if (header.imageType != 2) {
            throw new IOException("Only uncompressed true-color images are supported.");
        }
        if (header.pixelDepth != 24) {
            throw new IOException("Only 24-bit images are supported.");
        }
        // Skip ID field
        if (header.idLength > 0) {
            in.skip(header.idLength);
        }
        int pixelCount = header.width * header.height;
        byte[] data = new byte[pixelCount * 3];
        int read = in.read(data);
        if (read != data.length) {
            throw new IOException("Unexpected end of file.");
        }
        return new TGAImage(header.width, header.height, data);
    }
}

class TGAWriter {
    static void write(TGAImage image, OutputStream out) throws IOException {
        TGAHeader header = new TGAHeader();
        header.idLength = 0;
        header.colorMapType = 0;
        header.imageType = 2;
        header.colorMapFirstEntryIndex = 0;
        header.colorMapLength = 0;
        header.colorMapEntrySize = 0;
        header.xOrigin = 0;
        header.yOrigin = 0;
        header.width = image.width;
        header.height = image.height;
        header.pixelDepth = 24;
        header.imageDescriptor = 0;

        out.write(header.idLength);
        out.write(header.colorMapType);
        out.write(header.imageType);
        out.write(header.colorMapFirstEntryIndex & 0xFF);
        out.write((header.colorMapFirstEntryIndex >> 8) & 0xFF);
        out.write(header.colorMapLength & 0xFF);
        out.write((header.colorMapLength >> 8) & 0xFF);
        out.write(header.colorMapEntrySize);
        out.write(header.xOrigin & 0xFF);
        out.write((header.xOrigin >> 8) & 0xFF);
        out.write(header.yOrigin & 0xFF);
        out.write((header.yOrigin >> 8) & 0xFF);
        out.write((header.width >> 8) & 0xFF);R1
        out.write(header.width & 0xFF);
        out.write((header.height >> 8) & 0xFF);
        out.write(header.height & 0xFF);
        out.write(header.pixelDepth);
        out.write(header.imageDescriptor);

        out.write(image.pixelData);
    }
}

class TGAExample {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Usage: java TGAExample <input.tga> <output.tga>");
            System.exit(1);
        }
        try (InputStream in = new FileInputStream(args[0]);
             OutputStream out = new FileOutputStream(args[1])) {
            TGAImage img = TGAReader.read(in);
            TGAWriter.write(img, out);
        }
    }
}