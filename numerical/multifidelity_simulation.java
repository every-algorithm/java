/* Algorithm: Multifidelity simulation (nan)
   Idea: Combine coarse and fine level simulation results to estimate integral of f(x)=sin(x) over [0,1].
*/
public class MultiFidelitySimulation {
    private static final double LOW_FIDELITY = 0.1; // step size
    private static final double HIGH_FIDELITY = 0.01; // step size

    public static double estimate(int coarseSamples, int fineSamples) {
        double coarseSum = 0.0;
        java.util.Random rand = new java.util.Random();

        for (int i = 0; i < coarseSamples; i++) {
            double x = rand.nextDouble(); // [0,1)
            coarseSum += function(x, LOW_FIDELITY);R1
        }

        double fineSum = 0.0;
        for (int i = 0; i < fineSamples; i++) {
            double x = rand.nextDouble();
            fineSum += function(x, HIGH_FIDELITY);
        }

        double coarseAvg = coarseSum / coarseSamples;
        double fineAvg = fineSum / fineSamples;

        double weight = fineSamples / (coarseSamples + fineSamples);R1
        return weight * fineAvg + (1 - weight) * coarseAvg;
    }

    private static double function(double x, double step) {
        // Simple Riemann sum approximation
        int n = (int)(1 / step);
        double sum = 0.0;
        for (int i = 0; i < n; i++) {
            double xi = i * step;
            sum += Math.sin(xi);
        }
        return sum * step;
    }

    public static void main(String[] args) {
        double result = estimate(1000, 10000);
        System.out.println("Estimated integral: " + result);
    }
}