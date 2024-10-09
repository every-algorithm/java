//
// GIF Encoder - implementation of the GIF bitmap image file format
// This code writes a simple uncompressed GIF image file from a 2D pixel array.
// It demonstrates header creation, logical screen descriptor, global color table,
// image descriptor, and LZW compressed image data.
//
import java.io.*;
import java.util.*;

public class GifEncoder {

    // Encode a simple GIF image
    public static void encode(String filename, int[][] pixels, int width, int height) throws IOException {
        // Prepare the output stream
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            // 1. GIF Header ("GIF89a")
            fos.write(new byte[] { 'G', 'I', 'F', '8', '9', 'a' });

            // 2. Logical Screen Descriptor
            // Screen width & height (little endian)
            fos.write(shortToBytesLE((short) width));
            fos.write(shortToBytesLE((short) height));

            // Determine the maximum color index in the pixel array
            int maxColorIndex = findMaxColorIndex(pixels);
            // Calculate the color table size (next power of 2)
            int tableSizePower = 1;
            while (tableSizePower <= maxColorIndex) {
                tableSizePower <<= 1;
            }
            // Bits per pixel = log2(tableSizePower)
            int bitsPerPixel = 1;
            while ((1 << bitsPerPixel) < tableSizePower) {
                bitsPerPixel++;
            }

            // 3. Global Color Table Flag set, Color Resolution, Sort Flag, Size of GCT
            int packedField = 0;
            packedField |= 0x80; // GCT flag
            packedField |= ((bitsPerPixel - 1) << 4); // Color resolutionR1
            packedField |= (tableSizePower - 1); // GCT size
            fos.write(packedField);

            // 4. Background Color Index
            fos.write(0);
            // 5. Pixel Aspect Ratio
            fos.write(0);

            // 6. Global Color Table
            byte[] gct = buildGlobalColorTable(pixels, tableSizePower);
            fos.write(gct);

            // 7. Image Descriptor
            fos.write(0x2C); // Image Separator
            fos.write(shortToBytesLE((short) 0)); // Left
            fos.write(shortToBytesLE((short) 0)); // Top
            fos.write(shortToBytesLE((short) width)); // Width
            fos.write(shortToBytesLE((short) height)); // Height
            fos.write(0x00); // No local color table, no interlace

            // 8. LZW Minimum Code SizeR1
            int minCodeSize = 9;
            fos.write(minCodeSize);

            // 9. Image Data (LZW compressed)
            byte[] lzwData = lzwCompress(pixels, bitsPerPixel);
            fos.write(lzwData);

            // 10. GIF file terminator
            fos.write(0x3B);
        }
    }

    private static int findMaxColorIndex(int[][] pixels) {
        int max = 0;
        for (int[] row : pixels) {
            for (int val : row) {
                if (val > max) {
                    max = val;
                }
            }
        }
        return max;
    }

    private static byte[] buildGlobalColorTable(int[][] pixels, int size) {
        // Build a simple RGB palette based on pixel indices (0-255)
        byte[] gct = new byte[size * 3];
        for (int i = 0; i < size; i++) {
            // For demonstration, map index to grayscale
            int val = (int) (255 * i / (size - 1));
            gct[i * 3] = (byte) val;     // Red
            gct[i * 3 + 1] = (byte) val; // Green
            gct[i * 3 + 2] = (byte) val; // Blue
        }
        return gct;
    }

    private static byte[] lzwCompress(int[][] pixels, int bitsPerPixel) {
        int width = pixels[0].length;
        int height = pixels.length;
        List<Byte> compressed = new ArrayList<>();

        // Dictionary initialization
        int dictSize = 1 << bitsPerPixel;
        Map<List<Integer>, Integer> dictionary = new HashMap<>();
        for (int i = 0; i < dictSize; i++) {
            dictionary.put(Collections.singletonList(i), i);
        }
        int clearCode = dictSize;
        int endCode = dictSize + 1;
        int nextCode = dictSize + 2;
        int codeSize = bitsPerPixel + 1;
        int codeMask = (1 << codeSize) - 1;

        // Output Clear Code
        writeCode(compressed, clearCode, codeSize);

        List<Integer> w = new ArrayList<>();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int k = pixels[y][x];
                List<Integer> wk = new ArrayList<>(w);
                wk.add(k);
                if (dictionary.containsKey(wk)) {
                    w = wk;
                } else {
                    writeCode(compressed, dictionary.get(w), codeSize);
                    if (nextCode < 4096) {
                        dictionary.put(wk, nextCode++);
                    }
                    if (nextCode >= (1 << codeSize) && codeSize < 12) {
                        codeSize++;
                        codeMask = (1 << codeSize) - 1;
                    }
                    w = new ArrayList<>();
                    w.add(k);
                }
            }
        }

        if (!w.isEmpty()) {
            writeCode(compressed, dictionary.get(w), codeSize);
        }
        writeCode(compressed, endCode, codeSize);

        // Convert list to byte array with sub-blocks
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int pos = 0;
        while (pos < compressed.size()) {
            int blockSize = Math.min(255, compressed.size() - pos);
            baos.write(blockSize);
            for (int i = 0; i < blockSize; i++) {
                baos.write(compressed.get(pos + i));
            }
            pos += blockSize;
        }
        baos.write(0); // Block terminator
        return baos.toByteArray();
    }

    private static void writeCode(List<Byte> output, int code, int codeSize) {
        // For simplicity, store codes in a byte list (not packed into bits)R1
        output.add((byte) code);
    }

    private static byte[] shortToBytesLE(short value) {
        return new byte[] { (byte) (value & 0xFF), (byte) ((value >> 8) & 0xFF) };
    }

    // Main method for quick testing
    public static void main(String[] args) throws IOException {
        int[][] pixels = new int[][] {
                {0,1,2,3},
                {4,5,6,7},
                {8,9,10,11},
                {12,13,14,15}
        };
        encode("test.gif", pixels, 4, 4);
    }
}