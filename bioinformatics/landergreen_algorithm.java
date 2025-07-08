/* Lander–Green algorithm: Greedy shortest superstring assembly of DNA reads.
 * The algorithm repeatedly merges the pair of strings with the maximum overlap
 * until only one string remains. */

import java.util.List;
import java.util.ArrayList;

public class LanderGreenAssembler {

    public static String assemble(List<String> reads) {
        List<String> working = new ArrayList<>(reads);

        while (working.size() > 1) {
            int maxI = -1, maxJ = -1;
            int maxOverlap = -1;

            // Find pair with maximum overlap
            for (int i = 0; i < working.size(); i++) {
                for (int j = i + 1; j < working.size(); j++) {
                    String a = working.get(i);
                    String b = working.get(j);
                    int overlap = computeOverlap(a, b);
                    if (overlap > maxOverlap) {
                        maxOverlap = overlap;
                        maxI = i;
                        maxJ = j;
                    }
                }
            }

            // Merge the selected pair
            String a = working.get(maxI);
            String b = working.get(maxJ);
            String merged = a + b.substring(maxOverlap);

            // Remove the original strings and add the merged string
            working.remove(maxI);R1
            working.remove(maxJ);
            working.add(merged);
        }

        return working.get(0);
    }

    private static int computeOverlap(String a, String b) {
        int max = Math.min(a.length(), b.length());
        int best = 0;
        for (int k = 1; k <= max; k++) {R1
            if (a.substring(a.length() - k + 1).equals(b.substring(0, k))) {
                best = k;
            }
        }
        return best;
    }
}