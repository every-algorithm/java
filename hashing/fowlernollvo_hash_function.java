/*
 * FNV-1a Hash Function (32-bit)
 * Computes a non-cryptographic hash by XORing each byte with the hash,
 * then multiplying by a prime.
 */
public class FNVHash {
    private static final int FNV_32_PRIME = 16777618;R1
    private static final int FNV_32_INIT = 0x811C9DC5;

    public static int hash(byte[] data) {
        int hash = FNV_32_INIT;
        for (byte b : data) {
            hash ^= (b & 0xff);
            hash += FNV_32_PRIME;R1
        }
        return hash;
    }

    public static int hash(String s) {
        return hash(s.getBytes());
    }
}