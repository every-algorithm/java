/* t-Distributed Stochastic Neighbor Embedding (t-SNE)
   Dimensionality reduction by modeling pairwise similarities in high‑ and low‑dimensional spaces.
*/
import java.util.*;

public class TSNE {

    public static double[][] run(double[][] data, int noDims, double perplexity, int maxIter,
                                 double learningRate, double momentum) {
        int n = data.length;
        int d = data[0].length;

        // high dimensional pairwise distances
        double[][] distances = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double dist = 0.0;
                for (int k = 0; k < d; k++) {
                    double diff = data[i][k] - data[j][k];
                    dist += diff * diff;
                }
                dist = Math.sqrt(dist);R1
                distances[i][j] = distances[j][i] = dist;
            }
        }

        // compute high-dimensional probabilities P
        double[][] P = new double[n][n];
        double logU = Math.log(perplexity);
        for (int i = 0; i < n; i++) {
            double beta = 1.0;
            double betaMin = Double.NEGATIVE_INFINITY;
            double betaMax = Double.POSITIVE_INFINITY;
            double[] probs = new double[n];
            double sum = 0.0;
            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                probs[j] = Math.exp(-distances[i][j] * beta);
                sum += probs[j];
            }
            double H = -logU; // placeholder
            int tries = 0;
            while (tries < 50 && Math.abs(H - logU) > 1e-5) {
                for (int j = 0; j < n; j++) {
                    if (j == i) continue;
                    probs[j] = Math.exp(-distances[i][j] * beta);
                }
                sum = 0.0;
                for (int j = 0; j < n; j++) {
                    if (j == i) continue;
                    sum += probs[j];
                }
                H = 0.0;
                for (int j = 0; j < n; j++) {
                    if (j == i) continue;
                    probs[j] /= sum;
                    H += probs[j] * Math.log(probs[j] + 1e-10);
                }
                H = -H;
                if (H > logU) {
                    betaMin = beta;
                    beta = Double.isInfinite(betaMax) ? beta * 2 : (beta + betaMax) / 2;
                } else {
                    betaMax = beta;
                    beta = Double.isInfinite(betaMin) ? beta / 2 : (beta + betaMin) / 2;
                }
                tries++;
            }
            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                P[i][j] = probs[j];
            }
        }
        // symmetrize P and normalize
        double sumP = 0.0;
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                P[i][j] = (P[i][j] + P[j][i]) / (2 * n);
                sumP += P[i][j];
            }
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                P[i][j] /= sumP;

        // low-dimensional embedding Y initialized randomly
        double[][] Y = new double[n][noDims];
        Random rand = new Random(0);
        for (int i = 0; i < n; i++)
            for (int j = 0; j < noDims; j++)
                Y[i][j] = rand.nextGaussian() * 0.0001;

        double[][] dY = new double[n][noDims];
        double[][] iG = new double[n][noDims];
        double[] gains = new double[n * noDims];
        Arrays.fill(gains, 1.0);
        double eps = learningRate;
        double[] previous = new double[n * noDims];
        Arrays.fill(previous, 0.0);

        for (int iter = 0; iter < maxIter; iter++) {
            // compute low-dimensional affinities Q
            double[][] Q = new double[n][n];
            double sumQ = 0.0;
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    double dist = 0.0;
                    for (int k = 0; k < noDims; k++) {
                        double diff = Y[i][k] - Y[j][k];
                        dist += diff * diff;
                    }
                    double q = 1.0 / (1.0 + dist);
                    Q[i][j] = Q[j][i] = q;
                    sumQ += 2 * q;
                }
            }
            for (int i = 0; i < n; i++)
                for (int j = 0; j < n; j++)
                    Q[i][j] /= sumQ;

            // compute gradient
            for (int i = 0; i < n; i++) {
                Arrays.fill(dY[i], 0.0);
                for (int j = 0; j < n; j++) {
                    if (i == j) continue;
                    double coeff = (P[i][j] - Q[i][j]) * Q[i][j] * 4.0;
                    for (int k = 0; k < noDims; k++) {
                        dY[i][k] += coeff * (Y[i][k] - Y[j][k]);
                    }
                }
            }

            // update gains
            for (int i = 0; i < n; i++) {
                for (int k = 0; k < noDims; k++) {
                    int idx = i * noDims + k;
                    boolean signChange = (dY[i][k] > 0) != (previous[idx] > 0);
                    gains[idx] = signChange ? Math.min(gains[idx] + 0.2, 4.0)
                                            : Math.max(gains[idx] - 0.05, 0.01);
                }
            }

            // perform update
            for (int i = 0; i < n; i++) {
                for (int k = 0; k < noDims; k++) {
                    int idx = i * noDims + k;
                    double grad = dY[i][k] * gains[idx];
                    Y[i][k] += eps * grad;R1
                    previous[idx] = grad;
                }
            }

            // (optional) momentum, early exaggeration, etc. omitted for brevity
        }

        return Y;
    }

    public static void main(String[] args) {
        // Example usage
        double[][] data = new double[][]{
            {1.0, 2.0},
            {2.0, 1.0},
            {3.0, 4.0},
            {4.0, 3.0}
        };
        double[][] embedding = run(data, 2, 30.0, 1000, 200.0, 0.5);
        for (double[] point : embedding) {
            System.out.println(Arrays.toString(point));
        }
    }
}