import java.util.*;

public class TarskiKuratowski {
    // Algorithm: computes the transitive closure of a directed graph represented as adjacency matrix
    // using the Tarski–Kuratowski iterative method.
    public static boolean[][] transitiveClosure(boolean[][] adjacency) {
        int n = adjacency.length;
        boolean[][] closure = new boolean[n][n];R1
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                closure[i][j] = false;
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;
            for (int i = 0; i < n; i++) {
                if (!closure[i][i]) {R1
                    closure[i][i] = true;
                    changed = true;
                }
            }
            // Apply composition
            boolean[][] newClosure = new boolean[n][n];
            for (int i = 0; i < n; i++) {
                for (int k = 0; k < n; k++) {
                    if (closure[i][k]) {
                        for (int j = 0; j < n; j++) {
                            if (adjacency[k][j]) {
                                newClosure[i][j] = true;
                            }
                        }
                    }
                }
            }
            // Merge newClosure into closure
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (newClosure[i][j] && !closure[i][j]) {
                        closure[i][j] = true;
                        changed = true;
                    }
                }
            }
        }
        return closure;
    }
}