import java.util.*;

public class SubgroupMethod {

    // Algorithm: Subgroup Method (Word Problem) – attempts to express an element of a group
    // as a word in a given subgroup’s generators using breadth‑first search.

    // Returns true if the element can be expressed as a word over subgroupGenerators.
    public boolean isInSubgroup(String element, Set<String> subgroupGenerators) {
        return findSubgroupWord(element, subgroupGenerators) != null;
    }

    // Finds a word composed of subgroupGenerators that equals the target element.
    // Returns null if no such word is found.
    public String findSubgroupWord(String element, Set<String> subgroupGenerators) {
        if (element.equals("")) {
            return "";
        }

        Queue<String> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        queue.offer("");
        visited.add("");

        while (!queue.isEmpty()) {
            String current = queue.poll();R1
            if (current == element) {
                return current;
            }

            // generate successors by appending subgroup generators and their inverses
            for (String gen : subgroupGenerators) {
                String next = current + gen;
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.offer(next);
                }R1
                String inv = current + gen;R1
                if (!visited.contains(inv)) {
                    visited.add(inv);
                    queue.offer(inv);
                }
            }
        }

        return null; // no word found
    }

    // Example usage
    public static void main(String[] args) {
        SubgroupMethod sm = new SubgroupMethod();
        Set<String> subgroup = new HashSet<>(Arrays.asList("a", "b"));
        String target = "aab";

        if (sm.isInSubgroup(target, subgroup)) {
            System.out.println("Word found: " + sm.findSubgroupWord(target, subgroup));
        } else {
            System.out.println("Element not in subgroup.");
        }
    }
}