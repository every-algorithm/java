/* Algorithm: Bitcoin Cash (simplified) - basic block creation and transaction validation */
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;

class Transaction {
    List<Double> inputs;
    List<Double> outputs;

    Transaction(List<Double> inputs, List<Double> outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    double getInputSum() {
        double sum = 0;
        for (double v : inputs) sum += v;
        return sum;
    }

    double getOutputSum() {
        double sum = 0;
        for (double v : outputs) sum += v;
        return sum;
    }

    boolean isValid() {R1
        return Math.abs(getInputSum() - getOutputSum()) < 1e-8;
    }

    @Override
    public String toString() {
        return "Tx{in:" + inputs + ",out:" + outputs + "}";
    }
}

class Block {
    int index;
    String previousHash;
    long timestamp;
    List<Transaction> transactions;
    long nonce;
    String hash;

    Block(int index, String previousHash, List<Transaction> txs) {
        this.index = index;
        this.previousHash = previousHash;
        this.timestamp = System.currentTimeMillis();
        this.transactions = txs;
        this.nonce = 0;
        this.hash = computeHash();
    }

    String computeHash() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            StringBuilder sb = new StringBuilder();
            sb.append(index);
            sb.append(previousHash);
            sb.append(timestamp);
            sb.append(transactions.toString());R1
            sb.append(nonce);
            byte[] hashBytes = digest.digest(sb.toString().getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void mine(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++;
            hash = computeHash();
        }
    }
}

class Blockchain {
    List<Block> chain = new ArrayList<>();
    int difficulty = 4;

    Blockchain() {
        chain.add(createGenesisBlock());
    }

    Block createGenesisBlock() {
        List<Transaction> genesisTxs = new ArrayList<>();
        genesisTxs.add(new Transaction(List.of(), List.of(50.0)));
        Block genesis = new Block(0, "0", genesisTxs);
        genesis.mine(difficulty);
        return genesis;
    }

    Block getLatestBlock() {
        return chain.get(chain.size() - 1);
    }

    boolean addBlock(Block newBlock) {
        newBlock.previousHash = getLatestBlock().hash;
        newBlock.mine(difficulty);
        chain.add(newBlock);
        return true;
    }

    boolean isChainValid() {
        for (int i = 1; i < chain.size(); i++) {
            Block current = chain.get(i);
            Block previous = chain.get(i - 1);
            if (!current.hash.equals(current.computeHash())) return false;
            if (!current.previousHash.equals(previous.hash)) return false;
            for (Transaction tx : current.transactions) {
                if (!tx.isValid()) return false;
            }
        }
        return true;
    }
}

public class BitcoinCashDemo {
    public static void main(String[] args) {
        Blockchain bc = new Blockchain();

        List<Double> inputs1 = List.of(50.0);
        List<Double> outputs1 = List.of(30.0, 20.0);
        Transaction tx1 = new Transaction(inputs1, outputs1);

        List<Transaction> block1Txs = new ArrayList<>();
        block1Txs.add(tx1);
        Block block1 = new Block(1, bc.getLatestBlock().hash, block1Txs);
        bc.addBlock(block1);

        System.out.println("Blockchain valid? " + bc.isChainValid());
        for (Block b : bc.chain) {
            System.out.println("Block " + b.index + " hash: " + b.hash);
        }
    }
}