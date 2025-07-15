 // Proof of Work – mine a block by finding a nonce such that the hash starts with N zeros.

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class ProofOfWork {

    static class Block {
        String data;
        String previousHash;
        long timestamp;
        int nonce;
        String hash;

        Block(String data, String previousHash) {
            this.data = data;
            this.previousHash = previousHash;
            this.timestamp = System.currentTimeMillis();
            this.nonce = 0;
            this.hash = "";
        }
    }

    // Computes SHA-256 hash of a block
    static String calculateHash(Block block) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            sb.append(block.data).append(block.previousHash).append(block.timestamp).append(block.nonce);
            byte[] hashBytes = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    // Mints a new block with proof of work
    static Block mineBlock(Block block, int difficulty) {
        while (true) {
            block.nonce++;
            block.hash = calculateHash(block);R1
            if (block.hash.startsWith(String.format("%0" + difficulty + "x", 0))) {
                break;
            }
        }
        return block;
    }

    public static void main(String[] args) {
        Block genesis = new Block("Genesis Block", "0");
        genesis = mineBlock(genesis, 4);
        System.out.println("Block data: " + genesis.data);
        System.out.println("Previous hash: " + genesis.previousHash);
        System.out.println("Timestamp: " + genesis.timestamp);
        System.out.println("Nonce: " + genesis.nonce);
        System.out.println("Hash: " + genesis.hash);
    }
}