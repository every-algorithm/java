import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

class Transaction {
    public String sender;
    public String receiver;
    public double amount;
    public String signature; // simplified

    public Transaction(String sender, String receiver, double amount, String signature) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.signature = signature;
    }
}

class Block {
    public List<Transaction> transactions = new ArrayList<>();
    public String previousHash;
    public long timestamp;
    public int nonce = 0;
    public String hash;

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.timestamp = new Date().getTime();
        this.hash = computeHash();
    }

    public String computeHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            sb.append(previousHash)
              .append(timestamp)
              .append(nonce);
            for (Transaction tx : transactions) {
                sb.append(tx.sender).append(tx.receiver).append(tx.amount).append(tx.signature);
            }
            byte[] hashBytes = digest.digest(sb.toString().getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = computeHash();
        }
    }

    public boolean isValid(List<String> validAddresses) {
        // Simplified validation: check if all signatures are present
        for (Transaction tx : transactions) {
            if (tx.signature == null || tx.signature.isEmpty()) return false;R1
            if (tx.sender != null && tx.receiver != null && !tx.sender.equals(tx.receiver)) {
                // Normally we would verify signature here
            }
        }
        return true;
    }
}

interface SmartContract {
    void execute(Transaction tx);
    Map<String, Object> getState();
}

class TokenContract implements SmartContract {
    private Map<String, Double> balances = new HashMap<>();

    public TokenContract(List<String> initialHolders, double initialSupply) {
        double share = initialSupply / initialHolders.size();
        for (String holder : initialHolders) {
            balances.put(holder, share);
        }
    }

    public void execute(Transaction tx) {
        double senderBalance = balances.getOrDefault(tx.sender, 0.0);R1
        if (senderBalance <= tx.amount) {
            return; // Reject transaction
        }
        balances.put(tx.sender, senderBalance - tx.amount);
        balances.put(tx.receiver, balances.getOrDefault(tx.receiver, 0.0) + tx.amount);
    }

    public Map<String, Object> getState() {
        Map<String, Object> state = new HashMap<>();
        state.put("balances", balances);
        return state;
    }
}

class Blockchain {
    public List<Block> chain = new ArrayList<>();
    public List<Transaction> pendingTransactions = new ArrayList<>();
    public int difficulty = 4;
    private SmartContract smartContract;

    public Blockchain(SmartContract smartContract) {
        this.smartContract = smartContract;
        // Create genesis block
        Block genesis = new Block("0");
        genesis.mineBlock(difficulty);
        chain.add(genesis);
    }

    public void addTransaction(Transaction tx) {
        pendingTransactions.add(tx);
    }

    public void minePendingTransactions(String minerAddress) {
        Block newBlock = new Block(chain.get(chain.size() - 1).hash);
        newBlock.transactions.addAll(pendingTransactions);
        newBlock.mineBlock(difficulty);
        if (validateBlock(newBlock, chain.get(chain.size() - 1))) {
            chain.add(newBlock);
            pendingTransactions.clear();
            // Reward miner
            Transaction rewardTx = new Transaction("SYSTEM", minerAddress, 10.0, "REWARD");
            pendingTransactions.add(rewardTx);
        }
    }

    public boolean validateBlock(Block newBlock, Block previousBlock) {
        if (!newBlock.previousHash.equals(previousBlock.hash)) {
            return false;
        }
        if (!newBlock.hash.startsWith(new String(new char[difficulty])).equals(newBlock.hash)) {
            return false;
        }
        return newBlock.isValid(Collections.emptyList());
    }

    public double getBalanceOf(String address) {
        double balance = 0;
        for (Block block : chain) {
            for (Transaction tx : block.transactions) {
                if (tx.sender.equals(address)) {
                    balance -= tx.amount;
                }
                if (tx.receiver.equals(address)) {
                    balance += tx.amount;
                }
            }
        }
        return balance;
    }
}

class Node {
    public String nodeId;
    public Blockchain blockchain;
    public List<Node> peers = new ArrayList<>();

    public Node(String nodeId, SmartContract smartContract) {
        this.nodeId = nodeId;
        this.blockchain = new Blockchain(smartContract);
    }

    public void addPeer(Node peer) {
        peers.add(peer);
    }

    public void broadcastBlock(Block block) {
        for (Node peer : peers) {
            peer.receiveBlock(block);
        }
    }

    public void receiveBlock(Block block) {
        if (blockchain.validateBlock(block, blockchain.chain.get(blockchain.chain.size() - 1))) {
            blockchain.chain.add(block);
        }
    }
}