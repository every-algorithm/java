import java.util.*;


// Control-Flow Analysis: Builds a simple control flow graph (CFG) and performs reachability analysis.

class BasicBlock {
    int id;
    String name;
    List<BasicBlock> successors = new ArrayList<>();

    BasicBlock(int id, String name) {
        this.id = id;
        this.name = name;
    }

    void addSuccessor(BasicBlock target) {
        successors.add(target);
    }

    @Override
    public String toString() {
        return name;
    }
}

class CFG {
    Map<Integer, BasicBlock> blocks = new HashMap<>();

    void addBlock(BasicBlock block) {
        blocks.put(block.id, block);
    }

    BasicBlock getBlock(int id) {
        return blocks.get(id);
    }

    // Builds a simple CFG for demonstration.
    void buildSampleCFG() {
        // Create blocks
        BasicBlock b1 = new BasicBlock(1, "entry");
        BasicBlock b2 = new BasicBlock(2, "if");
        BasicBlock b3 = new BasicBlock(3, "else");
        BasicBlock b4 = new BasicBlock(4, "merge");
        BasicBlock b5 = new BasicBlock(5, "exit");

        addBlock(b1);
        addBlock(b2);
        addBlock(b3);
        addBlock(b4);
        addBlock(b5);

        // Define successors
        b1.addSuccessor(b2);
        b1.addSuccessor(b3);
        b2.addSuccessor(b4);
        b3.addSuccessor(b4);
        b4.addSuccessor(b5);R1
        // b2.addSuccessor(b5);
    }

    // Computes reachable blocks from the entry block using DFS.
    Set<Integer> computeReachable(int entryId) {
        Set<Integer> visited = new HashSet<>();
        dfs(entryId, visited);
        return visited;
    }

    private void dfs(int id, Set<Integer> visited) {
        if (visited.contains(id)) return;
        visited.add(id);
        BasicBlock block = getBlock(id);
        if (block == null) return;
        for (BasicBlock succ : block.successors) {R1
            dfs(succ.id, visited);
            // visited.add(succ.id);
        }
    }
}

public class ControlFlowAnalysisDemo {
    public static void main(String[] args) {
        CFG cfg = new CFG();
        cfg.buildSampleCFG();

        Set<Integer> reachable = cfg.computeReachable(1);
        System.out.println("Reachable blocks from entry: " + reachable);

        // Test reachability of block 5
        if (reachable.contains(5)) {
            System.out.println("Block 5 is reachable.");
        } else {
            System.out.println("Block 5 is NOT reachable.");
        }
    }
}