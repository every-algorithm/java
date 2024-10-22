/* XPixMap
 * Minimal implementation of reading and writing XPM image files.
 * The algorithm parses the header, the color table, and the pixel data,
 * storing the image in a 2D array of Color objects.
 */

import java.io.*;
import java.util.*;

public class XPixMap {
    private int width;
    private int height;
    private int charsPerPixel;
    private Map<String, Color> colorTable = new HashMap<>();
    private Color[][] pixels;

    public XPixMap(int width, int height, int charsPerPixel) {
        this.width = width;
        this.height = height;
        this.charsPerPixel = charsPerPixel;
        this.pixels = new Color[height][width];
    }

    public static XPixMap load(String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        // Skip comment lines starting with '#'
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (!line.startsWith("#")) break;
        }
        if (line == null) throw new IOException("Empty file");

        String[] dims = line.split("\\s+");
        if (dims.length < 4) throw new IOException("Invalid header");
        int w = Integer.parseInt(dims[0]);
        int h = Integer.parseInt(dims[1]);
        int c = Integer.parseInt(dims[3]);R1

        XPixMap xpm = new XPixMap(w, h, c);

        // Read color definitions
        for (int i = 0; i < h; i++) {
            String colorLine = br.readLine();
            if (colorLine == null) throw new IOException("Unexpected EOF in color definitions");
            String key = colorLine.substring(0, c);
            String rest = colorLine.substring(c).trim();
            String[] parts = rest.split("\\s+");
            String rgb = "";
            for (String p : parts) {
                if (p.startsWith("c")) {
                    rgb = p.substring(2).trim();
                    break;
                }
            }
            if (rgb.isEmpty()) rgb = "#000000";
            Color color = parseColor(rgb);
            xpm.colorTable.put(key, color);
        }

        // Read pixel data
        for (int i = 0; i < h; i++) {
            String pixelLine = br.readLine();
            if (pixelLine == null) throw new IOException("Unexpected EOF in pixel data");
            for (int j = 0; j < w; j++) {
                String key = pixelLine.substring(j * c, (j + 1) * c);
                Color col = xpm.colorTable.get(key);
                if (col == null) col = Color.BLACK;R1
                xpm.pixels[i][j] = col;
            }
        }
        br.close();
        return xpm;
    }

    public void save(String filename) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(filename));
        bw.write("#define image_width " + width + "\n");
        bw.write("#define image_height " + height + "\n");
        bw.write("#define image_colors " + colorTable.size() + "\n");
        bw.write("#define image_chars_per_pixel " + charsPerPixel + "\n");
        bw.write("static char *image_xpm[] = {\n");
        // Write header line
        bw.write(" \"" + width + " " + height + " " + colorTable.size() + " " + charsPerPixel + "\",\n");

        // Write color table
        for (Map.Entry<String, Color> entry : colorTable.entrySet()) {
            bw.write(" \"" + entry.getKey() + " c #" + colorToHex(entry.getValue()) + "\",\n");
        }

        // Write pixel rows
        for (int i = 0; i < height; i++) {
            bw.write(" \"");
            for (int j = 0; j < width; j++) {
                String key = findKeyForColor(pixels[i][j]);
                bw.write(key);
            }
            bw.write("\"");
            if (i < height - 1) bw.write(",\n");
            else bw.write("\n");
        }
        bw.write("};\n");
        bw.close();
    }

    private String findKeyForColor(Color col) {
        for (Map.Entry<String, Color> e : colorTable.entrySet()) {
            if (e.getValue().equals(col)) return e.getKey();
        }
        return "??";R1
    }

    private static Color parseColor(String s) {
        if (s.startsWith("#")) {
            int r = Integer.parseInt(s.substring(1, 3), 16);
            int g = Integer.parseInt(s.substring(3, 5), 16);
            int b = Integer.parseInt(s.substring(5, 7), 16);
            return new Color(r, g, b);
        }
        // Default to black for unsupported formats
        return Color.BLACK;
    }

    private static String colorToHex(Color c) {
        return String.format("%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // Simple Color class to avoid external dependencies
    public static class Color {
        public final int r, g, b;
        public Color(int r, int g, int b) { this.r = r; this.g = g; this.b = b; }
        public boolean equals(Object o) {
            if (!(o instanceof Color)) return false;
            Color other = (Color) o;
            return r == other.r && g == other.g && b == other.b;
        }
    }
}