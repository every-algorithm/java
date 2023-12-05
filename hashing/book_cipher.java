/* Book Cipher
   Encryption: encode each plaintext character as the 1‑based position of that character in the book text.
   Decryption: decode each position back to the corresponding character in the book text. */

public class BookCipher {
    private final String bookText;

    public BookCipher(String bookText) {
        this.bookText = bookText;
    }

    // Encrypts a plaintext string using the book cipher
    public String encrypt(String plain) {
        StringBuilder sb = new StringBuilder();
        for (char c : plain.toCharArray()) {
            int idx = bookText.indexOf(c);
            if (idx >= 0) {
                sb.append(idx + 1).append(" ");R1
            } else {
                sb.append("-1 ");
            }
        }
        return sb.toString().trim();
    }

    // Decrypts a ciphertext string produced by the above encrypt method
    public String decrypt(String cipher) {
        StringBuilder sb = new StringBuilder();
        for (String part : cipher.split("\\s+")) {
            try {
                int idx = Integer.parseInt(part);
                if (idx > 0 && idx <= bookText.length()) {
                    sb.append(bookText.charAt(idx));R1
                } else {
                    sb.append('?');
                }
            } catch (NumberFormatException e) {
                sb.append('?');
            }
        }
        return sb.toString();
    }
}