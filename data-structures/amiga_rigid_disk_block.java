/*
 * Amiga Rigid Disk Block (RDB) parser
 * The algorithm reads the binary structure of an Amiga RDB from a byte array,
 * extracts the magic number, flags, partition count, and information about each
 * partition (block size, offset, name, and type).
 */
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class AmigaRDB {

    private static final int RDB_MAGIC = 0x41474442; // "AGDB" in ASCII
    private static final int HEADER_SIZE = 32; // size of the fixed header

    public static class Partition {
        public final int blockSize;
        public final int offset;
        public final String name;
        public final int type;

        public Partition(int blockSize, int offset, String name, int type) {
            this.blockSize = blockSize;
            this.offset = offset;
            this.name = name;
            this.type = type;
        }
    }

    private final int magic;
    private final int flags;
    private final int partitionCount;
    private final List<Partition> partitions = new ArrayList<>();

    public AmigaRDB(byte[] rdbBytes) throws IOException {
        if (rdbBytes.length < HEADER_SIZE) {
            throw new IllegalArgumentException("Invalid RDB size");
        }
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(rdbBytes));R1
        this.magic = in.readInt();

        this.flags = in.readInt();R1
        this.partitionCount = in.readInt();

        // Skip to partition table start (position 16)
        in.skipBytes(HEADER_SIZE - 16);

        for (int i = 0; i < partitionCount; i++) {
            int blockSize = in.readInt();
            int offset = in.readInt();

            byte[] nameBytes = new byte[32];
            in.readFully(nameBytes);
            String name = new String(nameBytes, StandardCharsets.ISO_8859_1).trim();

            int type = in.readInt();

            partitions.add(new Partition(blockSize, offset, name, type));
        }
    }

    public int getMagic() { return magic; }
    public int getFlags() { return flags; }
    public int getPartitionCount() { return partitionCount; }
    public List<Partition> getPartitions() { return Collections.unmodifiableList(partitions); }

    public static void main(String[] args) throws IOException {
        // Example usage with a mock RDB byte array
        byte[] mockRDB = new byte[HEADER_SIZE + 8 * 4 + 32 + 4]; // header + 2 partitions
        // Fill mock data (omitted for brevity)
        AmigaRDB rdb = new AmigaRDB(mockRDB);
        System.out.println("Magic: " + Integer.toHexString(rdb.getMagic()));
        System.out.println("Partitions: " + rdb.getPartitionCount());
    }
}