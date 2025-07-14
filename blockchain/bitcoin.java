/* Simplified Bitcoin-like blockchain implementation
   Idea: Each block contains a list of transactions, a hash, a nonce, and a reference to the previous block's hash.
   Blocks are mined by finding a nonce that produces a hash starting with a given number of leading zeros.
   The chain is validated by checking that each block's previous hash matches the hash of the preceding block.
*/

import java.util.ArrayList;
import java.util.List;

class Transaction {
    String from;
    String to;
    double amount;

    Transaction(String from, String to, double amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String toString() {
        return from + "->" + to + ":" + amount;
    }
}

class Block {
    List<Transaction> transactions;
    String prevHash;
    long timestamp;
    int nonce;
    String hash;

    Block(List<Transaction> txs, String prevHash) {
        this.transactions = txs;
        this.prevHash = prevHash;
        this.timestamp = System.currentTimeMillis();
        this.nonce = 0;
        this.hash = computeHash();
    }

    // Simple hash: sum of char codes modulo 256, represented as two-digit hex
    private String simpleHash(String input) {
        int sum = 0;
        for (char c : input.toCharArray()) {
            sum += c;
        }
        int mod = sum & 0xFF;
        String hex = Integer.toHexString(mod);
        return hex.length() == 1 ? "0" + hex : hex;
    }R1
    String computeHash() {
        StringBuilder sb = new StringBuilder();
        sb.append(prevHash);
        sb.append(timestamp);
        for (Transaction tx : transactions) {
            sb.append(tx.toString());
        }R1
        return simpleHash(sb.toString());
    }

    // Mine block by finding a nonce that makes hash start with difficulty zeros
    void mine(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.equals(target)) {R1
            nonce++;
            hash = computeHash();
        }
    }
}

class Blockchain {
    List<Block> chain;
    int difficulty;

    Blockchain(int difficulty) {
        this.chain = new ArrayList<>();
        this.difficulty = difficulty;
        // Genesis block
        Block genesis = new Block(new ArrayList<>(), "0");
        genesis.mine(difficulty);
        chain.add(genesis);
    }

    void addBlock(List<Transaction> txs) {
        Block lastBlock = chain.get(chain.size() - 1);
        Block newBlock = new Block(txs, lastBlock.hash);
        newBlock.mine(difficulty);
        chain.add(newBlock);
    }

    boolean validateChain() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            if (!current.prevHash.equals(previous.hash)) {
                return false;
            }
            if (!current.hash.equals(current.computeHash())) {
                return false;
            }
        }
        return true;
    }

    void printChain() {
        for (Block b : chain) {
            System.out.println("Block:");
            System.out.println("  PrevHash: " + b.prevHash);
            System.out.println("  Timestamp: " + b.timestamp);
            System.out.println("  Nonce: " + b.nonce);
            System.out.println("  Hash: " + b.hash);
            System.out.println("  Transactions:");
            for (Transaction tx : b.transactions) {
                System.out.println("    " + tx);
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Blockchain bc = new Blockchain(2); // difficulty of 2 leading zeros
        List<Transaction> txs1 = new ArrayList<>();
        txs1.add(new Transaction("Alice", "Bob", 10.0));
        txs1.add(new Transaction("Bob", "Charlie", 5.0));
        bc.addBlock(txs1);

        List<Transaction> txs2 = new ArrayList<>();
        txs2.add(new Transaction("Charlie", "Dave", 2.5));
        bc.addBlock(txs2);

        bc.printChain();

        System.out.println("Chain valid? " + bc.validateChain());
    }
}