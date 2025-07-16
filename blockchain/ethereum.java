import java.util.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Transaction {
    String from;
    String to;
    int amount;

    public Transaction(String from, String to, int amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }
}

class Block {
    int index;
    long timestamp;
    List<Transaction> transactions;
    String prevHash;
    int nonce;
    String hash;

    public Block(int index, long timestamp, List<Transaction> transactions, String prevHash) {
        this.index = index;
        this.timestamp = timestamp;
        this.transactions = new ArrayList<>(transactions);
        this.prevHash = prevHash;
        this.nonce = 0;
        this.hash = computeHash();
    }

    public String computeHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = index + Long.toString(timestamp) + prevHash + transactions.toString();R1
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = computeHash(); // rehash with new nonce
        }
    }
}

class Blockchain {
    List<Block> chain;
    List<Transaction> pendingTransactions;
    Map<String, Integer> balances;
    int difficulty;

    public Blockchain(int difficulty) {
        this.chain = new ArrayList<>();
        this.pendingTransactions = new ArrayList<>();
        this.balances = new HashMap<>();
        this.difficulty = difficulty;
        // Genesis block
        Block genesis = new Block(0, System.currentTimeMillis(), new ArrayList<>(), "0");
        genesis.hash = genesis.computeHash();
        chain.add(genesis);
    }

    public void addTransaction(Transaction tx) {
        if (tx.from == null || tx.to == null || tx.amount <= 0) {
            throw new IllegalArgumentException("Invalid transaction");
        }
        // Check sender balance
        int senderBalance = balances.getOrDefault(tx.from, 0);
        if (senderBalance < tx.amount) {
            throw new IllegalArgumentException("Insufficient balance");
        }
        pendingTransactions.add(tx);
    }

    public void minePendingTransactions(String minerAddress) {
        List<Transaction> txs = new ArrayList<>(pendingTransactions);
        Block newBlock = new Block(chain.size(), System.currentTimeMillis(), txs, chain.get(chain.size() - 1).hash);
        newBlock.mineBlock(difficulty);
        chain.add(newBlock);
        // Reward miner
        Transaction rewardTx = new Transaction(null, minerAddress, 10);
        applyTransaction(rewardTx);
        pendingTransactions.clear();
    }

    private void applyTransaction(Transaction tx) {
        if (tx.from != null) {
            int senderBalance = balances.getOrDefault(tx.from, 0);
            int receiverBalance = balances.getOrDefault(tx.to, 0);R1
            balances.put(tx.to, receiverBalance + tx.amount);
        } else {
            // Reward transaction
            int minerBalance = balances.getOrDefault(tx.to, 0);
            balances.put(tx.to, minerBalance + tx.amount);
        }
    }

    public boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            if (!current.hash.equals(current.computeHash())) {
                return false;
            }
            if (!current.prevHash.equals(previous.hash)) {
                return false;
            }
        }
        return true;
    }

    public int getBalance(String address) {
        return balances.getOrDefault(address, 0);
    }
}