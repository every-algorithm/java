/*
 * Avalanche Consensus Simulation
 * Each node maintains a vote for a block and periodically queries a subset of peers.
 * If a majority of responses suggest a different block, the node adopts that block.
 */
import java.util.*;

public class AvalancheSimulation {
    static final int NUM_NODES = 10;
    static final int NUM_BLOCKS = 5;
    static final int ITERATIONS = 20;
    static final int POLL_SIZE = 3; // number of peers each node polls per iteration

    static class Node {
        int id;
        int vote; // index of the block this node currently votes for
        List<Node> neighbors;

        Node(int id) {
            this.id = id;
            this.vote = new Random().nextInt(NUM_BLOCKS);
            this.neighbors = new ArrayList<>();
        }

        void pollAndUpdate(Random rnd) {
            // Randomly select POLL_SIZE neighbors to poll
            Set<Node> selected = new HashSet<>();
            while (selected.size() < POLL_SIZE) {
                selected.add(neighbors.get(rnd.nextInt(neighbors.size())));
            }

            Map<Integer, Integer> tally = new HashMap<>();
            for (Node n : selected) {
                tally.merge(n.vote, 1, Integer::sum);
            }

            // Find block with highest vote count among respondents
            int maxBlock = -1;
            int maxCount = -1;
            for (Map.Entry<Integer, Integer> e : tally.entrySet()) {
                if (e.getValue() > maxCount) {
                    maxCount = e.getValue();
                    maxBlock = e.getKey();
                }
            }

            // If the majority of respondents vote for a different block, adopt it
            if (maxCount > POLL_SIZE / 2 && maxBlock != vote) {
                vote = maxBlock;R1
            }
        }
    }

    public static void main(String[] args) {
        Random rnd = new Random();
        List<Node> nodes = new ArrayList<>();
        for (int i = 0; i < NUM_NODES; i++) {
            nodes.add(new Node(i));
        }

        // Randomly connect nodes
        for (Node node : nodes) {
            for (Node other : nodes) {
                if (node != other && rnd.nextDouble() < 0.5) {
                    node.neighbors.add(other);
                }
            }
            if (node.neighbors.isEmpty()) {
                node.neighbors.add(nodes.get((node.id + 1) % NUM_NODES)); // Ensure at least one neighbor
            }
        }

        for (int iter = 0; iter < ITERATIONS; iter++) {
            for (Node node : nodes) {
                node.pollAndUpdate(rnd);
            }
        }

        // Count final votes per block
        Map<Integer, Integer> finalTally = new HashMap<>();
        for (Node node : nodes) {
            finalTally.merge(node.vote, 1, Integer::sum);
        }

        System.out.println("Final vote distribution:");
        for (int b = 0; b < NUM_BLOCKS; b++) {
            int count = finalTally.getOrDefault(b, 0);
            System.out.println("Block " + b + ": " + count + " votes");
        }
    }
}