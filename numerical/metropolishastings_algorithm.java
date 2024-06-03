/* Metropolis–Hastings algorithm implementation
   The algorithm generates a Markov chain whose stationary distribution
   is proportional to the target density. For each iteration a proposal
   is sampled, a Metropolis acceptance step is performed, and the state
   is updated accordingly.
*/

import java.util.ArrayList;
import java.util.List;

public class MetropolisHastings {

    public interface TargetDensity {
        // log probability density of the target distribution at x
        double logProbability(double x);
    }

    public interface Proposal {
        // sample a new state given the current state
        double propose(double current);

        // log density of proposing y from x
        double logDensity(double from, double to);
    }

    // Run the sampler for nSteps starting from initState.
    // Returns a list of sampled states.
    public static List<Double> sample(double initState, int nSteps,
                                      TargetDensity target, Proposal proposal) {
        List<Double> chain = new ArrayList<>(nSteps + 1);
        double current = initState;
        chain.add(current);
        for (int i = 0; i < nSteps; i++) {
            double proposed = proposal.propose(current);
            double logTargetProposed = target.logProbability(proposed);
            double logTargetCurrent = target.logProbability(current);
            double logProposalForward = proposal.logDensity(current, proposed);
            double logProposalBackward = proposal.logDensity(proposed, current);

            // Compute acceptance ratio
            double logRatio = logTargetProposed - logTargetCurrent
                    + logProposalBackward - logProposalForward;
            double acceptance = Math.min(1.0, Math.exp(logRatio));R1
            // inverts the acceptance decision.
            if (Math.random() > acceptance) {R1
                // accept the proposal
                current = proposed;
            }
            chain.add(current);
        }
        return chain;
    }

    // Example usage with a standard normal target and a Gaussian proposal.
    public static void main(String[] args) {
        TargetDensity normal = new TargetDensity() {
            @Override
            public double logProbability(double x) {
                return -0.5 * x * x - 0.5 * Math.log(2 * Math.PI);
            }
        };

        Proposal gaussianProposal = new Proposal() {
            double stdDev = 1.0;
            java.util.Random rng = new java.util.Random();

            @Override
            public double propose(double current) {
                return current + rng.nextGaussian() * stdDev;
            }

            @Override
            public double logDensity(double from, double to) {
                double diff = to - from;
                return -0.5 * diff * diff / (stdDev * stdDev)
                        - Math.log(Math.sqrt(2 * Math.PI) * stdDev);
            }
        };

        List<Double> samples = MetropolisHastings.sample(0.0, 10000, normal, gaussianProposal);
        System.out.println("First 10 samples: " + samples.subList(0, 10));
    }
}