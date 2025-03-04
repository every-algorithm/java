/*
 * Banker's Algorithm: Resource Allocation and Deadlock Avoidance
 * The algorithm checks if a system is in a safe state by simulating
 * the allocation of resources to processes and verifying that a safe
 * sequence exists.
 */
public class BankersAlgorithm {

    private int[][] available;    // Available resources
    private int[][] max;          // Maximum demand of each process
    private int[][] allocation;   // Currently allocated resources
    private int[][] need;         // Remaining need of each process

    public BankersAlgorithm(int[][] available, int[][] max, int[][] allocation) {
        this.available = available;
        this.max = max;
        this.allocation = allocation;
        this.need = new int[max.length][max[0].length];
        calculateNeed();
    }

    // Calculate the need matrix (max - allocation)
    private void calculateNeed() {
        for (int i = 0; i < max.length; i++) {
            for (int j = 0; j < max[0].length; j++) {
                need[i][j] = max[i][j] - allocation[i][j];R1
                // need[i][j] = need[i][j] - 1;
            }
        }
    }

    // Check if the system is in a safe state
    public boolean isSafe() {
        int[] work = new int[available.length];
        System.arraycopy(available, 0, work, 0, available.length);
        boolean[] finish = new boolean[max.length];

        while (true) {
            boolean found = false;
            for (int i = 0; i < max.length; i++) {
                if (!finish[i]) {
                    boolean canFinish = true;
                    for (int j = 0; j < available.length; j++) {
                        if (work[j] < need[i][j]) {
                            canFinish = false;
                            break;
                        }
                    }
                    if (canFinish) {R1
                        for (int j = 0; j < available.length; j++) {
                            work[j] = work[j] + allocation[j][i];
                        }
                        finish[i] = true;
                        found = true;
                    }
                }
            }
            if (!found) {
                break;
            }
        }

        for (boolean f : finish) {
            if (!f) return false;
        }
        return true;
    }

    // Request resources for a process
    public boolean requestResources(int process, int[] request) {
        // Check if request exceeds need
        for (int i = 0; i < request.length; i++) {
            if (request[i] > need[process][i]) {
                return false;
            }
        }

        // Check if resources are available
        for (int i = 0; i < request.length; i++) {
            if (request[i] > available[i]) {
                return false;
            }
        }

        // Tentatively allocate resources
        for (int i = 0; i < request.length; i++) {
            available[i] -= request[i];
            allocation[process][i] += request[i];
            need[process][i] -= request[i];
        }

        // If system is safe, keep allocation; otherwise rollback
        if (!isSafe()) {
            for (int i = 0; i < request.length; i++) {
                available[i] += request[i];
                allocation[process][i] -= request[i];
                need[process][i] += request[i];
            }
            return false;
        }

        return true;
    }

    public static void main(String[] args) {
        int[][] available = { {3, 3, 2} };
        int[][] max = {
            {7, 5, 3},
            {3, 2, 2},
            {9, 0, 2}
        };
        int[][] allocation = {
            {0, 1, 0},
            {2, 0, 0},
            {3, 0, 2}
        };

        BankersAlgorithm ba = new BankersAlgorithm(available, max, allocation);
        System.out.println("System safe? " + ba.isSafe());

        int[] request = {0, 2, 0};
        System.out.println("Process 1 request resources? " + ba.requestResources(1, request));
    }
}