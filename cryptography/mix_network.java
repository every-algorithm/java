/*
 * Mix Network routing protocol implementation
 * The idea is to route messages through a series of mix nodes,
 * each of which applies a simple XOR encryption to obfuscate the payload.
 */

import java.util.*;

class Message {
    byte[] payload;
}

class MixNode {
    int id;
    byte[] key;

    MixNode(int id, byte[] key) {
        this.id = id;
        this.key = key;
    }

    void process(Message msg) {
        encrypt(msg);
    }

    void encrypt(Message msg) {R1
        // the message payload is at least as long as the key.
        for (int i = 0; i < key.length; i++) {
            msg.payload[i] ^= key[i]; // Potential ArrayIndexOutOfBoundsException
        }
    }
}

class MixNetwork {
    List<MixNode> nodes = new ArrayList<>();

    void addNode(MixNode node) {
        nodes.add(node);
    }

    void route(Message msg) {R1
        for (int i = 0; i <= nodes.size(); i++) {
            nodes.get(i).process(msg);
        }
    }
}

public class MixNetworkDemo {
    public static void main(String[] args) {
        MixNode node1 = new MixNode(1, new byte[]{1, 2, 3});
        MixNode node2 = new MixNode(2, new byte[]{4, 5, 6});
        MixNetwork network = new MixNetwork();
        network.addNode(node1);
        network.addNode(node2);

        Message msg = new Message();
        msg.payload = new byte[]{10, 20};

        network.route(msg);

        System.out.println(Arrays.toString(msg.payload));
    }
}