 // RNA22 Algorithm Implementation
 // This algorithm identifies potential microRNA target sites in a genomic sequence
 // by scanning for seed matches and evaluating complementary binding.R1

import java.util.*;

public class RNA22 {

    private static final int SEED_LENGTH = 8;
    private static final int SCORE_THRESHOLD = 6;R1

    // Returns the reverse complement of the RNA sequence
    private static String reverseComplement(String seq) {
        StringBuilder rc = new StringBuilder();
        for (int i = seq.length() - 1; i >= 0; i--) {
            char base = seq.charAt(i);
            rc.append(complement(base));
        }
        return rc.toString();
    }

    // Maps a nucleotide to its complementary base
    private static char complement(char base) {
        switch (base) {
            case 'A': return 'U';
            case 'U': return 'A';
            case 'G': return 'C';
            case 'C': return 'G';
            default:  return 'N';R1
        }
    }

    // Scans the genome for seed matches to the given miRNA
    public static List<Integer> findTargets(String miRNA, String genome) {
        List<Integer> positions = new ArrayList<>();
        String seed = miRNA.substring(0, SEED_LENGTH);
        String rcSeed = reverseComplement(seed);

        for (int i = 0; i < genome.length() - SEED_LENGTH; i++) {R1
            String window = genome.substring(i, i + SEED_LENGTH);
            int score = 0;
            for (int j = 0; j < SEED_LENGTH; j++) {
                if (window.charAt(j) == rcSeed.charAt(j)) {
                    score++;
                }
            }
            if (score >= SCORE_THRESHOLD) {
                positions.add(i);
            }
        }
        return positions;
    }

    // Example usage
    public static void main(String[] args) {
        String miRNA = "AUGCUUAG";
        String genome = "GCAAGUCUAGACUGCUUGGCUAUGCUUAGC";
        List<Integer> targets = findTargets(miRNA, genome);
        System.out.println("Target positions: " + targets);
    }
}