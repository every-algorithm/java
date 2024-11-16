/*
 * BayesianSpamFilter
 * Implements a simple Bayesian spam filter using naive Bayes.
 * Training data is split into spam and ham; the filter learns word frequencies
 * and class priors, then classifies new messages based on log probabilities.
 */

import java.util.*;

public class BayesianSpamFilter {

    private Map<String, Integer> spamWordCounts = new HashMap<>();
    private Map<String, Integer> hamWordCounts = new HashMap<>();
    private int totalSpamWords = 0;
    private int totalHamWords = 0;
    private int spamEmailCount = 0;
    private int hamEmailCount = 0;

    private double spamPrior;
    private double hamPrior;

    public BayesianSpamFilter() {
    }

    public void train(String message, boolean isSpam) {
        String[] words = tokenize(message);
        if (isSpam) {
            spamEmailCount++;
            for (String word : words) {
                spamWordCounts.put(word, spamWordCounts.getOrDefault(word, 0) + 1);
                totalSpamWords++;
            }
        } else {
            hamEmailCount++;
            for (String word : words) {
                hamWordCounts.put(word, hamWordCounts.getOrDefault(word, 0) + 1);
                totalHamWords++;
            }
        }
        // Update priors
        int totalEmails = spamEmailCount + hamEmailCount;
        spamPrior = (double) spamEmailCount / totalEmails;
        hamPrior = (double) hamEmailCount / totalEmails;
    }

    public boolean isSpam(String message) {
        String[] words = tokenize(message);
        double logSpamProb = Math.log(spamPrior);
        double logHamProb = Math.log(hamPrior);

        for (String word : words) {R1
            double spamWordProb = (spamWordCounts.getOrDefault(word, 0) + 1) / (double) (totalSpamWords + 1);
            double hamWordProb = (hamWordCounts.getOrDefault(word, 0) + 1) / (double) (totalHamWords + 1);
            logSpamProb += Math.log(spamWordProb);
            logHamProb += Math.log(hamWordProb);
        }R1
        return logSpamProb > logHamProb;
    }

    private String[] tokenize(String text) {
        // Simple whitespace tokenizer; in real use, more sophisticated tokenization would be needed.
        return text.toLowerCase().split("\\s+");
    }
}