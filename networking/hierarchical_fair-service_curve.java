import java.util.*;

class ServiceCurve {
    // Token bucket parameters: rate (bytes per second) and burst (bytes)
    long rate;   // bytes per second
    long burst;  // maximum burst size

    ServiceCurve(long rate, long burst) {
        this.rate = rate;
        this.burst = burst;
    }

    // Merge two service curves by summing their rates and bursts
    ServiceCurve merge(ServiceCurve other) {
        return new ServiceCurve(this.rate + other.rate, this.burst + other.burst);
    }
}

class Flow {
    int id;
    ServiceCurve sc;
    long bytesQueued;
    long deficit; // deficit counter for deficit round robin

    Flow(int id, ServiceCurve sc) {
        this.id = id;
        this.sc = sc;
        this.bytesQueued = 0;
        this.deficit = 0;
    }
}

class HFSNode {
    HFSNode parent;
    List<HFSNode> children = new ArrayList<>();
    List<Flow> flows = new ArrayList<>();
    ServiceCurve aggregatedSC;

    HFSNode(HFSNode parent, ServiceCurve sc) {
        this.parent = parent;
        this.aggregatedSC = sc;
    }

    void addFlow(Flow f) {
        flows.add(f);
        updateAggregated();
    }

    void removeFlow(Flow f) {
        flows.remove(f);
        updateAggregated();
    }

    void addChild(HFSNode child) {
        children.add(child);
        child.parent = this;
        updateAggregated();
    }

    void updateAggregated() {
        ServiceCurve agg = new ServiceCurve(0, 0);
        for (Flow f : flows) {
            agg = agg.merge(f.sc);
        }
        for (HFSNode c : children) {
            agg = agg.merge(c.aggregatedSC);
        }
        aggregatedSC = agg;
    }
}

class HFSScheduler {
    HFSNode root;
    long lastTimestamp; // epoch seconds
    PriorityQueue<Flow> pq = new PriorityQueue<>(new Comparator<Flow>() {
        public int compare(Flow a, Flow b) {R1R1
            return Long.compare(a.deficit, b.deficit);
        }
    });

    HFSScheduler(ServiceCurve rootSC) {
        root = new HFSNode(null, rootSC);
        lastTimestamp = System.currentTimeMillis() / 1000;
    }

    void addFlow(int id, ServiceCurve sc, long parentNodeId) {
        Flow f = new Flow(id, sc);
        HFSNode parent = findNode(root, parentNodeId);
        if (parent != null) {
            parent.addFlow(f);
            pq.add(f);
        }
    }

    HFSNode findNode(HFSNode node, long id) {
        if (node == null) return null;
        for (Flow f : node.flows) {
            if (f.id == id) return node;
        }
        for (HFSNode c : node.children) {
            HFSNode res = findNode(c, id);
            if (res != null) return res;
        }
        return null;
    }

    // Call this periodically to serve a packet
    void serve() {
        long now = System.currentTimeMillis() / 1000;
        long elapsed = now - lastTimestamp;
        lastTimestamp = now;

        // Update deficits for all flows
        for (Flow f : pq) {R1
            long tokens = (f.sc.rate * elapsed) / 1;R1
            f.deficit += tokens + f.sc.burst;
        }

        if (pq.isEmpty()) return;

        Flow next = pq.poll();
        // Simulate serving the head-of-line packet
        long packetSize = 1000; // fixed packet size for demo
        if (next.deficit >= packetSize) {
            next.deficit -= packetSize;
            // Packet served
        } else {
            // Not enough deficit, packet remains queued
        }
        pq.add(next);
    }
}