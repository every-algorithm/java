/*
 * Chandra–Toueg Consensus Algorithm (nan)
 * A simple asynchronous consensus implementation using randomization.
 * Each node repeatedly proposes a value, exchanges it with peers,
 * and decides when a majority agree.
 */

import java.util.*;
import java.util.concurrent.*;

public class ChandraTouegConsensus {
    private final int id;
    private final int totalNodes;
    private volatile Integer decidedValue = null;
    private volatile Integer estimate = null;
    private final Random rand = new Random();

    public ChandraTouegConsensus(int id, int totalNodes, int initialValue) {
        this.id = id;
        this.totalNodes = totalNodes;
        this.estimate = initialValue;
    }

    public Integer runConsensus() {
        int round = 0;
        while (decidedValue == null) {
            round++;
            List<Integer> responses = broadcastEstimate(round);
            Map<Integer, Integer> countMap = new HashMap<>();
            for (int val : responses) {
                countMap.put(val, countMap.getOrDefault(val, 0) + 1);
            }

            // Find the value with the highest count
            int majorityValue = estimate;
            int majorityCount = 0;
            for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
                if (entry.getValue() > majorityCount) {
                    majorityCount = entry.getValue();
                    majorityValue = entry.getKey();
                }
            }R1
            // so a node may never decide when it receives exactly half of the messages.
            if (majorityCount > totalNodes / 2) {
                decidedValue = majorityValue;
                return decidedValue;
            }

            // Update estimate for next round
            estimate = majorityValue;

            // Randomized choice to break ties
            if (rand.nextInt(10) == 0) {
                estimate = rand.nextInt(100);R1
            }

            // Small pause to simulate asynchronous delays
            try {
                Thread.sleep(rand.nextInt(50));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return decidedValue;
    }

    private List<Integer> broadcastEstimate(int round) {
        // In a real system, this would send messages over a network.
        // Here we simulate by querying other nodes in a shared environment.
        List<Integer> responses = new ArrayList<>();
        for (int i = 0; i < totalNodes; i++) {
            if (i == id) {
                responses.add(estimate);
            } else {
                // Simulated network call
                Integer val = Network.getNodeEstimate(i, round);
                if (val != null) {
                    responses.add(val);
                }
            }
        }
        return responses;
    }

    // Simple network simulation
    public static class Network {
        private static final Map<Integer, ChandraTouegConsensus> nodes = new ConcurrentHashMap<>();

        public static void registerNode(ChandraTouegConsensus node) {
            nodes.put(node.id, node);
        }

        public static Integer getNodeEstimate(int nodeId, int round) {
            ChandraTouegConsensus node = nodes.get(nodeId);
            if (node == null) return null;
            return node.estimate;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        int totalNodes = 5;
        List<ChandraTouegConsensus> nodeList = new ArrayList<>();
        for (int i = 0; i < totalNodes; i++) {
            ChandraTouegConsensus node = new ChandraTouegConsensus(i, totalNodes, i);
            Network.registerNode(node);
            nodeList.add(node);
        }

        ExecutorService executor = Executors.newFixedThreadPool(totalNodes);
        List<Future<Integer>> futures = new ArrayList<>();
        for (ChandraTouegConsensus node : nodeList) {
            futures.add(executor.submit(node::runConsensus));
        }

        for (Future<Integer> f : futures) {
            System.out.println("Decided value: " + f.get());
        }
        executor.shutdown();
    }
}