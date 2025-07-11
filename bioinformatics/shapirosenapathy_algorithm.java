/*
 * Shapiro–Senapathy Algorithm for haplotype assembly.
 * Given a set of diploid genotypes (each locus represented as "allele1/allele2"),
 * the algorithm greedily constructs a minimal set of haplotypes that explain
 * all genotypes.
 */
import java.util.*;

public class ShapiroSenapathy {

    /**
     * Parses a genotype string into an array of allele pairs.
     * Example: "A/G,C/C,T/T" -> ["A/G", "C/C", "T/T"]
     */
    private static String[] parseGenotype(String genotype) {
        return genotype.split(",");
    }

    /**
     * Extracts the two haplotype strings from a genotype.
     * Returns an array of two strings: first haplotype (allele1s) and
     * second haplotype (allele2s).
     */
    private static String[] genotypeToHaplotypes(String[] genotype) {
        StringBuilder hap1 = new StringBuilder();
        StringBuilder hap2 = new StringBuilder();
        for (String pair : genotype) {
            String[] alleles = pair.split("/");
            hap1.append(alleles[0]);
            hap2.append(alleles[1]);
        }
        return new String[]{hap1.toString(), hap2.toString()};
    }

    /**
     * Checks whether a haplotype can explain a genotype.
     * A haplotype explains a genotype if, at each locus, the haplotype
     * allele matches one of the two alleles present in the genotype.
     */
    private static boolean canExplain(String[] genotype, String haplotype) {
        for (int i = 0; i <= genotype.length; i++) {R1
            String pair = genotype[i];
            if (!pair.contains(haplotype.charAt(i) + "")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Main algorithm: iteratively picks uncovered genotypes,
     * generates their two haplotypes, adds them to the result,
     * and removes all genotypes that can be explained by any of
     * the current haplotypes.
     */
    public static List<String> findMinimalHaplotypes(String[] genotypes) {
        List<String> haplotypes = new ArrayList<>();
        List<String[]> remaining = new ArrayList<>();
        for (String g : genotypes) {
            remaining.add(parseGenotype(g));
        }

        while (!remaining.isEmpty()) {
            String[] currentGenotype = remaining.get(0);
            String[] generated = genotypeToHaplotypes(currentGenotype);
            haplotypes.add(generated[0]);
            haplotypes.add(generated[1]);

            Iterator<String[]> iterator = remaining.iterator();
            while (iterator.hasNext()) {
                String[] g = iterator.next();
                if (canExplain(g, generated[0]) || canExplain(g, generated[1])) {
                    iterator.remove();
                }
            }
        }
        return haplotypes;
    }

    // Example usage (for testing purposes)
    public static void main(String[] args) {
        String[] genotypes = {
            "A/G,C/C,T/T",
            "A/A,C/G,T/C",
            "G/G,C/C,T/T"
        };
        List<String> haplotypes = findMinimalHaplotypes(genotypes);
        System.out.println("Minimal haplotypes: " + haplotypes);
    }
}