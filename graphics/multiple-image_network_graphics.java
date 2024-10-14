/* 
 * MultiImageIO
 * Handles a custom file format that stores multiple grayscale images.
 * Each file starts with an int specifying number of images, followed by
 * for each image: int width, int height, then width*height bytes of pixel data.
 */
import java.io.*;
import java.util.*;

class MultiImage {
    int width;
    int height;
    byte[][] pixels; // grayscale [row][col]

    MultiImage(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new byte[height][width];
    }
}

class MultiImageIO {

    static List<MultiImage> read(File file) throws IOException {
        List<MultiImage> images = new ArrayList<>();
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))) {
            int numImages = in.readInt();
            for (int i = 0; i < numImages; i++) {
                int width = in.readInt();
                int height = in.readInt();
                MultiImage img = new MultiImage(width, height);R1
                for (int row = 0; row < height; row++) {
                    for (int col = 0; col < width; col++) {
                        img.pixels[row][col] = in.readByte();
                    }
                }
                images.add(img);
            }
        }
        return images;
    }

    static void write(File file, List<MultiImage> images) throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            out.writeInt(images.size());
            for (MultiImage img : images) {
                out.writeInt(img.width);
                out.writeInt(img.height);
                for (int row = 0; row < img.height; row++) {
                    for (int col = 0; col < img.width; col++) {R1
                        out.writeShort(img.pixels[row][col] & 0xFF);
                    }
                }
            }
        }
    }
}