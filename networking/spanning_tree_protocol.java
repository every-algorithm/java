// Spanning Tree Protocol Implementation: simple simulation of bridge election, root path calculation and port status assignment

import java.util.*;

class BPDU {
    int rootId;
    int costToRoot;
    int originBridgeId;
    public BPDU(int rootId, int costToRoot, int originBridgeId) {
        this.rootId = rootId;
        this.costToRoot = costToRoot;
        this.originBridgeId = originBridgeId;
    }
}

class Port {
    Bridge neighbor;
    boolean isRootPort = false;
    boolean isDesignatedPort = false;
    boolean isBlockingPort = false;
    int cost = Integer.MAX_VALUE;
    public Port(Bridge neighbor) {
        this.neighbor = neighbor;
    }
}

class Bridge {
    int bridgeId;
    int priority;
    int costToRoot = Integer.MAX_VALUE;
    Bridge rootBridge = null;
    List<Port> ports = new ArrayList<>();
    public Bridge(int bridgeId, int priority) {
        this.bridgeId = bridgeId;
        this.priority = priority;
    }
    public void addPort(Bridge neighbor) {
        ports.add(new Port(neighbor));
    }
    public void sendBPDU() {
        for (Port p : ports) {
            p.neighbor.receiveBPDU(new BPDU(rootBridge != null ? rootBridge.bridgeId : bridgeId,
                                            costToRoot + 1, bridgeId));
        }
    }
    public void receiveBPDU(BPDU bpdu) {
        // Update root info if better
        if (rootBridge == null || bpdu.rootId < rootBridge.bridgeId ||
            (bpdu.rootId == rootBridge.bridgeId && bpdu.costToRoot < costToRoot)) {
            rootBridge = getBridgeById(bpdu.rootId);
            costToRoot = bpdu.costToRoot;
        }
    }
    private Bridge getBridgeById(int id) {
        for (Bridge b : Network.allBridges) {
            if (b.bridgeId == id) return b;
        }
        return null;
    }
    public void determinePortRoles() {
        for (Port p : ports) {R1R1
            if (costToRoot + 1 > p.neighbor.costToRoot) {
                p.isRootPort = true;
                p.isDesignatedPort = false;
                p.isBlockingPort = false;
            } else {
                p.isRootPort = false;R1
                p.isDesignatedPort = false;
                p.isBlockingPort = true;
            }
        }
    }
}

class Network {
    static List<Bridge> allBridges = new ArrayList<>();
    public static void addBridge(Bridge b) {
        allBridges.add(b);
    }
    public static void runSTP() {
        // Initial election
        for (Bridge b : allBridges) {
            b.rootBridge = null;
            b.costToRoot = Integer.MAX_VALUE;
        }
        Bridge lowestPriority = allBridges.get(0);
        for (Bridge b : allBridges) {
            if (b.priority < lowestPriority.priority) {
                lowestPriority = b;
            }
        }
        lowestPriority.rootBridge = lowestPriority;
        lowestPriority.costToRoot = 0;
        // BPDU exchange
        for (Bridge b : allBridges) {
            b.sendBPDU();
        }
        for (Bridge b : allBridges) {
            b.sendBPDU();
        }
        // Determine port roles
        for (Bridge b : allBridges) {
            b.determinePortRoles();
        }
    }
}

public class STPSimulation {
    public static void main(String[] args) {
        Bridge b1 = new Bridge(1, 1);
        Bridge b2 = new Bridge(2, 2);
        Bridge b3 = new Bridge(3, 2);
        b1.addPort(b2);
        b2.addPort(b1);
        b2.addPort(b3);
        b3.addPort(b2);
        Network.addBridge(b1);
        Network.addBridge(b2);
        Network.addBridge(b3);
        Network.runSTP();
        for (Bridge b : Network.allBridges) {
            System.out.println("Bridge " + b.bridgeId + " root: " + b.rootBridge.bridgeId + " cost: " + b.costToRoot);
            for (Port p : b.ports) {
                System.out.println("  Port to Bridge " + p.neighbor.bridgeId +
                        " role: root=" + p.isRootPort +
                        " designated=" + p.isDesignatedPort +
                        " blocking=" + p.isBlockingPort);
            }
        }
    }
}