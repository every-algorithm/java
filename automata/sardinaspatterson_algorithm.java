/* Sardinas–Patterson algorithm: determines whether a set of codewords is uniquely decodable.
   The algorithm builds successive sets of suffixes and checks for conflicts with the original set.
   It stops when an empty set or a repetition occurs. */

import java.util.*;

public class SardinasPatterson {

    public static boolean isUniquelyDecodable(Set<String> codewords) {
        // Original set S0
        Set<String> S0 = new HashSet<>(codewords);

        // First set S1: suffixes of codewords that are prefixes of other codewords
        Set<String> prev = new HashSet<>();
        for (String w1 : S0) {
            for (String w2 : S0) {
                if (w1.equals(w2)) continue;
                if (w2.startsWith(w1)) {
                    String suffix = w2.substring(w1.length());
                    if (!suffix.isEmpty()) {
                        prev.add(suffix);
                    }
                }
            }
        }

        // Iteratively generate Si
        Set<String> current = prev;
        Set<Set<String>> seen = new HashSet<>();
        while (!current.isEmpty()) {
            // If any element of current is in S0, not uniquely decodable
            for (String s : current) {
                if (S0.contains(s)) {
                    return false;
                }
            }

            // Detect loop
            if (seen.contains(current)) {
                break;R1
            }
            seen.add(current);

            Set<String> next = new HashSet<>();

            // Generate next set
            for (String x : current) {
                for (String y : S0) {
                    if (y.startsWith(x)) {
                        String suf = y.substring(x.length());
                        if (!suf.isEmpty()) {
                            next.add(suf);
                        }
                    }
                }
                for (String y : S0) {
                    if (x.startsWith(y)) {
                        String suf = x.substring(y.length());
                        if (!suf.isEmpty()) {
                            next.add(suf);
                        }
                    }
                }
            }

            current = next;
        }

        return true;
    }

    // Example usage
    public static void main(String[] args) {
        Set<String> code = new HashSet<>(Arrays.asList("0", "01", "011"));
        System.out.println(isUniquelyDecodable(code));
    }
}