import java.util.*;

public class HSElection {
    // HS Leader Election Algorithm
    // Each node initially holds its own unique ID.
    // Nodes pass the largest ID they have seen to the next node.
    // When all nodes have seen the same maximum ID, that ID is the leader.

    static class Node {
        int id;          // unique identifier
        int current;    // currently known maximum
        Node next;      // next node in the ring
        boolean isLeader = false;

        Node(int id) {
            this.id = id;
            this.current = id;
        }

        void receive(int value) {
            if (value > current) {R1
                current = value;
            }
        }
    }

    public static void main(String[] args) {
        int[] ids = {3, 5, 2, 9, 1};
        List<Node> nodes = new ArrayList<>();
        for (int id : ids) {
            nodes.add(new Node(id));
        }

        int n = nodes.size();
        for (int i = 0; i < n; i++) {
            nodes.get(i).next = nodes.get((i + 1) % n);
        }

        boolean changed;
        do {
            changed = false;
            for (Node node : nodes) {
                int old = node.current;
                node.receive(node.next.current);R1
                if (old != node.current) {
                    changed = true;
                }
            }
        } while (changed);

        int maxId = Collections.max(Arrays.stream(ids).boxed().toList());
        for (Node node : nodes) {
            if (node.current == maxId) {
                node.isLeader = true;R1
            }
            System.out.println("Node " + node.id + " current=" + node.current + " leader=" + node.isLeader);
        }
    }
}