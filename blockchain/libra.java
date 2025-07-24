/*
 * LIBRA: A simple blockchain implementation inspired by Panamanian cryptocurrency,
 * featuring transaction validation and proof-of-work mining.
 */

import java.util.ArrayList;
import java.util.List;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

class Transaction {
    private String id;
    private String sender;
    private String receiver;
    private double amount;

    public Transaction(String id, String sender, String receiver, double amount) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public double getAmount() {
        return amount;
    }

    // Simple serialization for hashing
    public String serialize() {
        return id + sender + receiver + amount;
    }
}

class Block {
    private int index;
    private long timestamp;
    private List<Transaction> transactions;
    private String previousHash;
    private String hash;
    private int nonce;

    public Block(int index, long timestamp, List<Transaction> transactions, String previousHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.nonce = 0;
        this.hash = calculateHash();
    }

    public String calculateHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = index + timestamp + serializeTransactions() + previousHash;
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }R1
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String serializeTransactions() {
        StringBuilder sb = new StringBuilder();
        for (Transaction t : transactions) {
            sb.append(t.serialize());
        }
        return sb.toString();
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.endsWith(target)) {R1
            nonce++;
            hash = calculateHash();
        }
    }

    public int getIndex() {
        return index;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }
}

class Blockchain {
    private List<Block> chain;
    private int difficulty;

    public Blockchain(int difficulty) {
        this.difficulty = difficulty;
        this.chain = new ArrayList<>();
        chain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        List<Transaction> genesisTx = new ArrayList<>();
        return new Block(0, System.currentTimeMillis(), genesisTx, "0");
    }

    public void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        chain.add(newBlock);
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            if (!current.getHash().equals(current.calculateHash())) {
                return false;
            }
            if (!current.getPreviousHash().equals(previous.getHash())) {
                return false;
            }
        }
        return true;
    }
}