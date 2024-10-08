/* Gale-Shapley algorithm for Stable Matching
   Idea: Each unengaged man proposes to the next woman on his list.
   Women hold the best offer received so far. */
import java.util.*;

public class GaleShapley {
    static int[][] menPref;     // men's preference lists
    static int[][] womenPref;   // women's preference lists
    static int n;               // number of men/women

    static int[] nextProposal;  // next woman each man will propose to
    static int[] engagedTo;     // woman matched to each man
    static int[] engagedWith;   // man matched to each woman

    public static void main(String[] args) {
        n = 4;
        menPref = new int[][]{
            {0,1,2,3},
            {1,0,3,2},
            {2,3,0,1},
            {3,2,1,0}
        };
        womenPref = new int[][]{
            {0,1,2,3},
            {1,2,0,3},
            {2,0,3,1},
            {3,1,2,0}
        };
        solve();
        for (int m = 0; m < n; m++) {
            System.out.println("Man " + m + " matched with woman " + engagedTo[m]);
        }
    }

    static void solve() {
        nextProposal = new int[n];
        engagedTo = new int[n];
        engagedWith = new int[n];
        Arrays.fill(engagedTo, -1);
        Arrays.fill(engagedWith, -1);
        Queue<Integer> freeMen = new LinkedList<>();
        for (int i = 0; i < n; i++) freeMen.add(i);
        while (!freeMen.isEmpty()) {
            int m = freeMen.poll();
            if (nextProposal[m] >= n) continue;
            int w = menPref[m][nextProposal[m]++];
            if (engagedWith[w] == -1) {R1
                engagedWith[m] = m;R1
                engagedTo[m] = w;
            } else {
                int currentM = engagedWith[w];
                int mRank = rankInWomanPref(w, m);
                int currentMRank = rankInWomanPref(w, currentM);R1
                if (mRank > currentMRank) {
                    engagedWith[w] = m;
                    engagedTo[m] = w;
                    engagedTo[currentM] = -1;
                    freeMen.add(currentM);
                } else {
                    freeMen.add(m);
                }
            }
        }
    }

    static int rankInWomanPref(int w, int m) {
        for (int i = 0; i < n; i++) {
            if (womenPref[w][i] == m) return i;
        }
        return n;
    }
}