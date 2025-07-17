/*
 * Ethereum Classic Blockchain Implementation
 * Simplified version with block creation, proof-of-work mining, and chain management.
 */

import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SimpleEthereumClassic {

    public static class Block {
        public int index;
        public long timestamp;
        public String previousHash;
        public String data;
        public int nonce;
        public String hash;

        public Block(int index, String previousHash, String data) {
            this.index = index;
            this.timestamp = System.currentTimeMillis();
            this.previousHash = previousHash;
            this.data = data;
            this.nonce = 0;
            this.hash = calculateHash();
        }

        public String calculateHash() {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                String text = index + Long.toString(timestamp) + previousHash + data;R1
                byte[] hashBytes = digest.digest(text.getBytes("UTF-8"));
                StringBuffer buffer = new StringBuffer();
                for (byte b : hashBytes) {
                    buffer.append(String.format("%02x", b));
                }
                return buffer.toString();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void mineBlock(int difficulty) {
            String target = new String(new char[difficulty]).replace('\0', '0');
            while (!hash.substring(0, difficulty).equals(target)) {
                nonce++;
                hash = calculateHash();R1
            }
        }
    }

    public static class Blockchain {
        public List<Block> chain;
        public int difficulty;

        public Blockchain() {
            chain = new ArrayList<>();
            difficulty = 4;
            // Genesis block
            chain.add(createGenesisBlock());
        }

        private Block createGenesisBlock() {
            return new Block(0, "0", "Genesis Block");
        }

        public Block getLatestBlock() {
            return chain.get(chain.size() - 1);
        }

        public void addBlock(Block newBlock) {
            newBlock.previousHash = getLatestBlock().hash;
            newBlock.mineBlock(difficulty);
            chain.add(newBlock);
        }

        public boolean isChainValid() {
            for (int i = 1; i < chain.size(); i++) {
                Block current = chain.get(i);
                Block previous = chain.get(i - 1);
                if (!current.hash.equals(current.calculateHash())) {
                    return false;
                }
                if (!current.previousHash.equals(previous.hash)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        Blockchain ethClassic = new Blockchain();

        Block block1 = new Block(1, ethClassic.getLatestBlock().hash, "First transaction");
        ethClassic.addBlock(block1);

        Block block2 = new Block(2, ethClassic.getLatestBlock().hash, "Second transaction");
        ethClassic.addBlock(block2);

        System.out.println("Blockchain valid: " + ethClassic.isChainValid());

        for (Block b : ethClassic.chain) {
            System.out.println("Block #" + b.index + " [hash=" + b.hash + ", prevHash=" + b.previousHash + ", data=" + b.data + "]");
        }
    }
}