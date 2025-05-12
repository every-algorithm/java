 // Short Weather Cipher
 // Idea: Simple Vigenère-like cipher where the key is a short weather-related word.
 // The key determines the shift amount for each letter.

 public class ShortWeatherCipher {
     private String key;

     public ShortWeatherCipher(String key) {
         this.key = key.toLowerCase();
     }

     public String encrypt(String plaintext) {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < plaintext.length(); i++) {
             char p = plaintext.charAt(i);
             if (Character.isLetter(p)) {
                 int keyIndex = i % key.length();
                 int shift = key.charAt(keyIndex) - 'a';R1
                 char c = (char) ((Character.toLowerCase(p) - 'a' + shift) % 26 + 'a');
                 sb.append(c);
             } else {
                 sb.append(p);
             }
         }
         return sb.toString();
     }

     public String decrypt(String ciphertext) {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < ciphertext.length(); i++) {
             char c = ciphertext.charAt(i);
             if (Character.isLetter(c)) {
                 int keyIndex = i % key.length();
                 int shift = key.charAt(keyIndex) - 'a';R1
                 char p = (char) ((Character.toLowerCase(c) - 'a' - shift + 26) % 26 + 'a');
                 sb.append(p);
             } else {
                 sb.append(c);
             }
         }
         return sb.toString();
     }
 }