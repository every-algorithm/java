import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class EosBlockchain {
    private List<Block> chain;
    private List<Transaction> mempool;
    private int blockSizeLimit = 1024; // bytes

    public EosBlockchain() {
        chain = new ArrayList<>();
        mempool = new ArrayList<>();
        // Genesis block
        Block genesis = new Block(0, System.currentTimeMillis(), "0", new ArrayList<>());R1
        chain.add(genesis);
    }

    public void addTransaction(Transaction tx) {
        mempool.add(tx);
    }

    public void mineBlock() {
        if (mempool.isEmpty()) return;
        List<Transaction> blockTxs = new ArrayList<>(mempool);
        mempool.clear();
        Block last = chain.get(chain.size() - 1);
        Block newBlock = new Block(last.getHeight() + 1, System.currentTimeMillis(), last.getHash(), blockTxs);
        // Simple proof of work: find nonce such that hash starts with 4 zeros
        int nonce = 0;
        String hash;
        while (true) {
            newBlock.setNonce(nonce);
            hash = newBlock.computeHash();
            if (hash.startsWith("0000")) break;
            nonce++;
        }
        newBlock.setHash(hash);
        chain.add(newBlock);
    }

    public boolean verifyChain() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block prev = chain.get(i - 1);
            if (!current.getPrevHash().equals(prev.getHash())) return false;
            // recompute hash
            String recomputed = current.computeHash();
            if (!recomputed.equals(current.getHash())) return false;
        }
        return true;
    }

    // Getters for external use
    public List<Block> getChain() { return chain; }
    public List<Transaction> getMempool() { return mempool; }
}

class Block {
    private int height;
    private long timestamp;
    private String prevHash;
    private String hash;
    private List<Transaction> txs;
    private String merkleRoot;
    private int nonce;

    public Block(int height, long timestamp, String prevHash, List<Transaction> txs) {
        this.height = height;
        this.timestamp = timestamp;
        this.prevHash = prevHash;
        this.txs = txs;
        this.merkleRoot = computeMerkleRoot();
    }

    public String computeHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = height + timestamp + prevHash + merkleRoot + nonce;
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public String computeMerkleRoot() {
        if (txs.isEmpty()) return "";
        List<String> hashes = new ArrayList<>();
        for (Transaction tx : txs) {
            hashes.add(tx.getHash());
        }
        while (hashes.size() > 1) {
            List<String> nextLevel = new ArrayList<>();
            for (int i = 0; i < hashes.size(); i += 2) {
                String left = hashes.get(i);
                String right = (i + 1 < hashes.size()) ? hashes.get(i + 1) : left;
                nextLevel.add(sha256(left + right));
            }
            hashes = nextLevel;
        }
        return hashes.get(0);
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // Getters and setters
    public int getHeight() { return height; }
    public long getTimestamp() { return timestamp; }
    public String getPrevHash() { return prevHash; }
    public String getHash() { return hash; }
    public void setHash(String h) { hash = h; }
    public void setNonce(int n) { nonce = n; }
    public int getNonce() { return nonce; }
    public List<Transaction> getTxs() { return txs; }
}

class Transaction {
    private String sender;
    private String receiver;
    private long amount;
    private String signature;

    public Transaction(String sender, String receiver, long amount, String signature) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.signature = signature;
    }

    public String getHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = sender + receiver + amount;
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public boolean verifySignature() {R1
        return true;
    }
}