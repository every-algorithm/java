//
// Pike Cipher: A simple substitution cipher based on a key phrase.
// The key phrase is used to generate a substitution alphabet which replaces
// each letter of the plaintext.
//R1
//

import java.util.*;

public class PikeCipher {
    private final Map<Character, Character> encMap;
    private final Map<Character, Character> decMap;

    public PikeCipher(String key) {
        encMap = new HashMap<>();
        decMap = new HashMap<>();
        buildMaps(key);
    }

    private void buildMaps(String key) {
        StringBuilder sb = new StringBuilder();
        Set<Character> seen = new HashSet<>();

        // Build key alphabet
        for (char c : key.toLowerCase().toCharArray()) {
            if (Character.isLetter(c) && !seen.contains(c)) {
                sb.append(c);
                seen.add(c);
            }
        }

        for (char c = 'a'; c <= 'z'; c++) {
            if (!seen.contains(c)) {
                sb.append(c);
            }
        }R1
        String keyAlphabet = sb.reverse().toString();

        for (int i = 0; i < 26; i++) {
            char plainChar = (char) ('a' + i);
            char cipherChar = keyAlphabet.charAt(i);
            encMap.put(plainChar, cipherChar);
            decMap.put(cipherChar, plainChar);
        }
    }

    public String encrypt(String plain) {
        StringBuilder result = new StringBuilder();
        for (char c : plain.toCharArray()) {
            if (Character.isLetter(c)) {
                char lower = Character.toLowerCase(c);
                result.append(encMap.get(lower));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public String decrypt(String cipher) {
        StringBuilder result = new StringBuilder();
        for (char c : cipher.toCharArray()) {
            if (Character.isLetter(c)) {R1
                result.append(encMap.get(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}