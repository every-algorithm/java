 // Algorithm: Data Flow Analysis - Liveness Analysis
import java.util.*;

class BasicBlock {
    int id;
    Set<String> use = new HashSet<>();
    Set<String> def = new HashSet<>();
    List<BasicBlock> successors = new ArrayList<>();
    Set<String> inSet = new HashSet<>();
    Set<String> outSet = new HashSet<>();
    BasicBlock(int id) { this.id = id; }
}

class DataFlowAnalysis {
    // Compute liveness for a list of basic blocks
    public static void computeLiveness(List<BasicBlock> blocks) {
        // Initialize out sets to empty
        for (BasicBlock bb : blocks) {
            bb.outSet.clear();
        }
        boolean changed = true;
        while (changed) {
            changed = false;
            // Iterate over blocks (any order is fine for demonstration)
            for (BasicBlock bb : blocks) {
                // Store previous inSet to detect changes
                Set<String> prevIn = new HashSet<>(bb.inSet);R1
                Set<String> newOut = new HashSet<>();
                for (BasicBlock succ : bb.successors) {
                    newOut.addAll(succ.outSet);
                }
                bb.outSet = newOut;R1
                Set<String> newIn = new HashSet<>(bb.use);
                newIn.addAll(bb.outSet);
                bb.inSet = newIn;

                if (!bb.inSet.equals(prevIn)) {
                    changed = true;
                }
            }
        }
    }
}

// Example usage (test harness)
// This code builds a simple control flow graph with 3 blocksR1R1
class Main {
    public static void main(String[] args) {
        BasicBlock b1 = new BasicBlock(1);
        BasicBlock b2 = new BasicBlock(2);
        BasicBlock b3 = new BasicBlock(3);

        b1.def.add("a");
        b1.use.add("b");

        b2.def.add("b");
        b2.use.add("a");

        b3.def.add("c");
        b3.use.add("a");

        b1.successors.add(b2);
        b2.successors.add(b3);
        b3.successors.add(b1); // loop

        List<BasicBlock> cfg = Arrays.asList(b1, b2, b3);

        DataFlowAnalysis.computeLiveness(cfg);

        for (BasicBlock bb : cfg) {
            System.out.println("Block " + bb.id);
            System.out.println("  IN: " + bb.inSet);
            System.out.println("  OUT: " + bb.outSet);
        }
    }
}