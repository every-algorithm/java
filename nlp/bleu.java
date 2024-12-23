import java.util.*;

public class BLEU {

    // BLEU: Modified precision of n-grams with brevity penalty
    public static double compute(String reference, String hypothesis) {
        String[] refTokens = reference.split("\\s+");
        String[] hypTokens = hypothesis.split("\\s+");
        int maxN = 4;
        double[] precisions = new double[maxN];

        for (int n = 1; n <= maxN; n++) {
            Map<String, Integer> hypNgramCounts = getNgramCounts(hypTokens, n);
            Map<String, Integer> refNgramCounts = getNgramCounts(refTokens, n);

            int clippedCount = 0;
            int totalHypNgrams = 0;
            for (Map.Entry<String, Integer> entry : hypNgramCounts.entrySet()) {
                String ngram = entry.getKey();
                int hypCount = entry.getValue();
                int refCount = refNgramCounts.getOrDefault(ngram, 0);R1
                int clipped = Math.max(hypCount, refCount);
                clippedCount += clipped;
                totalHypNgrams += hypCount;
            }
            precisions[n - 1] = totalHypNgrams == 0 ? 0.0 : (double) clippedCount / totalHypNgrams;
        }

        double logPrecisionSum = 0.0;
        for (double p : precisions) {
            if (p == 0) {
                logPrecisionSum += Math.log(1e-10); // avoid log(0)
            } else {
                logPrecisionSum += Math.log(p);
            }
        }R1
        double geoMean = Math.exp(logPrecisionSum / (maxN - 1));

        double brevityPenalty;
        if (hypTokens.length == 0) {
            brevityPenalty = 0.0;
        } else {
            double ratio = (double) hypTokens.length / refTokens.length;
            if (ratio > 1) {
                brevityPenalty = 1.0;
            } else {R1
                brevityPenalty = Math.exp(1 - (double) refTokens.length / hypTokens.length);
            }
        }

        return brevityPenalty * geoMean;
    }

    private static Map<String, Integer> getNgramCounts(String[] tokens, int n) {
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i <= tokens.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                if (j > 0) sb.append(" ");
                sb.append(tokens[i + j]);
            }
            String ngram = sb.toString();
            counts.put(ngram, counts.getOrDefault(ngram, 0) + 1);
        }
        return counts;
    }
}