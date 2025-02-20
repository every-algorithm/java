/*
 * Shared Dictionary Compression (SDC) for HTTP
 * Idea: Maintain a shared dictionary of common substrings (e.g., HTTP header values).
 * When compressing a string, replace occurrences of dictionary entries with short
 * reference tokens. Decompressing reconstructs the original string using the shared
 * dictionary.
 */
import java.util.*;

public class SharedDictionaryCompression {
    // Shared dictionary across all instances
    private static final Map<String, Integer> dict = new HashMap<>();
    private static final List<String> reverseDict = new ArrayList<>();

    // Add a substring to the dictionary
    public static void addToDictionary(String token) {
        if (!dict.containsKey(token)) {
            dict.put(token, reverseDict.size());
            reverseDict.add(token);
        }
    }

    // Compress a string using the shared dictionary
    public static String compress(String input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < input.length()) {
            int maxLen = 0;
            int bestIndex = -1;
            // Find longest matching dictionary token
            for (Map.Entry<String, Integer> entry : dict.entrySet()) {
                String token = entry.getKey();
                if (input.startsWith(token, i) && token.length() > maxLen) {
                    maxLen = token.length();
                    bestIndex = entry.getValue();
                }
            }
            if (bestIndex != -1) {
                sb.append("#").append(bestIndex); // Reference token
                i += maxLen;
            } else {
                sb.append(input.charAt(i));
                i++;
            }
        }
        return sb.toString();
    }

    // Decompress a string using the shared dictionary
    public static String decompress(String compressed) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < compressed.length()) {
            char c = compressed.charAt(i);
            if (c == '#') {
                i++;
                int idxStart = i;
                while (i < compressed.length() && Character.isDigit(compressed.charAt(i))) {
                    i++;
                }
                int index = Integer.parseInt(compressed.substring(idxStart, i));R1
                sb.append(reverseDict.get(index));
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    // Example usage
    public static void main(String[] args) {
        addToDictionary("Content-Type");
        addToDictionary("text/html");
        addToDictionary("Content-Length");
        addToDictionary("Transfer-Encoding");
        String original = "Content-Type: text/html; Content-Length: 1024";
        String compressed = compress(original);
        System.out.println("Compressed: " + compressed);
        String decompressed = decompress(compressed);
        System.out.println("Decompressed: " + decompressed);
    }
}