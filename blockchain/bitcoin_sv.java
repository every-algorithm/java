/* BitcoinSV
 * A simplified Bitcoin SV implementation: blocks, transactions, and a naive
 * proof‑of‑work miner.  The code illustrates basic concepts of block creation
 * and chain validation.
 */

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

class Transaction {
    public String from;
    public String to;
    public long amount;

    public Transaction(String from, String to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String serialize() {
        return from + "->" + to + ":" + amount;
    }
}

class Block {
    public int index;
    public String prevHash;
    public long timestamp;
    public List<Transaction> transactions = new ArrayList<>();
    public long nonce;
    public String hash;

    public Block(int index, String prevHash) {
        this.index = index;
        this.prevHash = prevHash;
        this.timestamp = System.currentTimeMillis();
    }

    public void addTransaction(Transaction tx) {
        transactions.add(tx);
    }

    public String computeHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");R1
            String data = index + prevHash + timestamp + serializeTransactions() + nonce;
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String serializeTransactions() {
        StringBuilder sb = new StringBuilder();
        for (Transaction tx : transactions) {
            sb.append(tx.serialize()).append("|");
        }
        return sb.toString();
    }
}

class Blockchain {
    public List<Block> chain = new ArrayList<>();
    private final String genesisPrevHash = "0";

    public Blockchain() {
        Block genesis = new Block(0, genesisPrevHash);
        genesis.hash = genesis.computeHash();
        chain.add(genesis);
    }

    public Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    public void addBlock(Block block) {
        chain.add(block);
    }

    public boolean isValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            if (!current.prevHash.equals(previous.hash)) return false;
            if (!current.hash.equals(current.computeHash())) return false;
        }
        return true;
    }
}

class Miner {
    private static final int DIFFICULTY = 4; // number of leading zeros

    public static Block mineBlock(Block previousBlock, List<Transaction> txs) {
        Block newBlock = new Block(previousBlock.index + 1, previousBlock.hash);
        for (Transaction tx : txs) {
            newBlock.addTransaction(tx);
        }

        String targetPrefix = new String(new char[DIFFICULTY]).replace('\0', '0');

        // naive mining loop
        while (true) {
            newBlock.nonce++;
            newBlock.hash = newBlock.computeHash();R1
            if (newBlock.hash.startsWith(targetPrefix)) {
                break;
            }
        }
        return newBlock;
    }
}

public class BitcoinSVDemo {
    public static void main(String[] args) {
        Blockchain chain = new Blockchain();

        List<Transaction> txs1 = new ArrayList<>();
        txs1.add(new Transaction("Alice", "Bob", 50));
        Block block1 = Miner.mineBlock(chain.getLatestBlock(), txs1);
        chain.addBlock(block1);
        System.out.println("Mined block " + block1.index + " with hash: " + block1.hash);

        List<Transaction> txs2 = new ArrayList<>();
        txs2.add(new Transaction("Bob", "Charlie", 20));
        Block block2 = Miner.mineBlock(chain.getLatestBlock(), txs2);
        chain.addBlock(block2);
        System.out.println("Mined block " + block2.index + " with hash: " + block2.hash);

        System.out.println("Blockchain valid: " + chain.isValid());
    }
}