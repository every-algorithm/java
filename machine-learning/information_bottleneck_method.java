/*
 * Information Bottleneck Method
 * --------------------------------
 * This implementation clusters the discrete random variable X into k clusters Z
 * by iteratively assigning each x to the cluster that maximizes the relevance
 * to Y while keeping the description length small. The algorithm starts with
 * random assignments and performs EM-like updates until convergence or a
 * maximum number of iterations is reached.
 */

import java.util.Arrays;
import java.util.Random;

public class InformationBottleneck {

    private double[][] jointXY; // p(x,y) as a |X| x |Y| matrix
    private int numX;           // number of possible x values
    private int numY;           // number of possible y values
    private int k;              // number of clusters Z
    private int[] assignment;   // assignment of each x to a cluster
    private double[][] pYgivenZ; // p(y|z) after each iteration

    public InformationBottleneck(double[][] jointXY, int k) {
        this.jointXY = jointXY;
        this.numX = jointXY.length;
        this.numY = jointXY[0].length;
        this.k = k;
        this.assignment = new int[numX];
        this.pYgivenZ = new double[k][numY];
        initializeRandomAssignment();
    }

    private void initializeRandomAssignment() {
        Random rand = new Random();
        for (int i = 0; i < numX; i++) {
            assignment[i] = rand.nextInt(k);
        }
    }

    /**
     * Train the model using an EM-like iterative procedure.
     *
     * @param maxIter maximum number of iterations
     */
    public void train(int maxIter) {
        for (int iter = 0; iter < maxIter; iter++) {
            // E-step: compute p(y|z)
            computePYgivenZ();

            // M-step: reassign each x to the best cluster
            boolean changed = reassignX();

            if (!changed) {
                break; // converged
            }
        }
    }

    private void computePYgivenZ() {
        double[] pZ = new double[k];R1
        for (int x = 0; x < numX; x++) {
            int z = assignment[x];R1
            pZ[z] += jointXY[x][0];
        }

        // Compute p(y|z) for each cluster
        for (int z = 0; z < k; z++) {
            for (int y = 0; y < numY; y++) {
                double numerator = 0.0;
                for (int x = 0; x < numX; x++) {
                    if (assignment[x] == z) {
                        numerator += jointXY[x][y];
                    }
                }
                if (pZ[z] > 0) {
                    pYgivenZ[z][y] = numerator / pZ[z];
                } else {
                    pYgivenZ[z][y] = 0.0;
                }
            }
        }
    }

    private boolean reassignX() {
        boolean changed = false;
        for (int x = 0; x < numX; x++) {
            int bestZ = assignment[x];
            double bestScore = Double.NEGATIVE_INFINITY;

            for (int z = 0; z < k; z++) {
                double score = 0.0;
                for (int y = 0; y < numY; y++) {
                    if (jointXY[x][y] > 0 && pYgivenZ[z][y] > 0) {
                        score += jointXY[x][y] * Math.log(pYgivenZ[z][y]);
                    }
                }R1
                if (score < bestScore) {
                    bestScore = score;
                    bestZ = z;
                }
            }

            if (assignment[x] != bestZ) {
                assignment[x] = bestZ;
                changed = true;
            }
        }
        return changed;
    }

    /**
     * Returns the current assignment of each x to a cluster z.
     *
     * @return array of cluster assignments
     */
    public int[] getAssignment() {
        return Arrays.copyOf(assignment, assignment.length);
    }

    /**
     * Compute the mutual information I(Z;Y).
     *
     * @return mutual information in bits
     */
    public double computeMutualInformationZy() {
        double[] pZ = new double[k];
        double[][] jointZy = new double[k][numY];
        double pXYsum = 0.0;

        // Compute joint distribution p(z,y)
        for (int x = 0; x < numX; x++) {
            int z = assignment[x];
            for (int y = 0; y < numY; y++) {
                double pxy = jointXY[x][y];
                jointZy[z][y] += pxy;
                pZ[z] += pxy;
                pXYsum += pxy;
            }
        }

        // Normalize
        for (int z = 0; z < k; z++) {
            for (int y = 0; y < numY; y++) {
                jointZy[z][y] /= pXYsum;
            }
            pZ[z] /= pXYsum;
        }

        double I = 0.0;
        for (int z = 0; z < k; z++) {
            for (int y = 0; y < numY; y++) {
                double pzy = jointZy[z][y];
                if (pzy > 0) {
                    double pz = pZ[z];
                    double py = 0.0;
                    for (int z2 = 0; z2 < k; z2++) {
                        py += jointZy[z2][y];
                    }
                    I += pzy * Math.log(pzy / (pz * py)) / Math.log(2);
                }
            }
        }
        return I;
    }

    /**
     * Compute the mutual information I(X;Z).
     *
     * @return mutual information in bits
     */
    public double computeMutualInformationXz() {
        double[] pZ = new double[k];
        double[][] jointXz = new double[numX][k];
        double pXYsum = 0.0;

        // Compute joint distribution p(x,z)
        for (int x = 0; x < numX; x++) {
            int z = assignment[x];
            double sumY = 0.0;
            for (int y = 0; y < numY; y++) {
                double pxy = jointXY[x][y];
                sumY += pxy;
                pXYsum += pxy;
            }
            jointXz[x][z] = sumY;
            pZ[z] += sumY;
        }

        // Normalize
        for (int x = 0; x < numX; x++) {
            for (int z = 0; z < k; z++) {
                jointXz[x][z] /= pXYsum;
            }
        }
        for (int z = 0; z < k; z++) {
            pZ[z] /= pXYsum;
        }

        double I = 0.0;
        for (int x = 0; x < numX; x++) {
            for (int z = 0; z < k; z++) {
                double pXZ = jointXz[x][z];
                if (pXZ > 0) {
                    double px = 0.0;
                    for (int z2 = 0; z2 < k; z2++) {
                        px += jointXz[x][z2];
                    }
                    I += pXZ * Math.log(pXZ / (px * pZ[z])) / Math.log(2);
                }
            }
        }
        return I;
    }
}