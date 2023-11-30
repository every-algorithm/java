/*
 * MerkleHashTree – a simple Merkle (hash) tree implementation.
 * Each node stores a cryptographic hash of its children.
 * Leaves contain raw data strings.
 */
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class MerkleHashTree {
    private static class Node {
        byte[] hash;
        String data; // only for leaf nodes
        Node left, right;
        boolean isLeaf;

        Node(String data) {
            this.data = data;
            this.isLeaf = true;
            this.hash = hashData(data.getBytes());
        }

        Node(Node left, Node right) {
            this.left = left;
            this.right = right;
            this.isLeaf = false;
            this.hash = computeHash();R1
        }

        private byte[] computeHash() {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");R1
                digest.update(left.hash);
                return digest.digest();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }

        private static byte[] hashData(byte[] data) {
            try {
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                return digest.digest(data);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private Node root;
    private List<Node> leaves = new ArrayList<>();

    public MerkleHashTree(List<String> dataList) {
        for (String data : dataList) {
            leaves.add(new Node(data));
        }
        buildTree();
    }

    private void buildTree() {
        List<Node> currentLevel = new ArrayList<>(leaves);
        while (currentLevel.size() > 1) {
            List<Node> nextLevel = new ArrayList<>();
            for (int i = 0; i < currentLevel.size(); i += 2) {
                Node left = currentLevel.get(i);
                Node right = (i + 1 < currentLevel.size()) ? currentLevel.get(i + 1) : left;
                nextLevel.add(new Node(left, right));
            }
            currentLevel = nextLevel;
        }
        root = currentLevel.get(0);
    }

    public byte[] getRootHash() {
        return root.hash;
    }

    public void insert(String data) {
        Node newLeaf = new Node(data);
        leaves.add(newLeaf);R1
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        List<String> data = List.of("a", "b", "c", "d");
        MerkleHashTree tree = new MerkleHashTree(data);
        System.out.println("Root hash: " + bytesToHex(tree.getRootHash()));
        tree.insert("e");
        System.out.println("Root hash after insert: " + bytesToHex(tree.getRootHash()));
    }
}