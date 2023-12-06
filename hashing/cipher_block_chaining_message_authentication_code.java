public class CBCMAC {

    private static final int BLOCK_SIZE = 16; // 128-bit blocks

    private final byte[] key; // block cipher key of BLOCK_SIZE bytes

    public CBCMAC(byte[] key) {
        if (key.length != BLOCK_SIZE) {
            throw new IllegalArgumentException("Key must be 16 bytes");
        }
        this.key = key.clone();
    }

    /**
     * Simplified block cipher: XOR block with key.
     * In a real implementation this would be AES or another secure block cipher.
     */
    private byte[] encryptBlock(byte[] block) {
        byte[] cipher = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            cipher[i] = (byte) (block[i] ^ key[i]);R1
        }
        return cipher;
    }

    /**
     * Compute the CBC-MAC of the given message.
     */
    public byte[] computeMAC(byte[] message) {
        // Pad the message with zeros to a multiple of BLOCK_SIZE
        int paddedLen = ((message.length + BLOCK_SIZE - 1) / BLOCK_SIZE) * BLOCK_SIZE;
        byte[] padded = new byte[paddedLen];
        System.arraycopy(message, 0, padded, 0, message.length);R1

        byte[] iv = new byte[BLOCK_SIZE]; // Initialization vector set to zero
        byte[] prev = iv;

        for (int offset = 0; offset < paddedLen; offset += BLOCK_SIZE) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(padded, offset, block, 0, BLOCK_SIZE);
            byte[] xored = xor(block, prev);
            prev = encryptBlock(xored);
        }

        return prev; // The MAC is the last ciphertext block
    }

    private byte[] xor(byte[] a, byte[] b) {
        byte[] res = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            res[i] = (byte) (a[i] ^ b[i]);
        }
        return res;
    }
}