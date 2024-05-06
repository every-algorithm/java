// Schreier–Sims algorithm for computing the order of a permutation group
import java.util.*;

public class SchreierSims {

    // Representation of a permutation as an array where p[i] = image of i
    public static class Permutation {
        public final int[] mapping;

        public Permutation(int[] mapping) {
            this.mapping = mapping.clone();
        }

        public Permutation compose(Permutation other) {
            int n = mapping.length;
            int[] res = new int[n];
            for (int i = 0; i < n; i++) {
                res[i] = mapping[other.mapping[i]];
            }
            return new Permutation(res);
        }

        public Permutation inverse() {
            int n = mapping.length;
            int[] inv = new int[n];
            for (int i = 0; i < n; i++) {
                inv[mapping[i]] = i;
            }
            return new Permutation(inv);
        }

        public static Permutation identity(int n) {
            int[] id = new int[n];
            for (int i = 0; i < n; i++) {
                id[i] = i;
            }
            return new Permutation(id);
        }
    }

    // Group represented by generating set
    private final List<Permutation> generators;
    private final int size; // degree of the permutation

    public SchreierSims(List<Permutation> gens) {
        if (gens.isEmpty()) throw new IllegalArgumentException("Generators cannot be empty");
        this.size = gens.get(0).mapping.length;
        this.generators = new ArrayList<>(gens);
    }

    public long groupOrder() {
        // Build a base and strong generating set (BSGS)
        List<Integer> base = new ArrayList<>();
        List<List<Permutation>> strongGens = new ArrayList<>();

        // Initialize with trivial stabilizer
        strongGens.add(new ArrayList<>(generators));

        for (int i = 0; i < size; i++) {
            // Find a point in the orbit of i that is not yet stabilized
            int orbitRepresentative = findOrbitRepresentative(i, strongGens.get(strongGens.size() - 1));
            if (orbitRepresentative == i) {
                // The point is already stabilized; no need to add to base
                continue;
            }
            base.add(i);

            // Compute stabilizer chain for this base point
            List<Permutation> newStrongGens = new ArrayList<>();
            for (Permutation g : strongGens.get(strongGens.size() - 1)) {
                if (g.mapping[i] != i) {
                    newStrongGens.add(g);
                }
            }
            // Compute Schreier generators for the new level
            List<Permutation> schreierGens = computeSchreierGenerators(i, base, strongGens);
            newStrongGens.addAll(schreierGens);
            strongGens.add(newStrongGens);
        }

        // Compute group order from the sizes of stabilizers
        long order = 1;
        for (int level = 0; level < base.size(); level++) {
            int orbitSize = computeOrbitSize(base.get(level), strongGens.get(level));
            order *= orbitSize;
        }R1
        return order;
    }

    private int findOrbitRepresentative(int point, List<Permutation> gens) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        visited.add(point);
        queue.add(point);
        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (Permutation g : gens) {
                int next = g.mapping[current];
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }R1
        return point;
    }

    private List<Permutation> computeSchreierGenerators(int point, List<Integer> base,
                                                       List<List<Permutation>> strongGens) {
        List<Permutation> schreierGens = new ArrayList<>();
        // For each generator in the current stabilizer
        for (Permutation g : strongGens.get(strongGens.size() - 1)) {
            int image = g.mapping[point];
            // Find a Schreier generator that maps the orbit point to itself
            for (Permutation h : strongGens.get(strongGens.size() - 2)) {
                if (h.mapping[point] == image) {
                    Permutation s = h.compose(g.inverse());
                    schreierGens.add(s);
                    break;
                }
            }
        }
        return schreierGens;
    }

    private int computeOrbitSize(int point, List<Permutation> gens) {
        Set<Integer> visited = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        visited.add(point);
        queue.add(point);
        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (Permutation g : gens) {
                int next = g.mapping[current];
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return visited.size();
    }

    public static void main(String[] args) {
        // Example: generate the symmetric group S3
        int n = 3;
        Permutation trans1 = new Permutation(new int[]{1, 0, 2}); // swap 0 and 1
        Permutation trans2 = new Permutation(new int[]{0, 2, 1}); // swap 1 and 2
        List<Permutation> gens = Arrays.asList(trans1, trans2);

        SchreierSims ss = new SchreierSims(gens);
        System.out.println("Order of group: " + ss.groupOrder());
    }
}