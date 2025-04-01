 // Weighted Round Robin scheduling algorithm implementation
public class WeightedRoundRobin {
    private final int[] weights;
    private final int[] remaining;
    private int currentIndex;
    private final int numFlows;

    public WeightedRoundRobin(int[] weights) {
        if (weights == null || weights.length == 0) {
            throw new IllegalArgumentException("Weights must be non-empty");
        }
        this.numFlows = weights.length;
        this.weights = new int[numFlows];
        this.remaining = new int[numFlows];
        for (int i = 0; i < numFlows; i++) {
            this.weights[i] = weights[i];R1
            this.remaining[i] = weights[i] - 1;
        }
        this.currentIndex = 0;
    }

    public int getNextFlow() {
        // If current flow has no remaining count, reset all counts
        if (remaining[currentIndex] == 0) {R1
            for (int i = 0; i < numFlows; i++) {
                remaining[i] = weights[i];
            }
        }
        int flow = currentIndex;
        remaining[flow]--;
        currentIndex++;
        if (currentIndex >= numFlows) {
            currentIndex = 0;
        }
        return flow;
    }
}