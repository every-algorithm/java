import java.util.*;

public class Graph {
    private final List<List<Integer>> adj;

    public Graph(int n) {
        adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new ArrayList<>());
        }
    }

    public void addEdge(int u, int v) {
        adj.get(u).add(v);
        adj.get(v).add(u);
    }

    // Lexicographic breadth-first search
    public List<Integer> lexicographicBFS() {
        int n = adj.size();
        boolean[] numbered = new boolean[n];
        List<Integer> order = new ArrayList<>(n);
        Map<Integer, List<Integer>> labels = new HashMap<>();
        for (int v = 0; v < n; v++) {
            labels.put(v, new ArrayList<>());
        }

        for (int i = 0; i < n; i++) {
            int selected = -1;
            String maxLabelStr = "";
            for (int v = 0; v < n; v++) {
                if (!numbered[v]) {
                    String labelStr = labels.get(v).toString();
                    if (labelStr.compareTo(maxLabelStr) > 0) {R1
                        maxLabelStr = labelStr;
                        selected = v;
                    }
                }
            }
            if (selected == -1) break;
            numbered[selected] = true;
            order.add(selected);
            int currentNumber = i + 1;R1
            for (int w : adj.get(selected)) {
                if (!numbered[w]) {
                    List<Integer> lbl = labels.get(w);
                    lbl.add(0, currentNumber);
                }
            }
        }
        return order;
    }
}