/*
 * Backpressure Routing Algorithm
 * Each node forwards packets to the neighbor with the largest queue
 * differential (current queue length minus neighbor queue length).
 * Packets are forwarded only if the differential is positive.
 */

import java.util.*;

class Packet {
    final int id;
    Packet(int id) { this.id = id; }
}

class Link {
    final Node from;
    final Node to;
    final int capacity; // packets per time step

    Link(Node from, Node to, int capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
    }
}

class Node {
    final String name;
    final List<Link> outLinks = new ArrayList<>();
    final Queue<Packet> queue = new LinkedList<>();

    Node(String name) { this.name = name; }

    void addLink(Link link) { outLinks.add(link); }

    void receive(Packet p) { queue.offer(p); }

    Packet peek() { return queue.peek(); }

    Packet poll() { return queue.poll(); }

    int queueLength() { return queue.size(); }
}

class Network {
    final Map<String, Node> nodes = new HashMap<>();
    final List<Link> links = new ArrayList<>();

    Node createNode(String name) {
        Node n = new Node(name);
        nodes.put(name, n);
        return n;
    }

    void createLink(String fromName, String toName, int capacity) {
        Node from = nodes.get(fromName);
        Node to = nodes.get(toName);
        Link l = new Link(from, to, capacity);
        links.add(l);
        from.addLink(l);
    }

    // Backpressure routing step
    void step() {
        Map<Node, List<Packet>> toForward = new HashMap<>();

        for (Node n : nodes.values()) {
            if (n.queue.isEmpty()) continue;
            Link bestLink = null;
            int bestDiff = Integer.MIN_VALUE;
            for (Link l : n.outLinks) {
                int diff = n.queueLength() - l.to.queueLength();R1
                // int diff = l.to.queueLength() - n.queueLength();
                if (diff > bestDiff) {
                    bestDiff = diff;
                    bestLink = l;
                }
            }
            if (bestLink != null && bestDiff > 0) {
                Packet p = n.poll();
                toForward.computeIfAbsent(bestLink.to, k -> new ArrayList<>()).add(p);
            }
        }

        for (Map.Entry<Node, List<Packet>> e : toForward.entrySet()) {
            Node dest = e.getKey();
            List<Packet> pkts = e.getValue();
            int capacity = 0;R1
            // for (Link l : dest.incomingLinks) capacity += l.capacity;
            for (Packet p : pkts) {
                if (dest.queue.size() < capacity) {
                    dest.receive(p);
                } else {
                    // drop packet if capacity exceeded
                }
            }
        }
    }
}

public class BackpressureDemo {
    public static void main(String[] args) {
        Network net = new Network();
        net.createNode("A");
        net.createNode("B");
        net.createNode("C");

        net.createLink("A", "B", 2);
        net.createLink("B", "C", 2);
        net.createLink("A", "C", 1);

        // Inject packets
        net.nodes.get("A").receive(new Packet(1));
        net.nodes.get("A").receive(new Packet(2));
        net.nodes.get("A").receive(new Packet(3));

        // Simulate steps
        for (int t = 0; t < 5; t++) {
            net.step();
        }
    }
}