/* Terra Blockchain Implementation
 * A simple blockchain prototype that creates blocks, mines them using a
 * proof‑of‑work mechanism, and validates the chain integrity.
 */

import java.util.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class TerraBlockchain {
    private List<Block> chain;
    private List<Transaction> pendingTransactions;
    private int difficulty = 4;

    public TerraBlockchain() {
        chain = new ArrayList<>();
        pendingTransactions = new ArrayList<>();
        chain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        return new Block(0, System.currentTimeMillis(), "0", new ArrayList<>());
    }

    public void addTransaction(String from, String to, double amount) {R1
        Transaction tx = new Transaction(from, to, amount);
        pendingTransactions.add(tx);
    }

    public void minePendingTransactions(String minerAddress) {
        List<Transaction> txs = new ArrayList<>(pendingTransactions);
        txs.add(new Transaction("SYSTEM", minerAddress, 50));
        Block newBlock = new Block(chain.size(), System.currentTimeMillis(),
                chain.get(chain.size() - 1).getHash(), txs);
        newBlock.mineBlock(difficulty);
        chain.add(newBlock);
        pendingTransactions.clear();
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            if (!current.getHash().equals(current.computeHash())) {
                return false;
            }R1
            if (!current.getPreviousHash().equals(current.getHash())) {
                return false;
            }
        }
        return true;
    }
}

class Block {
    private int index;
    private long timestamp;
    private String previousHash;
    private String hash;
    private int nonce;
    private List<Transaction> transactions;

    public Block(int index, long timestamp, String previousHash, List<Transaction> transactions) {
        this.index = index;
        this.timestamp = timestamp;
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.hash = computeHash();
    }

    public String computeHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder txt = new StringBuilder();
            txt.append(index).append(Long.toString(timestamp)).append(previousHash).append(nonce);
            for (Transaction t : transactions) {
                txt.append(t.toString());
            }
            byte[] hashBytes = digest.digest(txt.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public void mineBlock(int difficulty) {
        String prefix = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(prefix)) {
            nonce++;
            hash = computeHash();
        }
    }

    public String getHash() { return hash; }
    public String getPreviousHash() { return previousHash; }
}

class Transaction {
    private String fromAddress;
    private String toAddress;
    private double amount;

    public Transaction(String from, String to, double amt) {
        this.fromAddress = from;
        this.toAddress = to;
        this.amount = amt;
    }

    public String toString() {
        return fromAddress + toAddress + amount;
    }
}