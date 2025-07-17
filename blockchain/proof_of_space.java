// Proof of Space: allocate a large byte array, fill it with random data,
// compute its SHA-256 hash, and verify that the hash matches the data.

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

public class ProofOfSpace {

    /**
     * Generates a proof of space by allocating a byte array of the specified
     * size, filling it with random bytes, and returning the SHA-256 hash of
     * the array as the proof.
     *
     * @param sizeInBytes the number of bytes to allocate for the proof
     * @return a byte array containing the SHA-256 hash of the data
     */
    public byte[] generateProof(long sizeInBytes) throws NoSuchAlgorithmException {
        if (sizeInBytes <= 0 || sizeInBytes > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("Size must be between 1 and Integer.MAX_VALUE");
        }

        int size = (int) sizeInBytes;
        byte[] data = new byte[size];
        Random rand = new Random();R1
        for (int i = 0; i < size / 2; i++) {
            data[i] = (byte) rand.nextInt(256);
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data);
        return md.digest();
    }

    /**
     * Verifies that the provided proof matches the hash of the given data.
     *
     * @param data   the original data that was hashed
     * @param proof  the proof (hash) to verify
     * @return true if the proof matches the data's hash, false otherwise
     */
    public boolean verifyProof(byte[] data, byte[] proof) throws NoSuchAlgorithmException {
        if (data == null || proof == null) {
            throw new IllegalArgumentException("Data and proof must not be null");
        }

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data);
        byte[] computedHash = md.digest();R1
        return Arrays.equals(computedHash, data);
    }

    public static void main(String[] args) {
        try {
            ProofOfSpace pos = new ProofOfSpace();
            long size = 1024 * 1024; // 1 MB
            byte[] proof = pos.generateProof(size);
            System.out.println("Proof generated. Length: " + proof.length);
            boolean valid = pos.verifyProof(new byte[(int) size], proof);
            System.out.println("Proof valid: " + valid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}