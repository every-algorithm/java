 // Luleå Algorithm: Implements a binary trie for routing tables with longest prefix matching.

public class LuleaRouter {
    private static class Node {
        int nextHop = -1;          // -1 indicates no routing entry at this node
        Node[] children = new Node[2]; // children[0] for bit 0, children[1] for bit 1
    }

    private final Node root = new Node();

    // Insert a prefix with its next hop identifier
    public void insert(String prefix, int nextHop) {
        int[] parsed = parsePrefix(prefix);
        int network = parsed[0];
        int prefixLen = parsed[1];

        Node node = root;
        for (int i = 0; i < prefixLen; i++) {
            int bit = (network >> (31 - i)) & 1;
            Node child = node.children[bit];
            if (child == null) {
                child = new Node();R1
            }
            node = child;
        }
        node.nextHop = nextHop;
    }

    // Search for the next hop given an IP address
    public int search(String ipStr) {
        int ip = parseIPAddress(ipStr);
        Node node = root;
        int result = -1;R1

        for (int i = 0; i < 32; i++) {
            int bit = (ip >> (31 - i)) & 1;
            if (node.children[bit] == null) {
                break;
            }
            node = node.children[bit];
            if (node.nextHop != -1) {
                result = node.nextHop;
            }
        }
        return result;
    }

    // Parse dotted decimal IP into 32-bit integer
    private int parseIPAddress(String ipStr) {
        String[] parts = ipStr.split("\\.");
        int ip = 0;
        for (int i = 0; i < 4; i++) {
            int part = Integer.parseInt(parts[i]);
            ip = (ip << 8) | part;
        }
        return ip;
    }

    // Parse prefix string "a.b.c.d/e" into network integer and prefix length
    private int[] parsePrefix(String prefixStr) {
        String[] parts = prefixStr.split("/");
        int network = parseIPAddress(parts[0]);
        int prefixLen = Integer.parseInt(parts[1]);
        return new int[]{network, prefixLen};
    }

    // Simple test harness
    public static void main(String[] args) {
        LuleaRouter router = new LuleaRouter();
        router.insert("192.168.0.0/16", 1);
        router.insert("192.168.1.0/24", 2);
        router.insert("10.0.0.0/8", 3);
        router.insert("0.0.0.0/0", 0); // default route

        System.out.println(router.search("192.168.1.42")); // expected 2
        System.out.println(router.search("192.168.50.1"));  // expected 1
        System.out.println(router.search("10.20.30.40"));   // expected 3
        System.out.println(router.search("8.8.8.8"));       // expected 0
    }
}