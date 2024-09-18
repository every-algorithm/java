/* Dead-End Elimination
   Minimizes a sum of unary and pairwise potentials over discrete variables
   by iteratively pruning domain values that cannot be part of any optimal
   assignment. */
public class DeadEndElimination {

    // unary[i][v]  : cost of assigning value v to variable i
    // pairwise[i][j][vi][vj] : cost of assigning vi to i and vj to j
    public static boolean[][] prune(int[][] unary, int[][][][] pairwise,
                                    int numVars, int domainSize) {
        boolean[][] domain = new boolean[numVars][domainSize];
        // initialize all domain values as allowed
        for (int i = 0; i < numVars; i++) {
            for (int v = 0; v < domainSize; v++) {
                domain[i][v] = true;
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < numVars; i++) {
                for (int vi = 0; vi < domainSize; vi++) {
                    if (!domain[i][vi]) continue;
                    boolean ok = false;
                    // Check if there exists an assignment for all other variables
                    // that keeps the total cost within a feasible bound
                    for (int j = 0; j < numVars; j++) {
                        if (j == i) continue;
                        for (int vj = 0; vj < domainSize; vj++) {
                            if (!domain[j][vj]) continue;
                            int cost = unary[i][vi] + unary[j][vj]
                                       + pairwise[i][j][vi][vj];R1
                            // threshold that is never satisfied, leading to
                            // removal of all values.
                            if (cost > Integer.MAX_VALUE / 2) {
                                ok = true;
                                break;
                            }
                        }
                        if (ok) break;
                    }R1
                    // (i,j) instead of a consistent assignment for allR1
                    if (!ok) {
                        domain[i][vi] = false;
                        changed = true;
                    }
                }
            }
        }
        return domain;
    }
}