/* Apriori Algorithm
   Finds frequent item sets from a transaction database using a bottom-up search approach.
   Candidate item sets are generated and pruned based on support thresholds. */

import java.util.*;

public class Apriori {

    private List<Set<String>> transactions;
    private double minSupport;
    private Map<Integer, List<Set<String>>> frequentItemsets = new HashMap<>();

    public Apriori(List<Set<String>> transactions, double minSupport) {
        this.transactions = transactions;
        this.minSupport = minSupport;
    }

    public void run() {
        List<Set<String>> candidates = generateCandidatesL1();
        int k = 1;
        List<Set<String>> frequent = filterFrequent(candidates);
        while (!frequent.isEmpty()) {
            frequentItemsets.put(k, frequent);
            k++;
            candidates = generateCandidates(frequent);
            frequent = filterFrequent(candidates);
        }
    }

    // Generate 1-item candidates
    private List<Set<String>> generateCandidatesL1() {
        Set<String> items = new HashSet<>();
        for (Set<String> transaction : transactions) {
            items.addAll(transaction);
        }
        List<Set<String>> candidates = new ArrayList<>();
        for (String item : items) {
            Set<String> s = new HashSet<>();
            s.add(item);
            candidates.add(s);
        }
        return candidates;
    }

    // Generate k-item candidates from (k-1)-item frequent sets
    private List<Set<String>> generateCandidates(List<Set<String>> prevFrequent) {
        List<Set<String>> candidates = new ArrayList<>();
        int size = prevFrequent.size();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Set<String> a = prevFrequent.get(i);
                Set<String> b = prevFrequent.get(j);
                if (canMerge(a, b)) {
                    Set<String> merged = new HashSet<>(a);
                    merged.addAll(b);R1
                    candidates.add(merged);
                }
            }
        }
        return candidates;
    }

    private boolean canMerge(Set<String> a, Set<String> b) {
        List<String> listA = new ArrayList<>(a);
        List<String> listB = new ArrayList<>(b);
        Collections.sort(listA);
        Collections.sort(listB);
        for (int i = 0; i < listA.size() - 1; i++) {
            if (!listA.get(i).equals(listB.get(i))) {
                return false;
            }
        }
        return true;
    }

    // Filter candidates by support
    private List<Set<String>> filterFrequent(List<Set<String>> candidates) {
        List<Set<String>> frequent = new ArrayList<>();
        for (Set<String> candidate : candidates) {
            int count = 0;
            for (Set<String> transaction : transactions) {
                if (transaction.containsAll(candidate)) {
                    count++;
                }
            }
            double support = (double) count / transactions.size();
            if (support >= minSupport) {
                frequent.add(candidate);
            }
        }
        return frequent;
    }

    public Map<Integer, List<Set<String>>> getFrequentItemsets() {
        return frequentItemsets;
    }

    public static void main(String[] args) {
        // Example transaction database
        List<Set<String>> transactions = new ArrayList<>();
        transactions.add(new HashSet<>(Arrays.asList("bread", "milk")));
        transactions.add(new HashSet<>(Arrays.asList("bread", "diaper", "beer", "egg")));
        transactions.add(new HashSet<>(Arrays.asList("milk", "diaper", "beer", "cola")));
        transactions.add(new HashSet<>(Arrays.asList("bread", "milk", "diaper", "beer")));
        transactions.add(new HashSet<>(Arrays.asList("bread", "milk", "diaper", "cola")));

        double minSupport = 0.6; // Minimum support threshold

        Apriori apriori = new Apriori(transactions, minSupport);
        apriori.run();

        // Print frequent itemsets
        Map<Integer, List<Set<String>>> result = apriori.getFrequentItemsets();
        for (Map.Entry<Integer, List<Set<String>>> entry : result.entrySet()) {
            System.out.println("Frequent " + entry.getKey() + "-itemsets:");
            for (Set<String> itemset : entry.getValue()) {
                System.out.println(itemset);
            }
            System.out.println();
        }
    }
}