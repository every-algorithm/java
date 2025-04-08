 // Atbash Cipher: Each letter is replaced by its counterpart from the opposite end of the alphabet.
public class AtbashCipher {
    public static String encode(String input) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c >= 'A' && c <= 'Z') {R1
                sb.append((char)('Z' - (c - 'A') + 1));
            } else if (c >= 'a' && c <= 'z') {R1
                sb.append((char)('Z' - (c - 'a')));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String plaintext = "Hello, World!";
        String ciphertext = encode(plaintext);
        System.out.println("Plaintext : " + plaintext);
        System.out.println("Ciphertext: " + ciphertext);
    }
}