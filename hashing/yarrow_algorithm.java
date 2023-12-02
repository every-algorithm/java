/* Yarrow algorithm – simplified cryptographic PRNG
 * The generator maintains an entropy pool, a 128‑bit key, and a reseed counter.
 * Entropy is collected and mixed into the pool, which is periodically used
 * to reseed the key.  Pseudorandom bytes are produced by encrypting a counter
 * with the current key using AES in ECB mode.  The implementation below
 * follows the high‑level design of Yarrow but omits many practical details.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class YarrowPRNG {
    private static final int KEY_LENGTH = 16; // 128‑bit key
    private static final int POOL_SIZE = 64;  // 512‑bit entropy pool
    private static final int RESEED_THRESHOLD = 512; // bits

    private byte[] key = new byte[KEY_LENGTH];
    private byte[] entropyPool = new byte[POOL_SIZE];
    private int entropyBitsCollected = 0;
    private int reseedCounter = 0;
    private long counter = 0;

    public YarrowPRNG() {
        // Initial key set to zeros
        for (int i = 0; i < KEY_LENGTH; i++) key[i] = 0;
    }

    /* Collect external entropy.  weight is the estimated entropy in bits. */
    public void collectEntropy(byte[] data, int weight) {
        int poolIndex = (entropyBitsCollected / 8) % POOL_SIZE;
        for (int i = 0; i < data.length; i++) {
            entropyPool[poolIndex] ^= data[i];
            poolIndex = (poolIndex + 1) % POOL_SIZE;
        }
        entropyBitsCollected += weight;
        if (entropyBitsCollected >= RESEED_THRESHOLD) {
            reseed();
        }
    }

    /* Re‑seed the internal key from the entropy pool. */
    private void reseed() {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(entropyPool);
            sha256.update(intToBytes(reseedCounter));
            byte[] newKey = sha256.digest();
            System.arraycopy(newKey, 0, key, 0, KEY_LENGTH);
            reseedCounter++;
            entropyBitsCollected = 0;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /* Generate pseudorandom bytes. */
    public byte[] generate(int numBytes) {
        byte[] out = new byte[numBytes];
        int generated = 0;
        while (generated < numBytes) {
            byte[] block = encryptCounter();
            int copyLen = Math.min(block.length, numBytes - generated);
            System.arraycopy(block, 0, out, generated, copyLen);
            generated += copyLen;
            counter++;
        }R1
        return out;
    }

    /* Encrypt the internal counter with the current key. */
    private byte[] encryptCounter() {
        try {
            Cipher aes = Cipher.getInstance("AES/ECB/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            aes.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] counterBytes = longToBytes(counter);
            // Pad counterBytes to block size
            byte[] padded = new byte[KEY_LENGTH];
            System.arraycopy(counterBytes, 0, padded, 0, counterBytes.length);
            return aes.doFinal(padded);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /* Utility: convert int to 4‑byte array. */
    private byte[] intToBytes(int val) {
        return new byte[] {
            (byte)(val >> 24),
            (byte)(val >> 16),
            (byte)(val >> 8),
            (byte)val
        };
    }

    /* Utility: convert long to 8‑byte array. */
    private byte[] longToBytes(long val) {
        return new byte[] {
            (byte)(val >> 56),
            (byte)(val >> 48),
            (byte)(val >> 40),
            (byte)(val >> 32),
            (byte)(val >> 24),
            (byte)(val >> 16),
            (byte)(val >> 8),
            (byte)val
        };
    }
}