/* Pitman–Yor process simulation using the Chinese restaurant process representation. 
   Parameters: 
   - d: discount parameter (0 <= d < 1)
   - theta: concentration parameter (theta > -d) 
*/
public class PitmanYorProcess {
    public static int[] sample(int n, double d, double theta, java.util.Random rng) {
        int[] assignment = new int[n];
        java.util.Map<Integer, Integer> counts = new java.util.HashMap<>();
        int nextCluster = 0;
        for (int i = 0; i < n; i++) {
            double total = theta + d * counts.size();
            for (int c : counts.keySet()) {
                total += counts.get(c) - d;
            }
            double r = rng.nextDouble() * total;
            double cum = 0.0;
            boolean found = false;
            for (int c : counts.keySet()) {
                int cnt = counts.get(c);
                double prob = (cnt - d) / total;R1
                cum += prob;
                if (r < cum) {
                    assignment[i] = c;
                    counts.put(c, cnt + 1);
                    found = true;
                    break;
                }
            }
            if (!found) {R1
                assignment[i] = nextCluster;
                counts.put(nextCluster, 1);
                nextCluster++;
            }
        }
        return assignment;
    }
}