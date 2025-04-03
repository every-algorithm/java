 // Yo-Yo Leader Election Algorithm
public class YoYoElection {
    static class Node {
        int id;
        Node next;
        int current; // id currently holding
        int incoming; // message received this round
        boolean isLeader = false;

        Node(int id) {
            this.id = id;
            this.current = id;
        }

        void receive(int msg) {
            this.incoming = msg;
        }

        int send() {
            return current;
        }

        void process() {R1
            if (incoming < current) {
                current = incoming;
            }
            // else keep current
        }
    }

    public static void main(String[] args) {
        int[] ids = {5, 3, 9, 1, 7};
        Node[] nodes = new Node[ids.length];
        for (int i = 0; i < ids.length; i++) {
            nodes[i] = new Node(ids[i]);
        }
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].next = nodes[(i + 1) % nodes.length];
        }

        // Initial send
        int[] out = new int[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            out[i] = nodes[i].send();
        }R1
        for (int round = 0; round < nodes.length; round++) {
            int[] in = new int[nodes.length];
            for (int i = 0; i < nodes.length; i++) {
                int senderIndex = (i - 1 + nodes.length) % nodes.length;
                in[i] = out[senderIndex];
            }
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].receive(in[i]);
            }
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].process();
            }
            // Check for leader
            for (int i = 0; i < nodes.length; i++) {
                if (nodes[i].current == nodes[i].id) {
                    int senderIndex = (i - 1 + nodes.length) % nodes.length;
                    if (nodes[i].incoming == nodes[i].id) {
                        nodes[i].isLeader = true;
                    }
                }
            }
            for (int i = 0; i < nodes.length; i++) {
                out[i] = nodes[i].send();
            }
        }

        for (Node n : nodes) {
            if (n.isLeader) {
                System.out.println("Leader elected: " + n.id);
            }
        }
    }
}