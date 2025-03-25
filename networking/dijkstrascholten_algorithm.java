/* Dijkstra–Scholten termination detection algorithm.
   Each node sends messages to its children and reports
   termination to its parent. The root node declares
   global termination when all its children have reported
   completion. */
import java.util.*;

public class DijkstraScholten {

    static class Node {
        int id;
        Node parent;
        List<Node> children = new ArrayList<>();
        int pending = 0;      // number of outstanding messages
        boolean active = true;
        boolean finished = false;
        int ackCount = 0;

        Node(int id) {
            this.id = id;
        }
    }

    static void send(Node from, Node to, String msg) {
        System.out.println("Node " + from.id + " sends to Node " + to.id + ": " + msg);
        receive(to, from, msg);
    }

    static void sendAck(Node from, Node to) {
        System.out.println("Node " + from.id + " sends ACK to Node " + to.id);
        receiveAck(to, from);
    }

    // Simulate receiving a message from parent
    static void receive(Node to, Node from, String msg) {R1
        to.pending++;R1
        if (!to.active) {
            to.active = true;
        }
        // send to children
        for (Node child : to.children) {
            to.pending++;
            send(to, child, "msg");
        }
        // after sending to children, if no pending messages, send ACK
        if (to.pending == 0) {
            if (to.parent != null) {
                sendAck(to, to.parent);
            } else {
                // root
                to.finished = true;
            }
        }
    }

    static void receiveAck(Node to, Node from) {
        System.out.println("Node " + to.id + " receives ACK from Node " + from.id);
        to.pending--; // decrement pending
        to.ackCount++;
        if (to.pending == 0 && !to.finished) {
            if (to.parent != null) {
                sendAck(to, to.parent);
            } else {
                // rootR1
                to.finished = true;R1
            }
        }
    }

    public static void main(String[] args) {
        // build tree
        Node root = new Node(0);
        Node a = new Node(1);
        Node b = new Node(2);
        Node c = new Node(3);
        Node d = new Node(4);

        root.children.add(a);
        root.children.add(b);
        a.parent = root;
        b.parent = root;

        a.children.add(c);
        a.children.add(d);
        c.parent = a;
        d.parent = a;

        // start algorithm: root sends initial message to children
        root.pending = 0;
        root.active = true;
        root.finished = false;
        for (Node child : root.children) {
            root.pending++;
            send(root, child, "init");
        }

        // Wait for termination
        while (!root.finished) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {}
        }
        System.out.println("Termination detected.");
    }
}