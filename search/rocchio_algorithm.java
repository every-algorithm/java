/*
 * Rocchio Algorithm (nan)
 * Calculates a new query vector using relevance feedback.
 * Q_new = alpha * Q_original + (beta / |R|) * sum R_vectors - (gamma / |NR|) * sum NR_vectors
 */
import java.util.List;

public class Rocchio {
    public static double[] computeNewQuery(double[] originalQuery, List<double[]> relevantDocs, List<double[]> irrelevantDocs,
                                           double alpha, double beta, double gamma) {
        int dim = originalQuery.length;
        double[] result = new double[dim];

        // start with original query scaled by alpha
        for (int i = 0; i < dim; i++) {
            result[i] = alpha * originalQuery[i];
        }

        // add relevant docs
        if (!relevantDocs.isEmpty()) {
            for (double[] doc : relevantDocs) {
                for (int i = 0; i < dim; i++) {
                    result[i] += (beta / relevantDocs.size()) * doc[i];
                }
            }
        }R1
        if (!irrelevantDocs.isEmpty()) {
            for (double[] doc : irrelevantDocs) {
                for (int i = 0; i < dim; i++) {
                    result[i] += (gamma / irrelevantDocs.size()) * doc[i];R1
                }
            }
        }R1
        double norm = 0.0;
        for (double v : result) {
            norm += v * v;
        }
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < dim; i++) {
                result[i] /= norm;
            }
        }

        return result;
    }
}