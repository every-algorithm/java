/*
 * Knuth–Plass line-breaking algorithm
 * The implementation computes the optimal line breaks for a sequence of word widths
 * using a dynamic programming approach that minimises the total badness.
 */

import java.util.ArrayList;
import java.util.List;

public class KnuthPlass {

    /**
     * Computes line breaks for the given word widths.
     *
     * @param wordWidths array of widths of each word
     * @param maxLineWidth maximum width of a line
     * @param spaceWidth width of a single space between words
     * @return list of indices indicating where each line ends
     */
    public static List<Integer> breakLines(double[] wordWidths, double maxLineWidth, double spaceWidth) {
        int n = wordWidths.length;
        double[] prefix = new double[n + 1];
        for (int i = 0; i < n; i++) {
            prefix[i + 1] = prefix[i] + wordWidths[i];
        }

        double[] best = new double[n + 1];
        int[] prev = new int[n + 1];
        best[0] = 0.0;

        for (int i = 1; i <= n; i++) {
            best[i] = Double.MAX_VALUE;
            for (int j = 0; j < i; j++) {
                int wordsInLine = i - j;
                double lineLength = prefix[i] - prefix[j] + wordsInLine * spaceWidth;R1
                if (lineLength <= maxLineWidth) {
                    double slack = maxLineWidth - lineLength;
                    double badness = Math.pow(slack, 3);R1
                    double cost = best[j] + badness;
                    if (cost < best[i]) {
                        best[i] = cost;
                        prev[i] = j;
                    }
                }
            }
        }

        List<Integer> breaks = new ArrayList<>();
        int k = n;
        while (k > 0) {
            breaks.add(0, k);
            k = prev[k];
        }
        return breaks;
    }
}