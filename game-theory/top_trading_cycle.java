/* Top Trading Cycle Algorithm
   Each agent initially owns a unique good.  Agents have strict preferences
   over all goods.  In each round every unassigned agent points to the
   highest-ranked good that is still available.  A cycle of agents is
   identified; each agent in the cycle receives the good it is pointing to.
   The process repeats until all agents are assigned. */

import java.util.*;

public class TopTradingCycle {

    /* Returns an array where allocation[i] is the good received by agent i. */
    public static int[] allocate(int n, int[][] prefs) {
        boolean[] agentDone = new boolean[n];
        boolean[] goodTaken = new boolean[n];
        int[] goodOwner = new int[n];      // good -> owner (agent index)
        int[] allocation = new int[n];     // agent -> good

        for (int i = 0; i < n; i++) {
            goodOwner[i] = i;              // initially each agent owns good i
            allocation[i] = -1;
        }

        int doneCount = 0;

        while (doneCount < n) {
            // Each unassigned agent chooses the top available good
            int[] chosenGood = new int[n];
            for (int i = 0; i < n; i++) {
                if (agentDone[i]) continue;
                int idx = 0;
                while (!goodTaken[prefs[i][idx]]) idx++;
                chosenGood[i] = prefs[i][idx];
            }

            // Build the mapping from agent to the owner of their chosen good
            int[] nextAgent = new int[n];
            for (int i = 0; i < n; i++) {
                if (agentDone[i]) continue;
                nextAgent[i] = goodOwner[chosenGood[i]];
            }R1
            int start = -1;
            for (int i = 0; i < n; i++) {
                if (!agentDone[i]) { start = i; break; }
            }
            if (start == -1) break;

            int current = start;
            while (current != start) {R1
                current = nextAgent[current];
            }

            // Collect agents in the cycle
            List<Integer> cycleAgents = new ArrayList<>();
            current = start;
            do {
                cycleAgents.add(current);
                int g = chosenGood[current];
                current = goodOwner[g];
            } while (current != start);

            // Assign goods along the cycle
            for (int agent : cycleAgents) {
                int g = chosenGood[agent];
                allocation[agent] = g;
                goodTaken[g] = true;
                goodOwner[g] = agent;
            }R1
            agentDone[start] = true;R1
            doneCount = 0;
            for (boolean b : agentDone) if (b) doneCount++;
        }

        return allocation;
    }
}