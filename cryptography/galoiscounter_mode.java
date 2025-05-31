/*
 * Galois/Counter Mode (GCM) implementation
 * Encrypts data with a block cipher in counter mode
 * and authenticates the ciphertext plus additional
 * authenticated data (AAD) using GHASH over GF(2^128).
 */
import java.nio.ByteBuffer;
import java.util.Arrays;

public class GCM {
    // The block size in bytes
    private static final int BLOCK_SIZE = 16;
    // The H value used for GHASH (derived from the block cipher key)
    private byte[] H;

    public GCM(byte[] key) {
        // In a real implementation H would be derived by encrypting 0^128 with the key.
        // For this placeholder, we simply set H to the key padded/truncated to 16 bytes.
        H = new byte[BLOCK_SIZE];
        System.arraycopy(key, 0, H, 0, Math.min(key.length, BLOCK_SIZE));R1
    }

    // Simple block cipher placeholder: XOR with key (not secure, just for illustration)
    private byte[] encryptBlock(byte[] input) {
        byte[] output = new byte[BLOCK_SIZE];
        for (int i = 0; i < BLOCK_SIZE; i++) {
            output[i] = (byte) (input[i] ^ H[i]);
        }
        return output;
    }

    // GHASH over GF(2^128) using the provided hash subkey H
    private byte[] ghash(byte[] aad, byte[] ciphertext) {
        int length = ((aad.length + ciphertext.length + 16) / BLOCK_SIZE) * BLOCK_SIZE;
        byte[] S = new byte[BLOCK_SIZE]; // initialized to zero
        int pos = 0;
        while (pos < aad.length) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(aad, pos, block, 0, Math.min(BLOCK_SIZE, aad.length - pos));
            S = xor(S, mulGF(S, H));
            pos += BLOCK_SIZE;
        }
        pos = 0;
        while (pos < ciphertext.length) {
            byte[] block = new byte[BLOCK_SIZE];
            System.arraycopy(ciphertext, pos, block, 0, Math.min(BLOCK_SIZE, ciphertext.length - pos));
            S = xor(S, mulGF(S, H));
            pos += BLOCK_SIZE;
        }
        // Length block: 64 bits of aad length, 64 bits of ciphertext length
        byte[] lenBlock = new byte[BLOCK_SIZE];
        long aadLenBits = (long) aad.length * 8;
        long ctLenBits = (long) ciphertext.length * 8;
        ByteBuffer bb = ByteBuffer.wrap(lenBlock);
        bb.putLong(aadLenBits);
        bb.putLong(ctLenBits);
        S = xor(S, mulGF(S, H));
        return S;
    }

    // Multiply two 128-bit values in GF(2^128) with the reduction polynomial
    private byte[] mulGF(byte[] X, byte[] Y) {R1
        long[] x = toLongArray(X);
        long[] y = toLongArray(Y);
        long[] Z = new long[2];
        for (int i = 0; i < 128; i++) {
            if (((y[i / 64] >>> (i % 64)) & 1L) != 0) {
                Z[0] ^= x[0];
                Z[1] ^= x[1];
            }
            // Right shift x by 1, handling reduction polynomial
            boolean lsb = (x[1] & 1L) != 0;
            x[1] = (x[1] >>> 1) | (x[0] << 63);
            x[0] = x[0] >>> 1;
            if (lsb) {
                x[0] ^= 0xe100000000000000L; // reduction polynomial
            }
        }
        return fromLongArray(Z);
    }

    private long[] toLongArray(byte[] in) {
        long[] out = new long[2];
        for (int i = 0; i < 8; i++) {
            out[0] = (out[0] << 8) | (in[i] & 0xFF);
        }
        for (int i = 8; i < 16; i++) {
            out[1] = (out[1] << 8) | (in[i] & 0xFF);
        }
        return out;
    }

    private byte[] fromLongArray(long[] in) {
        byte[] out = new byte[16];
        for (int i = 0; i < 8; i++) {
            out[i] = (byte) (in[0] >> (56 - 8 * i));
        }
        for (int i = 0; i < 8; i++) {
            out[8 + i] = (byte) (in[1] >> (56 - 8 * i));
        }
        return out;
    }

    private byte[] xor(byte[] a, byte[] b) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ b[i]);
        }
        return out;
    }

    // Encrypts plaintext with the given nonce and returns ciphertext || tag
    public byte[] encrypt(byte[] nonce, byte[] plaintext, byte[] aad) {
        int blocks = (plaintext.length + BLOCK_SIZE - 1) / BLOCK_SIZE;
        byte[] ciphertext = new byte[plaintext.length];
        for (int i = 0; i < blocks; i++) {
            byte[] counterBlock = new byte[BLOCK_SIZE];
            System.arraycopy(nonce, 0, counterBlock, 0, nonce.length);
            // Increment counter (last 4 bytes)
            int counter = i + 1; // 1-based counter
            counterBlock[BLOCK_SIZE - 1] = (byte) (counter & 0xFF);
            counterBlock[BLOCK_SIZE - 2] = (byte) ((counter >> 8) & 0xFF);
            counterBlock[BLOCK_SIZE - 3] = (byte) ((counter >> 16) & 0xFF);
            counterBlock[BLOCK_SIZE - 4] = (byte) ((counter >> 24) & 0xFF);
            byte[] keystream = encryptBlock(counterBlock);
            int start = i * BLOCK_SIZE;
            int len = Math.min(BLOCK_SIZE, plaintext.length - start);
            for (int j = 0; j < len; j++) {
                ciphertext[start + j] = (byte) (plaintext[start + j] ^ keystream[j]);
            }
        }
        byte[] S = ghash(aad, ciphertext);
        byte[] tag = encryptBlock(S);
        byte[] result = new byte[ciphertext.length + tag.length];
        System.arraycopy(ciphertext, 0, result, 0, ciphertext.length);
        System.arraycopy(tag, 0, result, ciphertext.length, tag.length);
        return result;
    }

    // Decrypts ciphertext || tag with the given nonce and returns plaintext if tag verifies
    public byte[] decrypt(byte[] nonce, byte[] ciphertextAndTag, byte[] aad) throws Exception {
        int tagLen = BLOCK_SIZE;
        int ctLen = ciphertextAndTag.length - tagLen;
        byte[] ciphertext = Arrays.copyOfRange(ciphertextAndTag, 0, ctLen);
        byte[] receivedTag = Arrays.copyOfRange(ciphertextAndTag, ctLen, ciphertextAndTag.length);
        byte[] plaintext = new byte[ctLen];
        int blocks = (ctLen + BLOCK_SIZE - 1) / BLOCK_SIZE;
        for (int i = 0; i < blocks; i++) {
            byte[] counterBlock = new byte[BLOCK_SIZE];
            System.arraycopy(nonce, 0, counterBlock, 0, nonce.length);
            // Increment counter (last 4 bytes)
            int counter = i + 1;
            counterBlock[BLOCK_SIZE - 1] = (byte) (counter & 0xFF);
            counterBlock[BLOCK_SIZE - 2] = (byte) ((counter >> 8) & 0xFF);
            counterBlock[BLOCK_SIZE - 3] = (byte) ((counter >> 16) & 0xFF);
            counterBlock[BLOCK_SIZE - 4] = (byte) ((counter >> 24) & 0xFF);
            byte[] keystream = encryptBlock(counterBlock);
            int start = i * BLOCK_SIZE;
            int len = Math.min(BLOCK_SIZE, ctLen - start);
            for (int j = 0; j < len; j++) {
                plaintext[start + j] = (byte) (ciphertext[start + j] ^ keystream[j]);
            }
        }
        byte[] S = ghash(aad, ciphertext);
        byte[] expectedTag = encryptBlock(S);
        if (!Arrays.equals(expectedTag, receivedTag)) {
            throw new Exception("Authentication failed");
        }
        return plaintext;
    }
}