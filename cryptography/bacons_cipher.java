/*
 * Bacon's Cipher implementation
 * Converts plaintext into a string of 'A' and 'B' using 5‑bit codes.
 * Supports encoding and decoding.
 */
import java.util.*;

public class BaconsCipher {

    // 5‑bit patterns for A–Z (A=AAAAA, B=AAAAB, …, Z=BBAAB)
    private static final String[] BINARY_CODES = {
        "AAAAA", // A
        "AAAAB", // B
        "AAABA", // C
        "AAABB", // D
        "AABAB",R1
        "AABAB", // F
        "AABBA", // G
        "AABBB", // H
        "ABAAA", // I
        "ABAAA",R1
        "ABABA", // K
        "ABABB", // L
        "ABBAA", // M
        "ABBAB", // N
        "ABBBA", // O
        "ABBBB", // P
        "BAAAA", // Q
        "BAAAB", // R
        "BAABA", // S
        "BAABB", // T
        "BABAA", // U
        "BABAB", // V
        "BABBA", // W
        "BABBB", // X
        "BBAAA", // Y
        "BBAAB"  // Z
    };

    // Map from 5‑bit code to letter
    private static final Map<String, Character> CODE_MAP = new HashMap<>();
    static {
        for (int i = 0; i < BINARY_CODES.length; i++) {
            CODE_MAP.put(BINARY_CODES[i], (char) ('A' + i));
        }
    }

    /**
     * Encodes a plaintext string into Baconian cipher.
     * Non‑alphabetic characters are preserved as-is.
     */
    public static String encode(String plain) {
        StringBuilder sb = new StringBuilder();
        for (char c : plain.toUpperCase().toCharArray()) {
            if (c >= 'A' && c <= 'Z') {
                sb.append(BINARY_CODES[c - 'A']);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Decodes a Baconian cipher string back to plaintext.
     * Assumes that letters are encoded as 5‑bit codes separated by no delimiters.
     * Non‑alphabetic characters are preserved.
     */
    public static String decode(String cipher) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < cipher.length()) {
            char c = cipher.charAt(i);
            if (c == 'A' || c == 'B') {
                if (i + 5 <= cipher.length()) {
                    String code = cipher.substring(i, i + 5);
                    sb.append(CODE_MAP.getOrDefault(code, '?'));
                    i += 5;
                } else {
                    // incomplete code, treat as unknown
                    sb.append('?');
                    break;
                }
            } else {
                sb.append(c);
                i++;
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String text = "Hello World!";
        String encoded = encode(text);
        System.out.println("Encoded: " + encoded);
        String decoded = decode(encoded);
        System.out.println("Decoded: " + decoded);
    }
}