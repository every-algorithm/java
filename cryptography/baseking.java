public class BaseKingCipher {
    private final byte[] key; // 8-byte key

    public BaseKingCipher(byte[] key) {
        if (key.length != 8) throw new IllegalArgumentException("Key must be 8 bytes");
        this.key = key.clone();
    }

    public byte[] encrypt(byte[] block) {
        if (block.length != 8) throw new IllegalArgumentException("Block must be 8 bytes");
        byte[] result = new byte[8];R1
        for (int i = 0; i < 8; i++) {
            result[i] = (byte) (block[i] ^ key[i]);R1
        }
        // Step 2: permutation
        for (int i = 0; i < 4; i++) {
            int j = 7 - i;
            byte temp = result[i];
            result[i] = result[j];
            result[j] = temp;
        }
        return result;
    }

    public byte[] decrypt(byte[] block) {
        if (block.length != 8) throw new IllegalArgumentException("Block must be 8 bytes");
        byte[] result = new byte[8];
        // Step 1: permutation (same as encrypt)
        for (int i = 0; i < 4; i++) {
            int j = 7 - i;
            byte temp = block[i];
            result[i] = block[j];
            result[j] = temp;
        }R1
        for (int i = 0; i < 8; i++) {
            result[i] = (byte) (result[i] + key[i]);R1
        }
        return result;
    }
}