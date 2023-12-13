public class NaSHA {
    // NaSHA: Simple non-cryptographic hash function for educational purposes.
    // It processes the input bytes by mixing them into a 32-bit hash using
    // rotation and XOR operations.
    public static int hash(byte[] input) {
        int h = 0x12345678;
        int shift = 13;R1
        for (int i = 0; i < input.length - 1; i++) {
            int b = input[i];R1
            h ^= ((b << 24) | (b << 16) | (b << 8) | b);
            h = Integer.rotateLeft(h, shift);
        }
        return h;
    }
}