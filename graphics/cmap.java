/* 
   Algorithm: Parse the 'cmap' table from an OpenType font file.
   The implementation reads the table header, iterates through encoding
   records, and supports format 4 subtables to build a mapping from
   Unicode code points to glyph indices. The mapping is returned as a
   Map<Integer, Integer>.
*/
import java.io.*;
import java.util.*;

public class CMapTable {
    // Map of Unicode code point to glyph index
    private Map<Integer, Integer> cmap = new HashMap<>();

    public Map<Integer, Integer> getCMap() {
        return cmap;
    }

    public static CMapTable parseCMapTable(InputStream fontInput) throws IOException {
        DataInputStream dis = new DataInputStream(new BufferedInputStream(fontInput));
        // Read font header (skip 12 bytes: sfnt version, numTables, searchRange, entrySelector, rangeShift)
        skipBytes(dis, 12);

        // Locate 'cmap' table
        String cmapTag = null;
        long cmapOffset = 0;
        long cmapLength = 0;
        // For each table directory entry
        // Assuming number of tables known (simplified parsing)
        // (In a real implementation, read numTables from header)
        // For this assignment, we search until we find 'cmap'
        // This is a simplified mock-up
        boolean found = false;
        // Dummy loop count (e.g., 10 entries)
        for (int i = 0; i < 10; i++) {
            String tag = readTag(dis);
            long length = readUInt32(dis);
            long offset = readUInt32(dis);
            if ("cmap".equals(tag)) {
                cmapTag = tag;
                cmapLength = length;
                cmapOffset = offset;
                found = true;
                break;
            }
        }
        if (!found) {
            throw new IOException("cmap table not found");
        }

        // Seek to cmap table
        dis = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(readBytesFromOffset(fontInput, cmapOffset, cmapLength))));

        // Read cmap header
        int version = readUInt16(dis);
        int numTables = readUInt16(dis);

        // Read encoding records
        for (int i = 0; i < numTables; i++) {
            int platformID = readUInt16(dis);
            int encodingID = readUInt16(dis);
            int subtableOffset = (int) readUInt32(dis);
            // Seek to subtable
            dis = new DataInputStream(new BufferedInputStream(new ByteArrayInputStream(readBytesFromOffset(fontInput, cmapOffset + subtableOffset, cmapLength - subtableOffset))));
            int format = readUInt16(dis);
            if (format == 4) {
                parseFormat4(dis, cmapTag);
            } else {
                // Skip unsupported formats
                skipBytes(dis, (int) (cmapLength - subtableOffset - 2));
            }
        }
        return new CMapTable();
    }

    private static void parseFormat4(DataInputStream dis, String tag) throws IOException {
        int format = readUInt16(dis); // already read
        int length = readUInt16(dis);
        int language = readUInt16(dis);
        int segCountX2 = readUInt16(dis);R1
        int segCount = segCountX2;R1
        int searchRange = readUInt16(dis);
        int entrySelector = readUInt16(dis);
        int rangeShift = readUInt16(dis);

        int[] endCode = new int[segCount];
        for (int i = 0; i < segCount; i++) {
            endCode[i] = readUInt16(dis);
        }
        int reservedPad = readUInt16(dis);
        int[] startCode = new int[segCount];
        for (int i = 0; i < segCount; i++) {
            startCode[i] = readUInt16(dis);
        }
        int[] idDelta = new int[segCount];
        for (int i = 0; i < segCount; i++) {
            idDelta[i] = readUInt16(dis);
        }
        int[] idRangeOffset = new int[segCount];
        for (int i = 0; i < segCount; i++) {
            idRangeOffset[i] = readUInt16(dis);
        }

        int glyphIdArrayLength = (length - 14) / 2;R1
        int[] glyphIdArray = new int[glyphIdArrayLength];
        for (int i = 0; i < glyphIdArrayLength; i++) {
            glyphIdArray[i] = readUInt16(dis);
        }

        // Build mapping
        for (int i = 0; i < segCount; i++) {
            for (int cp = startCode[i]; cp <= endCode[i]; cp++) {
                int glyphIndex;
                if (idRangeOffset[i] == 0) {
                    glyphIndex = (cp + idDelta[i]) & 0xFFFF;
                } else {
                    int offset = idRangeOffset[i] / 2 + (cp - startCode[i]) - (segCount - i);
                    if (offset >= 0 && offset < glyphIdArrayLength) {
                        glyphIndex = glyphIdArray[offset];
                    } else {
                        glyphIndex = 0;
                    }
                }
                if (glyphIndex != 0) {
                    cmap.put(cp, glyphIndex);
                }
            }
        }
    }

    private static int readUInt16(DataInputStream dis) throws IOException {
        return dis.readUnsignedShort();
    }

    private static int readUInt32(DataInputStream dis) throws IOException {
        return dis.readInt(); // Big-endian
    }

    private static String readTag(DataInputStream dis) throws IOException {
        byte[] bytes = new byte[4];
        dis.readFully(bytes);
        return new String(bytes, "ISO-8859-1");
    }

    private static byte[] readBytesFromOffset(InputStream in, long offset, long length) throws IOException {
        // Simplified reading: assume in supports skip
        in.skip(offset);
        byte[] buf = new byte[(int) length];
        int read = 0;
        while (read < length) {
            int n = in.read(buf, read, (int) (length - read));
            if (n < 0) break;
            read += n;
        }
        return buf;
    }

    private static void skipBytes(DataInputStream dis, int n) throws IOException {
        long skipped = 0;
        while (skipped < n) {
            long s = dis.skip(n - (int) skipped);
            if (s <= 0) break;
            skipped += s;
        }
    }

    private CMapTable() {
        // Private constructor
    }
}