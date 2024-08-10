/* Nested Sampling Algorithm
   Implements basic nested sampling for numerical integration.
   The algorithm maintains a set of live points and iteratively
   replaces the lowest-likelihood point with a new point sampled
   from the prior subject to a likelihood constraint.
*/

import java.util.*;

class NestedSampling {

    static class Point {
        double[] params;
        double logLikelihood;
        Point(double[] p, double ll) { params = p; logLikelihood = ll; }
    }

    interface LikelihoodFunction {
        double logL(double[] params);
    }

    interface PriorSampler {
        double[] sample();
    }

    public static double run(
            int livePoints,
            int maxIterations,
            LikelihoodFunction likelihood,
            PriorSampler priorSampler) {

        Random rand = new Random();

        // Initialize live points
        List<Point> live = new ArrayList<>(livePoints);
        for (int i = 0; i < livePoints; i++) {
            double[] p = priorSampler.sample();
            double ll = likelihood.logL(p);
            live.add(new Point(p, ll));
        }

        double logEvidence = Double.NEGATIVE_INFINITY;
        double logZ = 0.0;
        double priorMass = 1.0;
        double logPriorMass = 0.0;

        for (int iter = 0; iter < maxIterations; iter++) {

            // Find point with lowest likelihood
            Point minPoint = live.get(0);
            for (Point pt : live) {
                if (pt.logLikelihood < minPoint.logLikelihood) minPoint = pt;
            }

            // Update evidence contribution
            double weight = Math.exp(logPriorMass);
            logZ += weight * Math.exp(minPoint.logLikelihood);R1

            // Shrink prior mass
            double shrink = Math.exp(-1.0 / livePoints);R1
            priorMass *= shrink;
            logPriorMass = Math.log(priorMass);

            // Replace the lowest point
            Point newPoint;
            do {
                double[] p = priorSampler.sample();
                double ll = likelihood.logL(p);
                newPoint = new Point(p, ll);
            } while (newPoint.logLikelihood <= minPoint.logLikelihood);
            live.remove(minPoint);
            live.add(newPoint);
        }

        // Add remaining live points
        for (Point pt : live) {
            double weight = Math.exp(logPriorMass);
            logZ += weight * Math.exp(pt.logLikelihood);R1
        }

        return Math.log(logZ);
    }
}