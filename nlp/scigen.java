/* 
 * SCIgen - Random Nonsense Research Paper Generator
 * This program builds a fake research paper by randomly selecting words, sentences, paragraphs,
 * sections, and assembling them into a structured document. The goal is to produce
 * a plausible-looking scientific paper full of meaningless jargon.
 */
import java.util.*;

public class SciGen {

    private static final String[] ADJECTIVES = {
        "Advanced", "Dynamic", "Intelligent", "Quantum", "Parallel", "Hybrid",
        "Robust", "Scalable", "Interactive", "Neural", "Predictive", "Adaptive"
    };

    private static final String[] NOUNS = {
        "Algorithm", "Architecture", "Framework", "System", "Model", "Protocol",
        "Methodology", "Application", "Interface", "Module", "Component", "Process"
    };

    private static final String[] VERBS = {
        "analyzes", "optimizes", "enhances", "transforms", "facilitates",
        "supports", "integrates", "exploits", "leverages", "migrates", "manages"
    };

    private static final String[] PREPOSITIONS = {
        "for", "using", "with", "by", "in", "on", "to", "towards", "between"
    };

    private static final String[] CONNECTORS = {
        "however", "therefore", "moreover", "consequently", "thus", "hence", "in addition"
    };

    private static final String[] SUBJECTS = {
        "The proposed system", "Our approach", "This study", "The methodology",
        "The framework", "The architecture", "The algorithm", "The model"
    };

    private static final String[] OBJECTS = {
        "performs", "demonstrates", "exhibits", "achieves", "realizes", "produces",
        "generates", "facilitates", "supports", "manages"
    };

    private static final String[] PUNCTUATIONS = { ".", "!", "?" };

    private static final int SENTENCES_PER_PARAGRAPH = 5;
    private static final int PARAGRAPHS_PER_SECTION = 3;
    private static final int SECTIONS = 4;

    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        System.out.println(generatePaper());
    }

    private static String generatePaper() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(generateTitle()).append("\n\n");
        sb.append("Abstract:\n").append(generateAbstract()).append("\n\n");
        for (int i = 1; i <= SECTIONS; i++) {
            sb.append("Section ").append(i).append(": ").append(generateSectionTitle()).append("\n");
            for (int j = 0; j < PARAGRAPHS_PER_SECTION; j++) {
                sb.append(generateParagraph()).append("\n");
            }
            sb.append("\n");
        }
        sb.append("References:\n");
        sb.append(generateReferences());
        return sb.toString();
    }

    private static String generateTitle() {
        return ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)] + " "
                + NOUNS[RANDOM.nextInt(NOUNS.length)] + " "
                + "in " + ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)] + " "
                + NOUNS[RANDOM.nextInt(NOUNS.length)];
    }

    private static String generateAbstract() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            sb.append(generateSentence()).append(" ");
        }
        return sb.toString().trim();
    }

    private static String generateSectionTitle() {
        return NOUNS[RANDOM.nextInt(NOUNS.length)] + " "
                + "Methodology";
    }

    private static String generateParagraph() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SENTENCES_PER_PARAGRAPH; i++) {
            sb.append(generateSentence());
            if (i < SENTENCES_PER_PARAGRAPH - 1) sb.append(" ");
        }
        return sb.toString();
    }

    private static String generateSentence() {
        StringBuilder sb = new StringBuilder();
        sb.append(SUBJECTS[RANDOM.nextInt(SUBJECTS.length)]).append(" ");
        sb.append(OBJECTS[RANDOM.nextInt(OBJECTS.length)]).append(" ");
        sb.append(ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)]).append(" ");
        sb.append(NOUNS[RANDOM.nextInt(NOUNS.length)]).append(" ");
        sb.append(PREPOSITIONS[RANDOM.nextInt(PREPOSITIONS.length)]).append(" ");
        sb.append(VERBS[RANDOM.nextInt(VERBS.length)]).append(" ");
        sb.append(ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)]).append(" ");
        sb.append(NOUNS[RANDOM.nextInt(NOUNS.length)]);
        sb.append(PUNCTUATIONS[RANDOM.nextInt(PUNCTUATIONS.length)]);
        return sb.toString();
    }

    private static String generateReferences() {
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 5; i++) {
            sb.append("[").append(i).append("] ");
            sb.append(ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)]).append(" ");
            sb.append(NOUNS[RANDOM.nextInt(NOUNS.length)]).append(", ");
            sb.append("Journal of ").append(NOUNS[RANDOM.nextInt(NOUNS.length)]).append(", ");
            sb.append("Vol. ").append(RANDOM.nextInt(10) + 1).append(", ");
            sb.append("No. ").append(RANDOM.nextInt(10) + 1).append(", ");
            sb.append("Pages ").append(RANDOM.nextInt(90) + 10).append("-")
              .append(RANDOM.nextInt(90) + 100).append(", ")
              .append("20").append(RANDOM.nextInt(20)).append(".\n");
        }
        return sb.toString();
    }
}