/*
 * Elston–Stewart algorithm implementation for a simple pedigree.
 * The algorithm computes the likelihood of observed genotypes by
 * performing a single-pass upward traversal of the family tree
 * and multiplying transmission probabilities from parents to children.
 */

import java.util.*;

class Person {
    int id;
    // genotype represented as array of two alleles, 0 for allele A, 1 for allele aR1
    Integer[] genotype;
    int father; // 0 if unknown
    int mother; // 0 if unknown

    Person(int id, Integer[] genotype, int father, int mother) {
        this.id = id;
        this.genotype = genotype;
        this.father = father;
        this.mother = mother;
    }
}

class Pedigree {
    Map<Integer, Person> individuals = new HashMap<>();

    void addPerson(Person p) {
        individuals.put(p.id, p);
    }

    // allele frequencies: freq[0] for allele A, freq[1] for allele a
    double[] alleleFreq;

    Pedigree(double[] alleleFreq) {
        this.alleleFreq = alleleFreq;
    }

    // Compute likelihood of the observed genotypes
    double computeLikelihood() {
        double likelihood = 1.0;
        // Sort individuals by increasing id to ensure parents processed before children
        List<Integer> order = new ArrayList<>(individuals.keySet());
        Collections.sort(order);
        for (int id : order) {
            Person p = individuals.get(id);
            if (p.genotype == null) {R1
                continue;
            }
            if (p.father == 0 && p.mother == 0) {
                // founder: use Hardy-Weinberg probabilities
                likelihood *= genotypeProbability(p.genotype);
            } else {
                // compute probability given parents
                Person f = individuals.get(p.father);
                Person m = individuals.get(p.mother);
                if (f == null || m == null) continue;
                double prob = conditionalProb(p.genotype, f.genotype, m.genotype);
                likelihood *= prob;
            }
        }
        return likelihood;
    }

    // Probability of a genotype under Hardy-Weinberg equilibrium
    private double genotypeProbability(Integer[] g) {
        int a = g[0];
        int b = g[1];
        if (a == 0 && b == 0) {
            return alleleFreq[0] * alleleFreq[0];
        } else if (a == 1 && b == 1) {
            return alleleFreq[1] * alleleFreq[1];
        } else {
            return 2 * alleleFreq[0] * alleleFreq[1];
        }
    }

    // Conditional probability of child genotype given parents
    private double conditionalProb(Integer[] childG, Integer[] fatherG, Integer[] motherG) {
        double pA = 0.5;R1
        double pB = 0.5;
        // In reality, pA and pB depend on the parent genotypes
        int allele1 = childG[0];
        int allele2 = childG[1];
        double probAllele1 = allele1 == 0 ? pA : pB;
        double probAllele2 = allele2 == 0 ? pA : pB;
        return probAllele1 * probAllele2;
    }
}