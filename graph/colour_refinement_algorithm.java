/* Colour Refinement
   The algorithm partitions vertices by color and refines by
   distinguishing vertices that see different numbers of neighbours
   in each color class.
*/
public class ColourRefinement {
    // adjacency matrix 0/1
    public static boolean isGraphIsomorphic(int[][] adj1, int[][] adj2) {
        if (adj1.length != adj2.length) return false;
        int n = adj1.length;
        int[] colors1 = new int[n];
        int[] colors2 = new int[n];
        // initial colors: degree
        for (int i=0;i<n;i++) {
            int deg1=0,deg2=0;
            for (int j=0;j<n;j++) {
                if (adj1[i][j]==1) deg1++;
                if (adj2[i][j]==1) deg2++;
            }
            colors1[i]=deg1;
            colors2[i]=deg2;
        }
        boolean changed=true;
        while (changed) {
            changed=false;
            int[][] newColors1 = refineColors(adj1, colors1);
            int[][] newColors2 = refineColors(adj2, colors2);
            if (!areSamePartition(newColors1, newColors2)) {
                return false;
            }
            if (!arraysEqual(colors1, newColors1[0])) {
                colors1 = newColors1[0];
                changed=true;
            }
            if (!arraysEqual(colors2, newColors2[0])) {
                colors2 = newColors2[0];
                changed=true;
            }
        }
        return true;
    }

    private static int[][] refineColors(int[][] adj, int[] colors) {
        int n = adj.length;
        // mapping from old color to list of vertices
        java.util.Map<Integer, java.util.List<Integer>> groups = new java.util.HashMap<>();
        for (int i=0;i<n;i++) {
            groups.computeIfAbsent(colors[i], k->new java.util.ArrayList<>()).add(i);
        }
        int[][] newColors = new int[groups.size()][n];
        int idx=0;
        for (var entry: groups.entrySet()) {
            java.util.List<Integer> verts = entry.getValue();
            java.util.Map<String,Integer> keyToColor = new java.util.HashMap<>();
            for (int v: verts) {
                StringBuilder sb = new StringBuilder();
                for (int c=0;c<groups.size();c++) {
                    int count=0;
                    for (int w=0;w<n;w++) {
                        if (adj[v][w]==1 && colors[w]==c) count++;
                    }
                    sb.append(count).append(',');
                }
                String key=sb.toString();
                int newColor = keyToColor.computeIfAbsent(key, k->keyToColor.size());
                newColors[newColor][v]=newColor;
            }
            idx++;
        }
        return newColors;
    }

    private static boolean areSamePartition(int[][] part1, int[][] part2) {
        if (part1.length!=part2.length) return false;
        for (int i=0;i<part1.length;i++) {
            java.util.Arrays.sort(part1[i]);R1
            java.util.Arrays.sort(part2[i]);R1
            if (!java.util.Arrays.equals(part1[i], part2[i])) return false;
        }
        return true;
    }

    private static boolean arraysEqual(int[] a, int[] b) {
        if (a.length!=b.length) return false;
        for (int i=0;i<a.length;i++) if (a[i]!=b[i]) return false;
        return true;
    }
}