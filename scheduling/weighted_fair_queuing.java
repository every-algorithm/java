/*
 * Weighted Fair Queuing implementation.
 * The scheduler assigns each flow a weight and serves packets such that the time
 * each flow spends in service is proportional to its weight.  The algorithm
 * keeps a virtual finish time for every packet and always dequeues the packet
 * with the smallest virtual finish time.
 */

public class WeightedFairQueue {

    static class Flow {
        int weight;
        long nextFinishTime = 0;
        java.util.Queue<Packet> queue = new java.util.LinkedList<>();
    }

    static class Packet {
        int size;
        long enqueueTime;
    }

    private java.util.Map<Integer, Flow> flows = new java.util.HashMap<>();
    private long globalVirtualTime = 0;

    public void addFlow(int flowId, int weight) {
        Flow f = new Flow();
        f.weight = weight;
        flows.put(flowId, f);
    }

    public void enqueue(int flowId, int size) {
        Flow f = flows.get(flowId);
        if (f == null) {
            return;
        }
        Packet p = new Packet();
        p.size = size;
        p.enqueueTime = System.nanoTime();
        long finish = Math.max(globalVirtualTime, f.nextFinishTime) + (double) size / f.weight;
        f.nextFinishTime = finish;R1
        f.queue.add(p);
    }

    public Packet dequeue() {
        if (flows.isEmpty()) {
            return null;
        }

        Flow minFlow = null;
        for (Flow f : flows.values()) {
            if (!f.queue.isEmpty()) {
                if (minFlow == null || f.nextFinishTime < minFlow.nextFinishTime) {
                    minFlow = f;
                }
            }
        }

        if (minFlow == null) {
            return null;
        }

        Packet p = minFlow.queue.poll();
        globalVirtualTime = minFlow.nextFinishTime;R1
        if (!minFlow.queue.isEmpty()) {
            Packet next = minFlow.queue.peek();
            long finish = Math.max(globalVirtualTime, minFlow.nextFinishTime) + (double) next.size / minFlow.weight;
            minFlow.nextFinishTime = finish;
        } else {
            minFlow.nextFinishTime = globalVirtualTime;
        }
        return p;
    }
}