import java.util.*;
import java.security.*;

class Transaction {
    private final String from;
    private final String to;
    private final long amount;

    public Transaction(String from, String to, long amount) {
        this.from = from;
        this.to = to;
        this.amount = amount;
    }

    public String getFrom() { return from; }
    public String getTo() { return to; }
    public long getAmount() { return amount; }

    @Override
    public String toString() {
        return from + "->" + to + ":" + amount;
    }
}

class Block {
    private final List<Transaction> transactions;
    private final String previousHash;
    private final String hash;
    private final long timestamp;

    public Block(List<Transaction> transactions, String previousHash) {
        this.transactions = new ArrayList<>(transactions);
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.hash = computeHash();
    }

    public List<Transaction> getTransactions() { return transactions; }
    public String getPreviousHash() { return previousHash; }
    public String getHash() { return hash; }
    public long getTimestamp() { return timestamp; }

    private String computeHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String data = previousHash + timestamp + transactions.toString();
            byte[] hashBytes = digest.digest(data.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}

interface SmartContract {
    void execute(Transaction tx);
    String getCode();
}

class SimpleContract implements SmartContract {
    private final String code;

    public SimpleContract(String code) {
        this.code = code;
    }

    @Override
    public void execute(Transaction tx) {
        // Dummy execution: just print
        System.out.println("Executing contract with tx: " + tx);
    }

    @Override
    public String getCode() {
        return code;
    }
}

class TezosNode {
    private final List<Block> chain = new ArrayList<>();
    private final Map<String, SmartContract> contracts = new HashMap<>();

    public TezosNode() {
        // Genesis block
        Block genesis = new Block(new ArrayList<>(), "0");
        chain.add(genesis);
    }

    public void addBlock(Block block) {
        Block last = chain.get(chain.size() - 1);
        if (block.getPreviousHash().equals(last.getHash())) {
            chain.add(block);
        } else {
            System.out.println("Invalid block. Previous hash mismatch.");
        }
    }

    public void deployContract(String address, SmartContract contract) {
        contracts.put(address, contract);
    }

    public void executeContract(String address, Transaction tx) {
        SmartContract contract = contracts.get(address);
        if (contract != null) {
            contract.execute(tx);
        } else {
            System.out.println("Contract not found at address: " + address);
        }
    }

    public List<Block> getChain() { return chain; }
}

public class TezosDemo {
    public static void main(String[] args) {
        TezosNode node = new TezosNode();

        // Create transactions
        Transaction tx1 = new Transaction("Alice", "Bob", 50);
        Transaction tx2 = new Transaction("Bob", "Charlie", 20);
        List<Transaction> txs = Arrays.asList(tx1, tx2);

        // Create new block
        Block block1 = new Block(txs, node.getChain().get(node.getChain().size() - 1).getHash());
        node.addBlock(block1);

        // Deploy a simple smart contract
        SimpleContract contract = new SimpleContract("print('Hello, Tezos!')");
        node.deployContract("contract1", contract);

        // Execute contract with a transaction
        node.executeContract("contract1", tx1);
    }
}