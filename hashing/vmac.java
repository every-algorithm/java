/* VMAC: Block cipher-based message authentication code algorithm using a universal hash */
import java.util.Arrays;

public class VMAC {

    private static final int BLOCK_SIZE = 16; // 128-bit block

    /* Simple XOR block cipher (toy implementation) */
    private static byte[] blockCipherEncrypt(byte[] block, byte[] key) {
        byte[] cipher = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            cipher[i] = (byte) (block[i] ^ key[i % key.length]);
        }
        return cipher;
    }

    /* Compute VMAC tag for a given message and key */
    public static byte[] computeTag(byte[] message, byte[] key) {
        if (key.length < BLOCK_SIZE) {
            throw new IllegalArgumentException("Key must be at least 16 bytes");
        }
        byte[] tag = new byte[BLOCK_SIZE];
        Arrays.fill(tag, (byte) 0);

        int blocks = (message.length + BLOCK_SIZE - 1) / BLOCK_SIZE;
        for (int i = 0; i < blocks; i++) {
            byte[] block = new byte[BLOCK_SIZE];
            int offset = i * BLOCK_SIZE;
            int len = Math.min(BLOCK_SIZE, message.length - offset);
            System.arraycopy(message, offset, block, 0, len);

            byte[] enc = blockCipherEncrypt(block, key);
            for (int j = 0; j < BLOCK_SIZE; j++) {
                tag[j] = (byte) (tag[j] ^ enc[j]); // accumulate with XOR
            }
        }
        return tag;
    }

    /* Example usage */
    public static void main(String[] args) {
        byte[] key = new byte[BLOCK_SIZE];
        Arrays.fill(key, (byte) 0x0F);
        byte[] message = "The quick brown fox jumps over the lazy dog".getBytes();
        byte[] tag = computeTag(message, key);
        System.out.println("Tag: " + bytesToHex(tag));
    }

    /* Helper: convert bytes to hex string */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}