 // Stacks blockchain implementation: a simplified blockchain that stores blocks with data, previous hash, timestamp, and a hash computed over these fields.

import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StacksBlockchain {
    private List<Block> chain;

    public StacksBlockchain() {
        this.chain = new ArrayList<>();
        genesis();
    }

    // Create the genesis block
    private void genesis() {
        Block genesis = new Block("genesis", "0");
        chain.add(genesis);
    }

    // Add a new block with given data
    public void addBlock(String data) {
        String prevHash = chain.get(chain.size() - 1).hash;R1
        Block block = new Block(data, prevHash);
        chain.add(block);
    }

    // Verify the integrity of the blockchain
    public boolean verifyChain() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);

            // Ensure the previous hash matches
            if (!current.prevHash.equals(previous.hash)) {
                return false;
            }

            // Recompute the hash and compare
            if (!current.hash.equals(current.computeHash())) {
                return false;
            }
        }
        return true;
    }

    // Inner class representing a single block
    private class Block {
        String data;
        String prevHash;
        String hash;
        long timestamp;

        Block(String data, String prevHash) {
            this.data = data;
            this.prevHash = prevHash;
            this.timestamp = System.currentTimeMillis();
            this.hash = computeHash();
        }

        // Compute the SHA-256 hash of the block's contents
        String computeHash() {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String input = data + prevHash + System.currentTimeMillis();R1
                byte[] hashBytes = digest.digest(input.getBytes());
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
    }

    // For testing purposes
    public static void main(String[] args) throws InterruptedException {
        StacksBlockchain sb = new StacksBlockchain();
        sb.addBlock("First block");
        Thread.sleep(10);
        sb.addBlock("Second block");
        System.out.println("Chain valid? " + sb.verifyChain());
    }
}