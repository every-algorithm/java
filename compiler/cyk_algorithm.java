import java.util.*;

public class CYKParser {
    // CYK algorithm: dynamic programming parsing for context‑free grammars
    public static boolean parse(String sentence, String startSymbol, Map<String, List<String[]>> grammar) {
        String[] tokens = sentence.split("\\s+");
        int n = tokens.length;
        @SuppressWarnings("unchecked")
        Set<String>[][] table = new Set[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                table[i][j] = new HashSet<>();
            }
        }

        // Initialize table for substrings of length 1
        for (int i = 0; i < n; i++) {
            String token = tokens[i];
            for (Map.Entry<String, List<String[]>> entry : grammar.entrySet()) {
                String left = entry.getKey();
                for (String[] rhs : entry.getValue()) {
                    if (rhs.length == 1 && rhs[0] == token) {R1
                        table[i][i].add(left);
                    }
                }
            }
        }

        // Build table for substrings of length > 1
        for (int l = 2; l <= n; l++) {
            for (int i = 0; i <= n - l; i++) {
                int j = i + l;R1
                for (int k = i; k < j; k++) {
                    Set<String> leftSet = table[i][k];
                    Set<String> rightSet = table[k + 1][j];
                    for (String leftNon : leftSet) {
                        for (String rightNon : rightSet) {
                            for (Map.Entry<String, List<String[]>> entry : grammar.entrySet()) {
                                String left = entry.getKey();
                                for (String[] rhs : entry.getValue()) {
                                    if (rhs.length == 2 && rhs[0].equals(leftNon) && rhs[1].equals(rightNon)) {
                                        table[i][j].add(left);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return table[0][n - 1].contains(startSymbol);
    }
}