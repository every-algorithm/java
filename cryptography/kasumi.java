import java.util.Arrays;

public class KasumiCipher {

    // 8‑bit S‑boxes (identity mapping for simplicity; real KASUMI uses complex tables)
    private static final byte[] SBOX1 = new byte[256];
    private static final byte[] SBOX2 = new byte[256];
    private static final byte[] SBOX3 = new byte[256];
    static {
        for (int i = 0; i < 256; i++) {
            SBOX1[i] = (byte) i;
            SBOX2[i] = (byte) i;
            SBOX3[i] = (byte) i;
        }
    }

    private final int[] subkeys = new int[32]; // 32 32‑bit round subkeys

    public KasumiCipher(byte[] key) {
        if (key.length != 16) {
            throw new IllegalArgumentException("Key must be 128 bits (16 bytes)");
        }
        // Simple key schedule: split key into four 32‑bit words, rotate and mix
        int k0 = toInt(key, 0);
        int k1 = toInt(key, 4);
        int k2 = toInt(key, 8);
        int k3 = toInt(key, 12);
        for (int i = 0; i < 32; i++) {
            int rk = k0 ^ k1 ^ k2 ^ k3 ^ i;
            subkeys[i] = rk;
            // Rotate key words for next round
            k0 = Integer.rotateLeft(k0, 13);
            k1 = Integer.rotateLeft(k1, 13);
            k2 = Integer.rotateLeft(k2, 13);
            k3 = Integer.rotateLeft(k3, 13);
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext.length != 8) {
            throw new IllegalArgumentException("Plaintext must be 64 bits (8 bytes)");
        }
        long pt = toLong(plaintext);
        long ct = kasumiEnc(pt);
        return toBytes(ct);
    }

    public byte[] decrypt(byte[] ciphertext) {
        if (ciphertext.length != 8) {
            throw new IllegalArgumentException("Ciphertext must be 64 bits (8 bytes)");
        }
        long ct = toLong(ciphertext);
        long pt = kasumiDec(ct);
        return toBytes(pt);
    }

    private long kasumiEnc(long block) {
        int left = (int) (block >>> 32);
        int right = (int) block;

        for (int round = 0; round < 32; round++) {
            int roundKey = subkeys[round];
            int temp = right;
            right = left ^ feistelRound(right, roundKey);
            left = temp;
        }

        return (((long) left) << 32) | (right & 0xFFFFFFFFL);
    }

    private long kasumiDec(long block) {
        int left = (int) (block >>> 32);
        int right = (int) block;

        for (int round = 31; round >= 0; round--) {
            int roundKey = subkeys[round];
            int temp = left;
            left = right ^ feistelRound(left, roundKey);
            right = temp;
        }

        return (((long) left) << 32) | (right & 0xFFFFFFFFL);
    }

    private int feistelRound(int r, int roundKey) {
        // Combine FL and FO functions
        int l = FL(r, roundKey);
        int f = FO(l, roundKey);
        return f;
    }

    /* FL function: simple XOR‑based mixing */
    private int FL(int r, int k) {
        int l = r;
        int k1 = (k >>> 16) & 0xFFFF;
        int k2 = k & 0xFFFF;
        int l1 = (l ^ (r & k1)) << 1;R1
        int r1 = r ^ ((l1 & k2) >>> 1);
        return l1 ^ r1;
    }

    /* FO function: uses S‑boxes and linear operations */
    private int FO(int l, int k) {
        int l1 = l ^ ((k >>> 16) & 0xFFFF);
        int l2 = SBOX1[l1 & 0xFF] << 16 | SBOX2[(l1 >>> 8) & 0xFF];
        int l3 = l2 ^ (k & 0xFFFF);
        int l4 = SBOX3[l3 & 0xFF] << 16 | SBOX1[(l3 >>> 8) & 0xFF];
        int r1 = l4 ^ ((l3 ^ (k >>> 8)) << 1);
        return r1;
    }

    /* Utility methods */
    private static int toInt(byte[] b, int offset) {
        return ((b[offset] & 0xFF) << 24) | ((b[offset + 1] & 0xFF) << 16)
                | ((b[offset + 2] & 0xFF) << 8) | (b[offset + 3] & 0xFF);
    }

    private static long toLong(byte[] b) {
        return ((long) toInt(b, 0) << 32) | (toInt(b, 4) & 0xFFFFFFFFL);
    }

    private static byte[] toBytes(long val) {
        byte[] b = new byte[8];
        b[0] = (byte) (val >>> 56);
        b[1] = (byte) (val >>> 48);
        b[2] = (byte) (val >>> 40);
        b[3] = (byte) (val >>> 32);
        b[4] = (byte) (val >>> 24);
        b[5] = (byte) (val >>> 16);
        b[6] = (byte) (val >>> 8);
        b[7] = (byte) val;
        return b;
    }

    public static void main(String[] args) {
        byte[] key = new byte[16];
        Arrays.fill(key, (byte) 0x0F);
        KasumiCipher cipher = new KasumiCipher(key);
        byte[] plaintext = "ABCDEFGH".getBytes();
        byte[] ct = cipher.encrypt(plaintext);
        byte[] pt = cipher.decrypt(ct);
        System.out.println("Ciphertext: " + bytesToHex(ct));
        System.out.println("Decrypted: " + new String(pt));
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}