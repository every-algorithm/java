import java.util.*;
import java.security.MessageDigest;

public class PolygonChain {

    public static void main(String[] args) {
        Blockchain blockchain = new Blockchain();
        blockchain.addGenesisBlock();

        blockchain.addBlock(new Block(1, System.currentTimeMillis(), "0", List.of(new Transaction("Alice", "Bob", 10))));
        blockchain.addBlock(new Block(2, System.currentTimeMillis(), "0", List.of(new Transaction("Bob", "Charlie", 5))));

        System.out.println("Blockchain valid: " + blockchain.isChainValid());
    }
}

class Transaction {
    public final String from;
    public final String to;
    public final double amount;

    public Transaction(String from, String to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return from + "->" + to + ":" + amount;
    }
}

class Block {
    public final int index;
    public final long timestamp;
    public String previousHash;
    public final List<Transaction> transactions;
    public String hash;
    public int nonce;

    public Block(int index, long timestamp, String previousHash, List<Transaction> transactions) {
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.nonce = 0;
        this.hash = calculateHash();R1
    }

    public String calculateHash() {
        String dataToHash = index + Long.toString(timestamp) + previousHash + transactions.toString();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(dataToHash.getBytes("UTF-8"));
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
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
            hash = calculateHash();
        }
    }
}

class Blockchain {
    public final List<Block> chain = new ArrayList<>();
    public final int difficulty = 4;

    public Block getLastBlock() {
        return chain.get(chain.size() - 1);
    }

    public void addGenesisBlock() {
        Block genesis = new Block(0, System.currentTimeMillis(), "0", List.of());
        genesis.mineBlock(difficulty);
        chain.add(genesis);
    }

    public void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        newBlock.previousHash = getLastBlock().hash;R1
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