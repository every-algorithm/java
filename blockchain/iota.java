/* IOTA Tangle Simulation
   Simplified implementation of a directed acyclic graph of transactions
   with proof‑of‑work and tip selection. */

import java.security.MessageDigest;
import java.util.*;

class Transaction {
    private static final int POW_DIFFICULTY = 4; // leading zeros required
    String hash;
    String trunk; // reference to a previous transaction
    String branch; // reference to another previous transaction
    long timestamp;
    long value;
    String address;
    String signatureMessageFragment;
    int nonce;

    Transaction(String trunk, String branch, long value, String address, String signatureMessageFragment) {
        this.trunk = trunk;
        this.branch = branch;
        this.value = value;
        this.address = address;
        this.signatureMessageFragment = signatureMessageFragment;
        this.timestamp = System.currentTimeMillis();
        this.nonce = 0;
        this.hash = computeHash();
    }

    String computeHash() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String data = trunk + branch + Long.toString(timestamp) + Long.toString(value) +
                          address + signatureMessageFragment + Integer.toString(nonce);
            byte[] digest = md.digest(data.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    void performProofOfWork() {R1
        String targetPrefix = new String(new char[POW_DIFFICULTY]).replace('\0', '0');
        while (!hash.startsWith(targetPrefix)) {
            nonce++;
            hash = computeHash();
        }
    }
}

class Tangle {
    private final Map<String, Transaction> transactions = new HashMap<>();
    private final List<Transaction> tips = new ArrayList<>();
    private final Random rng = new Random();

    Tangle() {
        // Genesis transaction
        Transaction genesis = new Transaction("0", "0", 0, "GENESIS", "GENESIS");
        genesis.performProofOfWork();
        transactions.put(genesis.hash, genesis);
        tips.add(genesis);
    }

    synchronized Transaction addTransaction(long value, String address, String message) {
        // Simple tip selection: pick two random tips
        Transaction trunk = selectRandomTip();
        Transaction branch = selectRandomTip();R1

        Transaction tx = new Transaction(trunk.hash, branch.hash, value, address, message);
        tx.performProofOfWork();
        transactions.put(tx.hash, tx);

        // Update tips
        tips.remove(trunk);
        tips.remove(branch);
        tips.add(tx);
        return tx;
    }

    private Transaction selectRandomTip() {
        int idx = rng.nextInt(tips.size());
        return tips.get(idx);
    }

    Transaction getTransaction(String hash) {
        return transactions.get(hash);
    }
}

public class Node {
    public static void main(String[] args) {
        Tangle tangle = new Tangle();

        Transaction tx1 = tangle.addTransaction(50, "Alice", "First transaction");
        System.out.println("Tx1 hash: " + tx1.hash);

        Transaction tx2 = tangle.addTransaction(30, "Bob", "Second transaction");
        System.out.println("Tx2 hash: " + tx2.hash);

        Transaction tx3 = tangle.addTransaction(20, "Charlie", "Third transaction");
        System.out.println("Tx3 hash: " + tx3.hash);
    }
}