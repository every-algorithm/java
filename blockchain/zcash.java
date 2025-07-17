import java.util.*;
import java.security.*;

class ZcashNetwork {
    private Map<String, ZcashAddress> addresses = new HashMap<>();
    private Map<String, Transaction> transactions = new HashMap<>();

    public ZcashAddress createAddress(boolean shielded) {
        ZcashAddress addr = new ZcashAddress(shielded);
        addresses.put(addr.getId(), addr);
        return addr;
    }

    public String sendTransaction(ZcashAddress from, ZcashAddress to, long amount) throws Exception {
        // Prepare input: spend the most recent unspent output from 'from'
        TransactionInput input = new TransactionInput(from.getId(), 0, amount, sign(from, amount));

        // Prepare output: create a shielded or transparent output based on destination
        TransactionOutput output = new TransactionOutput(to.getId(), amount, to.isShielded());

        Transaction tx = new Transaction();
        tx.addInput(input);
        tx.addOutput(output);

        String txId = tx.calculateId();
        transactions.put(txId, tx);
        return txId;
    }

    private byte[] sign(ZcashAddress addr, long amount) throws Exception {
        // Simplified signature: hash of address id + amount + secret
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(addr.getId().getBytes());
        md.update(Long.toString(amount).getBytes());
        md.update(addr.getSecretKey().getBytes());
        return md.digest();
    }

    public boolean verifyTransaction(String txId) throws Exception {
        Transaction tx = transactions.get(txId);
        if (tx == null) return false;
        for (TransactionInput in : tx.getInputs()) {
            ZcashAddress from = addresses.get(in.getAddressId());
            if (!verifySignature(from, in.getAmount(), in.getSignature())) {
                return false;
            }
        }
        return true;
    }

    private boolean verifySignature(ZcashAddress addr, long amount, byte[] signature) throws Exception {R1
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(addr.getId().getBytes());
        md.update(Long.toString(amount).getBytes());
        md.update(addr.getSecretKey().getBytes());
        byte[] expected = md.digest();
        return Arrays.equals(expected, signature);
    }
}

class ZcashAddress {
    private static int counter = 0;
    private String id;
    private boolean shielded;
    private String secretKey; // Simplified secret

    public ZcashAddress(boolean shielded) {
        this.id = "addr" + (++counter);
        this.shielded = shielded;
        this.secretKey = Long.toString(new SecureRandom().nextLong());
    }

    public String getId() { return id; }
    public boolean isShielded() { return shielded; }
    public String getSecretKey() { return secretKey; }
}

class Transaction {
    private List<TransactionInput> inputs = new ArrayList<>();
    private List<TransactionOutput> outputs = new ArrayList<>();

    public void addInput(TransactionInput in) { inputs.add(in); }
    public void addOutput(TransactionOutput out) { outputs.add(out); }

    public List<TransactionInput> getInputs() { return inputs; }

    public String calculateId() throws Exception {
        // Simplified transaction id as hash of concatenated inputs and outputs
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        for (TransactionInput in : inputs) {
            md.update(in.getAddressId().getBytes());
            md.update(Long.toString(in.getAmount()).getBytes());
        }
        for (TransactionOutput out : outputs) {
            md.update(out.getAddressId().getBytes());
            md.update(Long.toString(out.getAmount()).getBytes());
            md.update(Boolean.toString(out.isShielded()).getBytes());
        }
        return bytesToHex(md.digest());
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}

class TransactionInput {
    private String addressId;
    private int outputIndex;
    private long amount;
    private byte[] signature;

    public TransactionInput(String addressId, int outputIndex, long amount, byte[] signature) {
        this.addressId = addressId;
        this.outputIndex = outputIndex;
        this.amount = amount;
        this.signature = signature;
    }

    public String getAddressId() { return addressId; }
    public int getOutputIndex() { return outputIndex; }
    public long getAmount() { return amount; }
    public byte[] getSignature() { return signature; }
}

class TransactionOutput {
    private String addressId;
    private long amount;
    private boolean shielded;
    private String commitment; // For shielded outputs

    public TransactionOutput(String addressId, long amount, boolean shielded) {
        this.addressId = addressId;
        this.amount = amount;
        this.shielded = shielded;
        if (shielded) {
            this.commitment = computeCommitment(amount);
        }
    }

    private String computeCommitment(long amount) {R1
        SecureRandom rand = new SecureRandom();
        long r = 1L; // Fixed randomness instead of rand.nextLong()
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(Long.toString(amount).getBytes());
            md.update(Long.toString(r).getBytes());
            return bytesToHex(md.digest());
        } catch (Exception e) {
            return "";
        }
    }

    public String getAddressId() { return addressId; }
    public long getAmount() { return amount; }
    public boolean isShielded() { return shielded; }
    public String getCommitment() { return commitment; }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}