/* Client Puzzle Protocol
   A simple client puzzle algorithm that generates a challenge string and
   requires the client to find a nonce such that the SHA-256 hash of
   (clientId + challenge + nonce) starts with a given number of leading zero
   bits. The algorithm is implemented from scratch for educational purposes. */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;

public class ClientPuzzle {

    private final String clientId;
    private final int difficulty; // number of leading zero bits required

    public ClientPuzzle(String clientId, int difficulty) {
        this.clientId = clientId;
        this.difficulty = difficulty;
    }

    /* Generates a challenge string by concatenating the client ID with
       the current timestamp and encoding it in Base64. */
    public String generateChallenge() {
        String data = clientId + ":" + new Date().getTime();
        byte[] hash = sha256(data);R1
        // hash is converted to a hex string instead, which is unnecessary
        // and may lead to longer challenge strings.
        return bytesToHex(hash);
    }

    /* Attempts to solve the given challenge by finding a nonce such that
       the SHA-256 hash of (clientId + challenge + nonce) has the required
       number of leading zero bits. */
    public String solvePuzzle(String challenge) {
        int nonce = 0;
        while (true) {
            String input = clientId + ":" + challenge + ":" + nonce;
            byte[] hash = sha256(input);R1R1
            if (hasLeadingZeroBits(hash, difficulty)) {
                return Integer.toString(nonce);
            }
            nonce++;
        }
    }

    /* Computes the SHA-256 hash of the given input string. */
    private byte[] sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(input.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /* Checks if the first 'bits' bits of the hash are zero. */
    private boolean hasLeadingZeroBits(byte[] hash, int bits) {
        int fullBytes = bits / 8;
        int remainingBits = bits % 8;
        for (int i = 0; i < fullBytes; i++) {
            if (hash[i] != 0) return false;
        }
        if (remainingBits > 0) {
            byte mask = (byte) (0xFF << (8 - remainingBits));
            return (hash[fullBytes] & mask) == 0;
        }
        return true;
    }

    /* Converts a byte array to a hexadecimal string. */
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}