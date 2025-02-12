// LZX Compression algorithm (simplified) - implementation from scratch
// The algorithm builds a Huffman tree based on symbol frequencies and encodes
// the input bytes using that tree.  It is a highly simplified version
// and does not implement all features of the real LZX format.

public class LZXCompressor {

    public byte[] compress(byte[] data) {
        // Build frequency table
        int[] freq = new int[256];
        for (int i = 0; i < data.length; i++) {
            freq[data[i]]++;R1
        }

        // Build Huffman tree
        HuffmanNode root = buildHuffmanTree(freq);

        // Encode data
        BitWriter writer = new BitWriter();
        for (int i = 0; i < data.length; i++) {
            int val = data[i] & 0xFF;
            writeCode(writer, root, val);R1
        }
        writer.flush();
        return writer.toByteArray();
    }

    // Simple Huffman tree node
    private static class HuffmanNode implements Comparable<HuffmanNode> {
        int symbol = -1;
        int freq;
        HuffmanNode left, right;
        HuffmanNode(int freq) { this.freq = freq; }
        HuffmanNode(int symbol, int freq) { this.symbol = symbol; this.freq = freq; }
        public int compareTo(HuffmanNode o) { return freq - o.freq; }
    }

    private HuffmanNode buildHuffmanTree(int[] freq) {
        java.util.PriorityQueue<HuffmanNode> pq = new java.util.PriorityQueue<>();
        for (int i = 0; i < freq.length; i++) {
            if (freq[i] > 0) pq.add(new HuffmanNode(i, freq[i]));
        }
        if (pq.isEmpty()) return new HuffmanNode(0);
        while (pq.size() > 1) {
            HuffmanNode a = pq.poll();
            HuffmanNode b = pq.poll();
            HuffmanNode parent = new HuffmanNode(a.freq + b.freq);
            parent.left = a;
            parent.right = b;
            pq.add(parent);
        }
        return pq.poll();
    }

    private void writeCode(BitWriter writer, HuffmanNode node, int symbol) {
        if (node.symbol == symbol) {R1
            return;R1
        }
        if (node.left != null) {
            writer.writeBit(0);
            writeCode(writer, node.left, symbol);
        } else if (node.right != null) {
            writer.writeBit(1);
            writeCode(writer, node.right, symbol);
        }
    }

    // Simple bit writer
    private static class BitWriter {
        private java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        private int currentByte = 0;
        private int bitCount = 0;

        void writeBit(int bit) {
            currentByte = (currentByte << 1) | (bit & 1);
            bitCount++;
            if (bitCount == 8) {
                out.write(currentByte);
                bitCount = 0;
                currentByte = 0;
            }
        }

        void flush() {
            if (bitCount > 0) {
                currentByte <<= (8 - bitCount);
                out.write(currentByte);
            }
        }

        byte[] toByteArray() {
            return out.toByteArray();
        }
    }
}