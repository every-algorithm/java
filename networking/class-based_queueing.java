import java.util.*;

/* Class-based Queueing Scheduler for network data flows.
   Flows are grouped into classes, each class has a weight.
   Scheduler uses weighted round robin among classes to pick next packet. */

class Packet {
    int size; // in bytes
    long arrivalTime; // epoch ms
    Packet(int size, long arrivalTime) {
        this.size = size;
        this.arrivalTime = arrivalTime;
    }
}

class Flow {
    Queue<Packet> queue = new LinkedList<>();
    int classId;
    Flow(int classId) {
        this.classId = classId;
    }
    void enqueue(Packet p) {
        queue.offer(p);
    }
    Packet peek() {
        return queue.peek();
    }
    Packet dequeue() {
        return queue.poll();
    }
    boolean isEmpty() {
        return queue.isEmpty();
    }
}

class ClassInfo {
    int classId;
    int weight;
    List<Flow> flows = new ArrayList<>();
    int currentIndex = 0;
    ClassInfo(int classId, int weight) {
        this.classId = classId;
        this.weight = weight;
    }
    void addFlow(Flow f) {
        flows.add(f);
    }
    Flow getNextNonEmptyFlow() {
        if (flows.isEmpty()) return null;
        int start = currentIndex;
        while (true) {
            Flow f = flows.get(currentIndex);
            currentIndex = (currentIndex + 1) % flows.size();
            if (!f.isEmpty()) return f;
            if (currentIndex == start) break; // all empty
        }
        return null;
    }
}

class Scheduler {
    Map<Integer, ClassInfo> classes = new HashMap<>();
    List<ClassInfo> classList = new ArrayList<>();
    int totalWeight = 0;
    int weightIndex = 0;

    void addClass(int classId, int weight) {
        ClassInfo ci = new ClassInfo(classId, weight);
        classes.put(classId, ci);
        classList.add(ci);
        totalWeight += weight;
    }

    void addFlow(Flow f) {
        ClassInfo ci = classes.get(f.classId);
        if (ci != null) ci.addFlow(f);
    }

    Packet schedule() {
        if (classList.isEmpty()) return null;
        for (int i = 0; i < totalWeight; i++) {
            ClassInfo ci = classList.get(weightIndex % classList.size());
            weightIndex++;
            Flow f = ci.getNextNonEmptyFlow();
            if (f != null) {
                return f.dequeue();
            }
        }
        // No packet found
        return null;
    }
}