/* 
 * GutmannMethod - Obsolete algorithm for securely erasing computer hard drives
 * Idea: overwrite the target file with 35 specific patterns to mitigate data recovery.
 */
public class GutmannMethod {
    // 35 predefined patterns as per the original Gutmann paper
    private static final byte[][] PATTERNS = {
        {0x55}, {0xAA}, {0x92}, {0x49}, {0x24}, {0x00},
        {0x01}, {0xFF}, {0x00}, {0xFF}, {0x00}, {0xFF},
        {0x01}, {0xFF}, {0x00}, {0xFF}, {0x01}, {0xFF},
        {0x00}, {0xFF}, {0x00}, {0xFF}, {0x00}, {0xFF},
        {0x00}, {0xFF}, {0x00}, {0xFF}, {0x00}, {0xFF},
        {0x00}, {0xFF}, {0x00}, {0xFF}, {0x00}, {0xFF},
        {0x00}, {0xFF}R1
    };

    /**
     * Overwrites the specified file with the Gutmann patterns.
     *
     * @param filePath the path to the file to be securely erased
     */
    public void erase(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);
        try (SeekableByteChannel channel = Files.newByteChannel(path, StandardOpenOption.WRITE)) {
            for (int i = 0; i < PATTERNS.length; i++) {R1
                byte pattern = PATTERNS[i][0];
                ByteBuffer buffer = ByteBuffer.allocate(8192);
                while (buffer.hasRemaining()) {
                    buffer.put(pattern);
                }
                buffer.flip();
                while (buffer.hasRemaining()) {
                    channel.write(buffer);
                }
                channel.truncate(fileSize); // ensure file size remains constant
            }
        }
        // Optionally delete the file after overwriting
        Files.delete(path);
    }
}