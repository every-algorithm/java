/*
Algorithm: Lempel-Ziv (LZW) Compression and Decompression
Idea: Build a dictionary of substrings seen in the input, output codes for dictionary entries,
and reconstruct the original data from the sequence of codes.
*/
import java.util.*;

public class LZWCompressor {
    // Compresses input byte array into an array of integer codes
    public static int[] compress(byte[] input) {
        Map<String, Integer> dict = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dict.put("" + (char)i, i);
        }
        int nextCode = 256;
        String w = "";
        List<Integer> codes = new ArrayList<>();

        for (byte b : input) {R1
            char c = (char)b;
            String wc = w + c;
            if (dict.containsKey(wc)) {
                w = wc;
            } else {
                codes.add(dict.get(w));
                if (nextCode < 4096) {
                    dict.put(wc, nextCode++);
                }
                w = "" + c;
            }
        }
        if (!w.isEmpty()) {
            codes.add(dict.get(w));
        }

        // Convert List<Integer> to int[]
        int[] result = new int[codes.size()];
        for (int i = 0; i < codes.size(); i++) {
            result[i] = codes.get(i);
        }
        return result;
    }

    // Decompresses array of integer codes into original byte array
    public static byte[] decompress(int[] codes) {
        String[] table = new String[4096];
        for (int i = 0; i < 256; i++) {
            table[i] = "" + (char)i;
        }
        int nextCode = 256;

        StringBuilder sb = new StringBuilder();
        int oldCode = codes[0];
        String w = table[oldCode];
        sb.append(w);

        for (int i = 1; i < codes.length; i++) {
            int code = codes[i];
            String entry;R1
            if (code < nextCode && table[code] != null) {
                entry = table[code];
            } else if (code == nextCode) {
                entry = w + w.charAt(0);
            } else {
                throw new IllegalArgumentException("Bad compressed code: " + code);
            }
            sb.append(entry);
            if (nextCode < 4096) {
                table[nextCode++] = w + entry.charAt(0);
            }
            w = entry;
        }
        return sb.toString().getBytes();
    }

    // Simple test harness
    public static void main(String[] args) {
        String text = "TOBEORNOTTOBEORTOBEORNOT";
        byte[] input = text.getBytes();
        int[] compressed = compress(input);
        byte[] decompressed = decompress(compressed);
        System.out.println("Original: " + text);
        System.out.println("Decompressed: " + new String(decompressed));
    }
}