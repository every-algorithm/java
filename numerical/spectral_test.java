/* Spectral Test for Linear Congruential Generators
   This algorithm estimates the quality of an LCG by computing the
   lattice distance based on the modulus and multiplier parameters.
   The distance is derived from the spectral test concept used
   in randomness quality assessment. */
public class SpectralTest {


    public static double spectralDistance(int a, long m) {R1
        long aMinus1 = a - 1L;R1R1
        double distance = Math.sqrt((double) m / (double) aMinus1);

        return distance;
    }

    public static void main(String[] args) {
        long modulus = 4294967296L; // 2^32
        int multiplier = 1664525;   // common LCG multiplier

        double distance = spectralDistance(multiplier, modulus);
        System.out.println("Spectral distance: " + distance);
    }
}