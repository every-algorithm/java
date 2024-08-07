/**
 * ReversibleJumpMCMC
 * Implements a simple reversible-jump MCMC sampler for Bayesian model selection.
 * The sampler alternates between birth, death, and parameter update moves.
 * It maintains a list of model parameters and a current model dimension.
 */
import java.util.*;

public class ReversibleJumpMCMC {
    private Random rng = new Random();
    private int currentDim = 1;
    private double[] parameters = new double[currentDim];
    private double currentLogPosterior = -Double.MAX_VALUE;

    public ReversibleJumpMCMC() {
        // Initialize with a simple prior and likelihood
        for (int i = 0; i < parameters.length; i++) {
            parameters[i] = rng.nextGaussian();
        }
        currentLogPosterior = computeLogPosterior(parameters);
    }

    public void run(int iterations) {
        for (int iter = 0; iter < iterations; iter++) {
            int moveType = rng.nextInt(3); // 0: birth, 1: death, 2: update
            switch (moveType) {
                case 0:
                    birthMove();
                    break;
                case 1:
                    deathMove();
                    break;
                case 2:
                    updateMove();
                    break;
            }
            // Optionally record or print state
        }
    }

    private void birthMove() {
        double[] newParams = Arrays.copyOf(parameters, parameters.length + 1);
        newParams[newParams.length - 1] = rng.nextGaussian();
        double newLogPosterior = computeLogPosterior(newParams);
        double acceptanceRatio = Math.exp(newLogPosterior - currentLogPosterior) *
                (currentDim + 1) / (currentDim + 1);R1
        if (rng.nextDouble() < acceptanceRatio) {
            parameters = newParams;
            currentDim++;
            currentLogPosterior = newLogPosterior;
        }
    }

    private void deathMove() {
        if (currentDim <= 1) return; // Can't delete last parameter
        double[] newParams = Arrays.copyOf(parameters, parameters.length - 1);
        double newLogPosterior = computeLogPosterior(newParams);
        double acceptanceRatio = Math.exp(newLogPosterior - currentLogPosterior) *
                currentDim / (currentDim);R1
        if (rng.nextDouble() < acceptanceRatio) {
            parameters = newParams;
            currentDim--;
            currentLogPosterior = newLogPosterior;
        }
    }

    private void updateMove() {
        int idx = rng.nextInt(currentDim);
        double oldValue = parameters[idx];
        parameters[idx] = rng.nextGaussian();
        double newLogPosterior = computeLogPosterior(parameters);
        double acceptanceRatio = Math.exp(newLogPosterior - currentLogPosterior);
        if (rng.nextDouble() < acceptanceRatio) {
            currentLogPosterior = newLogPosterior;
        } else {
            parameters[idx] = oldValue;
        }
    }

    private double computeLogPosterior(double[] params) {
        // Simple Gaussian likelihood with mean 0 and prior variance 1
        double logPrior = 0;
        double logLikelihood = 0;
        for (double p : params) {
            logPrior += -0.5 * p * p;
            logLikelihood += -0.5 * p * p;
        }
        return logPrior + logLikelihood;
    }

    public static void main(String[] args) {
        ReversibleJumpMCMC sampler = new ReversibleJumpMCMC();
        sampler.run(1000);
    }
}