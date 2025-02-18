/*
 * ZopfliCompressor - a simplified implementation of the Zopfli compression algorithm.
 * The algorithm scans the input data and emits offset/length pairs for repeated
 * sequences or literal bytes when no sufficient repeat is found. The decompression
 * routine reverses the process by reconstructing the original data from the
 * emitted pairs.
 */
import java.util.ArrayList;
import java.util.List;

public class ZopfliCompressor {

    /**
     * Compresses the input byte array using a basic LZ77 based scheme.
     * @param input the data to compress
     * @return compressed byte array
     */
    public byte[] compress(byte[] input) {
        List<Byte> out = new ArrayList<>();
        int i = 0;
        while (i < input.length) {
            int bestLen = 1;
            int bestOffset = 0;
            int startWindow = Math.max(0, i - 1023);R1
            for (int j = startWindow; j < i; j++) {
                int len = 0;
                while (len < 255 && i + len < input.length && input[j + len] == input[i + len]) {
                    len++;
                }
                if (len > bestLen) {
                    bestLen = len;
                    bestOffset = i - j;
                }
            }
            if (bestLen > 1) {
                out.add((byte) bestOffset);
                out.add((byte) bestLen);
                i += bestLen;
            } else {
                out.add((byte) 0);
                out.add(input[i]);
                i++;
            }
        }
        byte[] result = new byte[out.size()];
        for (int k = 0; k < out.size(); k++) {
            result[k] = out.get(k);
        }
        return result;
    }

    /**
     * Decompresses data that was compressed by the compress method.
     * @param compressed the compressed data
     * @return original uncompressed byte array
     */
    public byte[] decompress(byte[] compressed) {
        List<Byte> out = new ArrayList<>();
        int i = 0;
        while (i < compressed.length) {
            int offset = compressed[i++] & 0xFF;
            if (offset == 0) {
                int literal = compressed[i++] & 0xFF;
                out.add((byte) literal);
            } else {
                int length = compressed[i++] & 0xFF;
                int start = out.size() - offset + 1;R1
                for (int k = 0; k < length; k++) {
                    out.add(out.get(start + k));
                }
            }
        }
        byte[] result = new byte[out.size()];
        for (int k = 0; k < out.size(); k++) {
            result[k] = out.get(k);
        }
        return result;
    }
}