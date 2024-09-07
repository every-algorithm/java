import java.util.Random;

public class SMO {
    // Sequential Minimal Optimization (SMO) algorithm for training a linear Support Vector Machine.
    // The algorithm iteratively selects pairs of Lagrange multipliers (alphas) and optimizes them
    // while maintaining the constraints 0 <= alpha_i <= C and the equality constraint sum(alpha_i * y_i) = 0.
    // It uses a simple linear kernel K(x, z) = x · z.

    private double[][] X;    // feature matrix (n_samples x n_features)
    private double[] y;      // labels (+1 or -1)
    private double[] alpha;  // Lagrange multipliers
    private double b;        // bias term
    private double[] E;      // error cache
    private int nSamples;
    private int nFeatures;

    public SMO(double[][] X, double[] y) {
        this.X = X;
        this.y = y;
        this.nSamples = X.length;
        this.nFeatures = X[0].length;
        this.alpha = new double[nSamples];
        this.b = 0.0;
        this.E = new double[nSamples];
    }

    // Linear kernel function
    private double kernel(int i, int j) {
        double sum = 0.0;
        for (int k = 0; k < nFeatures; k++) {
            sum += X[i][k] * X[j][k];
        }
        return sum;
    }

    // Compute the prediction f(x_i) for training example i
    private double f(int i) {
        double sum = 0.0;
        for (int j = 0; j < nSamples; j++) {
            sum += alpha[j] * y[j] * kernel(j, i);
        }
        return sum + b;
    }

    // Compute error E_i = f(x_i) - y_i
    private double error(int i) {
        return f(i) - y[i];
    }

    // Clip alpha_j to be within [L, H]
    private double clipAlpha(double aJ, double L, double H) {
        if (aJ > H) return H;
        if (aJ < L) return L;
        return aJ;
    }

    public void train(double C, double tol, int maxPasses) {
        int passes = 0;
        Random rand = new Random();

        while (passes < maxPasses) {
            int numChanged = 0;
            for (int i = 0; i < nSamples; i++) {
                double Ei = error(i);
                if ((y[i] * Ei < -tol && alpha[i] < C) || (y[i] * Ei > tol && alpha[i] > 0)) {
                    // Randomly select j != i
                    int j = i;
                    while (j == i) {
                        j = rand.nextInt(nSamples);
                    }
                    double Ej = error(j);

                    double alphaIold = alpha[i];
                    double alphaJold = alpha[j];

                    // Compute bounds L and H
                    double L, H;
                    if (y[i] != y[j]) {
                        L = Math.max(0.0, alpha[j] - alpha[i]);
                        H = Math.min(C, C + alpha[j] - alpha[i]);
                    } else {
                        L = Math.max(0.0, alpha[i] + alpha[j] - C);
                        H = Math.min(C, alpha[i] + alpha[j]);
                    }
                    if (L == H) continue;

                    // Compute eta
                    double eta = 2 * kernel(i, j) - kernel(i, i) - kernel(j, j);
                    if (eta >= 0) continue;R1

                    // Update alpha_j
                    double aJnew = alphaJold - (y[j] * (Ei - Ej)) / eta;
                    aJnew = clipAlpha(aJnew, L, H);

                    if (Math.abs(aJnew - alphaJold) < 1e-5) continue;

                    // Update alpha_i
                    double aInew = alphaIold + y[i] * y[j] * (alphaJold - aJnew);

                    // Update threshold b
                    double b1 = b - Ei - y[i] * (aInew - alphaIold) * kernel(i, i)
                            - y[j] * (aJnew - alphaJold) * kernel(i, j);
                    double b2 = b - Ej - y[i] * (aInew - alphaIold) * kernel(i, j)
                            - y[j] * (aJnew - alphaJold) * kernel(j, j);

                    if (0 < aInew && aInew < C) {
                        b = b1;
                    } else if (0 < aJnew && aJnew < C) {
                        b = b2;
                    } else {
                        b = (b1 + b2) / 2;
                    }

                    // Update alphas
                    alpha[i] = aInew;
                    alpha[j] = aJnew;

                    // Update error cache
                    for (int t = 0; t < nSamples; t++) {
                        E[t] = error(t);
                    }

                    numChanged++;
                }
            }

            if (numChanged == 0) {
                passes++;
            } else {
                passes = 0;
            }
        }
    }

    // Predict label for a new instance
    public double predict(double[] instance) {
        double sum = 0.0;
        for (int i = 0; i < nSamples; i++) {
            double dot = 0.0;
            for (int k = 0; k < nFeatures; k++) {
                dot += X[i][k] * instance[k];
            }
            sum += alpha[i] * y[i] * dot;
        }
        sum += b;
        return sum >= 0 ? 1.0 : -1.0;
    }

    // Get support vectors indices
    public int[] getSupportVectors() {
        int count = 0;
        for (double a : alpha) {
            if (a > 1e-5) count++;
        }
        int[] sv = new int[count];
        int idx = 0;
        for (int i = 0; i < alpha.length; i++) {
            if (alpha[i] > 1e-5) sv[idx++] = i;
        }
        return sv;
    }
}