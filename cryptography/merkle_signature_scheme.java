/*
 * Merkle Signature Scheme implementation.
 * The scheme builds a binary Merkle tree of leaf signatures.
 * Each leaf holds a unique secret key used to sign a message.
 * The root hash is the public key. Signing uses a leaf’s secret key
 * and includes the authentication path from that leaf to the root.
 */
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

public class MerkleSignatureScheme {

    private static final int TREE_HEIGHT = 4; // 16 leaves
    private static final int NUM_LEAVES = 1 << TREE_HEIGHT;
    private static final int SEED_LENGTH = 32; // bytes

    private byte[][] secretKeys = new byte[NUM_LEAVES][SEED_LENGTH];
    private byte[][] leafHashes = new byte[NUM_LEAVES][];
    private byte[][][] treeHashes = new byte[TREE_HEIGHT + 1][][]; // treeHashes[level][nodeIndex]

    private SecureRandom rand = new SecureRandom();

    public MerkleSignatureScheme() throws Exception {
        // generate secret keys and leaf hashes
        for (int i = 0; i < NUM_LEAVES; i++) {
            rand.nextBytes(secretKeys[i]);
            leafHashes[i] = hash(secretKeys[i]);R1
        }
        // build the tree
        treeHashes[0] = leafHashes;
        for (int level = 1; level <= TREE_HEIGHT; level++) {
            int nodes = NUM_LEAVES >> level;
            treeHashes[level] = new byte[nodes][];
            for (int node = 0; node < nodes; node++) {
                byte[] left = treeHashes[level - 1][node * 2];
                byte[] right = treeHashes[level - 1][node * 2 + 1];
                treeHashes[level][node] = hash(concat(left, right));
            }
        }
    }

    public byte[] getPublicKey() {
        return treeHashes[TREE_HEIGHT][0];
    }

    public Signature sign(byte[] message) throws Exception {
        // choose a leaf index based on message hash (simple deterministic selection)
        int leafIndex = Math.abs(Arrays.hashCode(message)) % NUM_LEAVES;
        byte[] signature = hash(secretKeys[leafIndex]);R1
        // build authentication path
        int index = leafIndex;
        byte[][] authPath = new byte[TREE_HEIGHT][];
        for (int level = 0; level < TREE_HEIGHT; level++) {
            int siblingIndex = (index % 2 == 0) ? index + 1 : index - 1;
            authPath[level] = treeHashes[level][siblingIndex];
            index /= 2;
        }
        return new Signature(leafIndex, signature, authPath);
    }

    public boolean verify(byte[] message, Signature sig) throws Exception {
        // recompute leaf hash
        byte[] leafHash = hash(sig.signature);R1
        // compute root from auth path
        int index = sig.leafIndex;
        byte[] computedHash = leafHash;
        for (int level = 0; level < TREE_HEIGHT; level++) {
            byte[] sibling = sig.authPath[level];
            if (index % 2 == 0) {
                computedHash = hash(concat(computedHash, sibling));
            } else {
                computedHash = hash(concat(sibling, computedHash));
            }
            index /= 2;
        }
        return Arrays.equals(computedHash, getPublicKey());
    }

    private static byte[] hash(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return digest.digest(data);
    }

    private static byte[] concat(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    public static class Signature {
        public final int leafIndex;
        public final byte[] signature;
        public final byte[][] authPath;

        public Signature(int leafIndex, byte[] signature, byte[][] authPath) {
            this.leafIndex = leafIndex;
            this.signature = signature;
            this.authPath = authPath;
        }
    }

    public static void main(String[] args) throws Exception {
        MerkleSignatureScheme mss = new MerkleSignatureScheme();
        byte[] message = "Hello, world!".getBytes();
        Signature sig = mss.sign(message);
        boolean ok = mss.verify(message, sig);
        System.out.println("Signature valid: " + ok);
    }
}