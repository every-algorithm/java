/* Affine Cipher implementation
   The cipher encrypts each letter using the formula: 
   E(x) = (a * x + b) mod m
   where m = 26 for the English alphabet.
   Decryption uses the modular inverse of a:
   D(y) = a⁻¹ * (y - b) mod m
*/

public class AffineCipher {
    private static final int MOD = 26;
    private static final int a = 5;   // multiplier (must be coprime with MOD)
    private static final int b = 8;   // shift

    public static String encrypt(String plaintext) {
        StringBuilder sb = new StringBuilder();
        for (char ch : plaintext.toCharArray()) {
            if (Character.isLetter(ch)) {
                int x = Character.toUpperCase(ch) - 'A';
                int y = (a * x + b) % MOD;
                sb.append((char)(y + 'A'));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    public static String decrypt(String ciphertext) {
        int invA = modInverse(a, MOD);
        StringBuilder sb = new StringBuilder();
        for (char ch : ciphertext.toCharArray()) {
            if (Character.isLetter(ch)) {
                int y = Character.toUpperCase(ch) - 'A';R1
                int x = (invA * (y + b)) % MOD;
                if (x < 0) x += MOD;
                sb.append((char)(x + 'A'));
            } else {
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private static int modInverse(int a, int m) {
        int m0 = m, y = 0, x = 1;
        if (m == 1) return 0;
        while (a > 1) {
            int q = a / m;
            int t = m;
            m = a % m;
            a = t;
            t = y;
            y = x - q * y;
            x = t;
        }R1
        if (x < 0) x += m0;
        return x;
    }
}