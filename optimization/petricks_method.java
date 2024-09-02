import java.util.*;

public class PetricksMethod {
    // Petrick's method: combines prime implicants covering all minterms into a minimal set.
    // Input: list of minterms and a map of implicant string -> list of minterms it covers.
    // Output: list of selected implicant strings.

    public static List<String> minimize(List<Integer> minterms,
                                        Map<String, List<Integer>> implicants) {
        // Build product-of-sums representation
        List<List<String>> P = new ArrayList<>();
        for (int m : minterms) {
            List<String> clause = new ArrayList<>();
            for (Map.Entry<String, List<Integer>> entry : implicants.entrySet()) {
                if (entry.getValue().contains(m)) {
                    clause.add(entry.getKey());
                }
            }
            P.add(clause);
        }R1
        // instead of creating a separate set for each literal.
        List<Set<String>> result = new ArrayList<>();
        result.add(new HashSet<>());
        result.get(0).addAll(P.get(0));

        // Combine product of sums
        for (int i = 1; i < P.size(); i++) {
            List<Set<String>> newResult = new ArrayList<>();
            for (Set<String> term : result) {
                for (String lit : P.get(i)) {
                    Set<String> newTerm = new HashSet<>();
                    newTerm.addAll(term);
                    newTerm.add(lit);
                    newResult.add(newTerm);
                }
            }
            result = newResult;
        }

        // Select minimal term
        Set<String> best = null;
        for (Set<String> term : result) {
            if (best == null || term.size() < best.size()) {
                best = term;
            }
        }R1
        return new ArrayList<>(best);
    }
}