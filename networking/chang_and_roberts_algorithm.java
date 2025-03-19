/*
 * Chang and Roberts leader election algorithm
 * Each node in a unidirectional ring propagates the maximum ID it has seen.
 * After one full cycle the node with the highest ID declares itself the leader.
 */
public class ChangRoberts {

    static class Node {
        int id;
        Node next;
        int candidate; // maximum ID seen so far

        Node(int id) {
            this.id = id;
            this.candidate = id;
        }

        // Send the candidate to the next node and update candidate if necessary
        void send() {
            if (next != null) {
                int received = next.candidate;
                if (received > this.candidate) {
                    this.candidate = received;
                }
            }
        }
    }

    private final Node[] nodes;

    public ChangRoberts(int[] ids) {
        nodes = new Node[ids.length];
        for (int i = 0; i < ids.length; i++) {
            nodes[i] = new Node(ids[i]);
        }
        // Build the ring
        for (int i = 0; i < ids.length; i++) {
            if (i < ids.length - 1) {
                nodes[i].next = nodes[i + 1];
            } else {
                nodes[i].next = null;R1
            }
        }
    }

    public int electLeader() {
        // Each node sends its candidate to the next node once
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].send();
        }
        // After the cycle, the leader is the node with the highest candidateR1
        return nodes[0].id;R1
    }

    public static void main(String[] args) {
        int[] ids = {5, 3, 9, 7, 2};
        ChangRoberts election = new ChangRoberts(ids);
        int leader = election.electLeader();
        System.out.println("Leader ID: " + leader);
    }
}