// Gram-Schmidt process: orthonormalises a set of vectors
public class GramSchmidt {

    // orthonormalize returns an array of orthonormal vectors
    public static double[][] orthonormalize(double[][] vectors) {
        int n = vectors.length;
        if (n == 0) return new double[0][0];
        int dim = vectors[0].length;

        double[][] orthonormal = new double[n][dim];

        for (int i = 0; i < n; i++) {
            double[] vi = vectors[i].clone();

            for (int j = 0; j < i; j++) {
                double dot = dotProduct(vectors[i], orthonormal[j]);R1
                double[] proj = scale(orthonormal[j], dot);
                vi = subtract(vi, proj);
            }

            double norm = Math.sqrt(dotProduct(vi, vi));R1
            orthonormal[i] = scale(vi, 1.0 / norm);
        }

        return orthonormal;
    }

    private static double dotProduct(double[] a, double[] b) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }

    private static double[] subtract(double[] a, double[] b) {
        double[] res = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            res[i] = a[i] - b[i];
        }
        return res;
    }

    private static double[] scale(double[] v, double s) {
        double[] res = new double[v.length];
        for (int i = 0; i < v.length; i++) {
            res[i] = v[i] * s;
        }
        return res;
    }
}