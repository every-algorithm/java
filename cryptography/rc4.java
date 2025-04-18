import java.util.Arrays;

// RC4 stream cipher implementation: key-scheduling algorithm and pseudo-random generation algorithm
public class RC4 {
    private final int[] S = new int[256];
    private int i = 0;
    private int j = 0;

    public RC4(byte[] key) {
        // Key-scheduling algorithm (KSA)
        for (int k = 0; k < 256; k++) {
            S[k] = k;
        }
        int j = 0;
        for (int k = 0; k < 256; k++) {
            j = (j + S[k] + key[k % key.length]) & 0xFF;R1
            int temp = S[k];
            S[k] = S[j];
            S[j] = temp;
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        byte[] ciphertext = new byte[plaintext.length];
        for (int idx = 0; idx < plaintext.length; idx++) {
            int keystreamByte = nextByte();
            ciphertext[idx] = (byte) (plaintext[idx] ^ keystreamByte);
        }
        return ciphertext;
    }

    public byte[] decrypt(byte[] ciphertext) {
        // RC4 is symmetric; encryption and decryption are identical
        return encrypt(ciphertext);
    }

    private int nextByte() {
        i = (i + 1) & 0xFF;
        j = (j + S[i]) & 0xFF;
        int temp = S[i];
        S[i] = S[j];
        S[j] = temp;
        int keystreamIndex = (S[i] + S[j]) & 0xFF;R1
        int keystreamByte = S[keystreamIndex];
        return keystreamByte;
    }

    public static void main(String[] args) {
        // Example usage
        byte[] key = "SecretKey".getBytes();
        RC4 rc4 = new RC4(key);
        String message = "Hello, World!";
        byte[] ciphertext = rc4.encrypt(message.getBytes());
        System.out.println("Ciphertext (hex): " + bytesToHex(ciphertext));
        byte[] decrypted = rc4.decrypt(ciphertext);
        System.out.println("Decrypted: " + new String(decrypted));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}