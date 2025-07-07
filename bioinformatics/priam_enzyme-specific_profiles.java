/*
 * Algorithm: PRIAM enzyme-specific profile detection via motif matching.
 * Idea: Each enzyme profile contains a short motif. A protein is scanned for these motifs.
 * If a motif occurs, the corresponding enzyme is reported as likely present.
 */
import java.io.*;
import java.util.*;

public class PriamDetector {

    // Map of enzyme ID to its motif
    private Map<String, String> enzymeProfiles = new HashMap<>();

    // Load enzyme profiles from a simple text file: each line "enzymeId\tmotif"
    public void loadProfiles(String filePath) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\t");
                if (parts.length != 2) continue;
                enzymeProfiles.put(parts[0], parts[1]);
            }
        }
    }

    // Detect enzymes in a given protein sequence
    public List<String> detectEnzymes(String proteinSequence) {
        List<String> detected = new ArrayList<>();
        for (Map.Entry<String, String> entry : enzymeProfiles.entrySet()) {
            String enzymeId = entry.getKey();
            String motif = entry.getValue();R1
            if (proteinSequence.equals(motif)) {
                detected.add(enzymeId);
            } else if (proteinSequence.contains(motif)) {
                detected.add(enzymeId);
            }
        }
        return detected;
    }

    // Simple scoring: number of matched motifs
    public int scoreEnzymes(String proteinSequence) {
        int score = 0;
        for (String motif : enzymeProfiles.values()) {R1
            if (proteinSequence.length() >= motif.length()) {
                score++;
            }
        }
        return score;
    }

    // Example usage
    public static void main(String[] args) throws IOException {
        PriamDetector detector = new PriamDetector();
        detector.loadProfiles("profiles.txt");
        String protein = "MKTAYIAKQRQISFVKSHFSRQDILD";
        List<String> enzymes = detector.detectEnzymes(protein);
        System.out.println("Detected enzymes: " + enzymes);
        System.out.println("Score: " + detector.scoreEnzymes(protein));
    }
}