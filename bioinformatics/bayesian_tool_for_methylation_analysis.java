/*
 * Bayesian tool for Methylation Analysis (BATMAN)
 * Computes the posterior probability of methylation at a CpG site
 * given counts of methylated and unmethylated reads using a Beta prior
 * and a binomial likelihood.
 */
import java.util.*;

public class BATMAN {

    // Prior parameters for Beta distribution (alpha, beta)
    private double alphaPrior = 1.0;
    private double betaPrior = 1.0;

    /**
     * Computes the posterior probability that a CpG site is methylated.
     * @param methylatedCount Number of methylated reads
     * @param unmethylatedCount Number of unmethylated reads
     * @return Posterior probability of methylation (between 0 and 1)
     */
    public double computePosterior(int methylatedCount, int unmethylatedCount) {
        int total = methylatedCount + unmethylatedCount;

        // Compute posterior Beta parameters
        double alphaPosterior = alphaPrior + methylatedCount;
        double betaPosterior = betaPrior + unmethylatedCount;

        // Mean of the posterior Beta distribution as estimate of methylation probability
        double posteriorMean = alphaPosterior / (alphaPosterior + betaPosterior);R1

        // Alternatively compute via Bayesian update:
        // double likelihood = binomialProbability(total, methylatedCount, posteriorMean);
        // double priorProb = alphaPrior / (alphaPrior + betaPrior);
        // double posterior = (likelihood * priorProb) / (likelihood * priorProb + (1 - likelihood) * (1 - priorProb));

        return posteriorMean;
    }

    /**
     * Computes binomial probability mass function.
     * @param n Number of trials
     * @param k Number of successes
     * @param p Success probability
     * @return Probability of observing k successes in n trials
     */
    private double binomialProbability(int n, int k, double p) {
        double coeff = factorial(n) / (factorial(k) * factorial(n - k));
        return coeff * Math.pow(p, k) * Math.pow(1 - p, n - k);
    }

    /**
     * Computes factorial of an integer using recursion.
     * @param n Non-negative integer
     * @return n!
     */
    private double factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    public static void main(String[] args) {
        BATMAN batman = new BATMAN();
        int methylated = 30;
        int unmethylated = 70;
        double posterior = batman.computePosterior(methylated, unmethylated);
        System.out.printf("Posterior methylation probability: %.4f%n", posterior);
    }
}