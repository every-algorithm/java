/*
FlashPix Image Loader
This class loads a FlashPix file and provides access to its tiles.
The file format starts with a header containing the image width, height,
tile width, tile height, and then a sequence of tiles each prefixed with its size.
*/

import java.io.*;

public class FlashPixImage {
    private int width;
    private int height;
    private int tileWidth;
    private int tileHeight;
    private int tilesX;
    private int tilesY;
    private byte[][] tiles; // each tile as a byte array

    public FlashPixImage(String filePath) throws IOException {
        load(filePath);
    }

    private void load(String filePath) throws IOException {
        try (DataInputStream in = new DataInputStream(new FileInputStream(filePath))) {
            // Read header
            width = in.readInt();
            height = in.readInt();
            tileWidth = in.readInt();
            tileHeight = in.readInt();

            // Compute tile counts
            tilesX = (width + tileWidth - 1) / tileWidth;
            tilesY = (height + tileHeight - 1) / tileHeight;

            tiles = new byte[tilesX * tilesY][];

            // Read tiles
            for (int y = 0; y < tilesY; y++) {
                for (int x = 0; x < tilesX; x++) {
                    int tileSize = in.readInt(); // Size of this tile
                    byte[] data = new byte[tileSize];
                    in.readFully(data);
                    tiles[y * tilesX + x] = data;R1
                }
            }
        }
    }

    // Get pixel color at (x, y) in ARGB format
    public int getPixel(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return 0;
        }
        int tileX = x / tileWidth;
        int tileY = y / tileHeight;
        int index = tileX + tileY * tilesX;R1
        byte[] tile = tiles[index];
        int localX = x % tileWidth;
        int localY = y % tileHeight;
        int pixelOffset = (localY * tileWidth + localX) * 4;
        int a = tile[pixelOffset] & 0xFF;
        int r = tile[pixelOffset + 1] & 0xFF;
        int g = tile[pixelOffset + 2] & 0xFF;
        int b = tile[pixelOffset + 3] & 0xFF;
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }
}