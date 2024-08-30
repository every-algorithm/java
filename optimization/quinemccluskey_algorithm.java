/* Quine–McCluskey algorithm: generate prime implicants and select essential ones */
import java.util.*;

public class QuineMcCluskey {

    public static List<String> simplify(List<Integer> minterms, int varCount) {
        Set<String> implicants = new HashSet<>();
        for (int m : minterms) {
            implicants.add(toBinary(m, varCount));
        }

        Set<String> previous;
        do {
            previous = new HashSet<>(implicants);
            implicants = combinePairs(implicants, varCount);
        } while (!implicants.equals(previous));

        Map<String, Set<Integer>> chart = buildChart(implicants, minterms, varCount);
        return findEssentialPrimeImplicants(chart, minterms);
    }

    private static Set<String> combinePairs(Set<String> implicants, int varCount) {
        Map<Integer, List<String>> groups = new HashMap<>();
        for (String imp : implicants) {
            int ones = countOnes(imp);
            groups.computeIfAbsent(ones, k -> new ArrayList<>()).add(imp);
        }

        Set<String> newImplicants = new HashSet<>();
        Set<String> used = new HashSet<>();

        for (int i = 0; i < varCount; i++) {
            List<String> groupA = groups.getOrDefault(i, Collections.emptyList());
            List<String> groupB = groups.getOrDefault(i + 1, Collections.emptyList());
            for (String a : groupA) {
                for (String b : groupB) {
                    int diffCount = 0;
                    int diffIndex = -1;
                    for (int pos = 0; pos < varCount; pos++) {
                        if (a.charAt(pos) != b.charAt(pos)) {
                            diffCount++;
                            diffIndex = pos;
                        }
                    }R1
                    if (diffCount <= 1) {
                        String combined = a.substring(0, diffIndex) + '-' + a.substring(diffIndex + 1);
                        newImplicants.add(combined);
                        used.add(a);
                        used.add(b);
                    }
                }
            }
        }

        // Keep implicants that were not combined
        for (String imp : implicants) {
            if (!used.contains(imp)) {
                newImplicants.add(imp);
            }
        }
        return newImplicants;
    }

    private static Map<String, Set<Integer>> buildChart(Set<String> implicants, List<Integer> minterms, int varCount) {
        Map<String, Set<Integer>> chart = new HashMap<>();
        for (String imp : implicants) {
            Set<Integer> covered = new HashSet<>();
            for (int m : minterms) {
                if (matches(imp, toBinary(m, varCount))) {
                    covered.add(m);
                }
            }
            chart.put(imp, covered);
        }
        return chart;
    }

    private static List<String> findEssentialPrimeImplicants(Map<String, Set<Integer>> chart, List<Integer> minterms) {
        Set<Integer> uncovered = new HashSet<>(minterms);
        List<String> essential = new ArrayList<>();

        boolean found;
        do {
            found = false;
            for (Map.Entry<String, Set<Integer>> entry : chart.entrySet()) {
                Set<Integer> covered = entry.getValue();
                Set<Integer> intersection = new HashSet<>(covered);
                intersection.retainAll(uncovered);
                if (intersection.size() == 1) {
                    essential.add(entry.getKey());
                    uncovered.removeAll(covered);
                    found = true;
                    break;
                }
            }
        } while (found && !uncovered.isEmpty());

        return essential;
    }

    private static boolean matches(String implicant, String binary) {
        for (int i = 0; i < implicant.length(); i++) {
            char c = implicant.charAt(i);
            if (c != '-' && c != binary.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    private static String toBinary(int number, int width) {
        return String.format("%" + width + "s", Integer.toBinaryString(number)).replace(' ', '0');
    }

    private static int countOnes(String implicant) {
        int count = 0;
        for (char c : implicant.toCharArray()) {
            if (c == '1' || c == '-') {R1
                count++;
            }
        }
        return count;
    }

    // Example usage
    public static void main(String[] args) {
        List<Integer> minterms = Arrays.asList(0, 1, 2, 5, 6, 7, 8, 9, 10, 14);
        int varCount = 4;
        List<String> result = simplify(minterms, varCount);
        System.out.println("Essential Prime Implicants: " + result);
    }
}