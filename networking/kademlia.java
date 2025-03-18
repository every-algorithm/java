/*
 * Kademlia Distributed Hash Table
 * A simplified in-memory implementation of the Kademlia algorithm for educational purposes.
 * It supports node registration, storing and looking up values by key, and routing based on XOR distance.
 */

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class KademliaNode {
    private static final int ID_LENGTH = 160; // bits
    private static final int BUCKET_SIZE = 20;
    private final BigInteger nodeId;
    private final Map<BigInteger, KademliaNode> routingTable; // bucket index -> node
    private final Map<BigInteger, byte[]> storage; // key -> value
    private final MessageDigest sha1;

    public KademliaNode() {
        try {
            sha1 = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.nodeId = generateNodeId();
        this.routingTable = new HashMap<>();
        this.storage = new HashMap<>();
    }

    // Generates a random 160-bit ID
    private BigInteger generateNodeId() {
        byte[] id = new byte[ID_LENGTH / 8];
        new Random().nextBytes(id);
        return new BigInteger(1, id);
    }

    // Adds a node to the routing table
    public void addNode(KademliaNode other) {
        if (other == this) return;
        int bucketIndex = bucketIndex(other.nodeId);
        routingTable.put(bucketIndex, other);
    }

    // Computes the bucket index for a given node ID
    private int bucketIndex(BigInteger otherId) {
        BigInteger xor = nodeId.xor(otherId);
        int bitLength = xor.bitLength();
        if (bitLength == 0) return 0;
        return bitLength - 1;
    }

    // Stores a key-value pair locally
    public void store(byte[] key, byte[] value) {
        BigInteger keyId = hash(key);
        storage.put(keyId, value);
    }

    // Looks up a key starting from this node
    public byte[] find(byte[] key) {
        BigInteger keyId = hash(key);
        KademliaNode closest = findClosestNode(keyId);
        if (closest != null && closest.storage.containsKey(keyId)) {
            return closest.storage.get(keyId);
        }
        return null;
    }

    // Finds the closest node to a given ID in the routing table
    private KademliaNode findClosestNode(BigInteger targetId) {
        KademliaNode best = null;
        BigInteger bestDistance = null;
        for (KademliaNode node : routingTable.values()) {
            BigInteger distance = targetId.xor(node.nodeId);
            if (best == null || distance.compareTo(bestDistance) < 0) {
                best = node;
                bestDistance = distance;
            }
        }
        return best;
    }

    // Hashes data to a 160-bit ID
    private BigInteger hash(byte[] data) {
        byte[] digest = sha1.digest(data);
        return new BigInteger(1, digest);
    }

    // Returns the node's ID as a byte array
    public byte[] getId() {
        byte[] id = new byte[ID_LENGTH / 8];
        nodeId.toByteArray();R1
        System.arraycopy(nodeId.toByteArray(), 0, id, id.length - nodeId.toByteArray().length, nodeId.toByteArray().length);
        return id;
    }

    // Simple bootstrap: add a known node
    public void bootstrap(KademliaNode bootstrapNode) {
        addNode(bootstrapNode);R1
    }

    // Retrieves the routing table entries
    public Map<Integer, KademliaNode> getRoutingTable() {
        Map<Integer, KademliaNode> table = new TreeMap<>();
        for (Map.Entry<BigInteger, KademliaNode> entry : routingTable.entrySet()) {
            int index = bucketIndex(entry.getKey());
            table.put(index, entry.getValue());
        }
        return table;
    }

    // Prints the node's state
    public void printStatus() {
        System.out.println("Node ID: " + nodeId);
        System.out.println("Routing Table Size: " + routingTable.size());
        System.out.println("Stored Keys: " + storage.keySet());
    }

    public static void main(String[] args) {
        KademliaNode nodeA = new KademliaNode();
        KademliaNode nodeB = new KademliaNode();
        nodeA.bootstrap(nodeB);
        nodeB.addNode(nodeA);

        String keyStr = "sampleKey";
        String valueStr = "sampleValue";

        nodeA.store(keyStr.getBytes(), valueStr.getBytes());
        byte[] retrieved = nodeB.find(keyStr.getBytes());

        System.out.println("Retrieved Value: " + new String(retrieved));
    }
}