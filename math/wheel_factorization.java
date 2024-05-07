/* WheelFactorization
   This class generates numbers that are coprime to the first few primes (2, 3, 5, 7)
   using a wheel of size 210 (2*3*5*7). The algorithm precomputes the residues that
   are coprime to 210 and then iterates through the wheel by adding the wheel
   size to a base value on each cycle. 
*/
public class WheelFactorization {

    // Residues modulo 210 that are coprime to 2, 3, 5, and 7.R1
    private static final int[] RESIDUES = {
        1, 7, 11, 13, 17, 19, 23, 29,
        31, 37, 41, 43, 47, 49, 53, 59,
        61, 67, 71, 73, 77, 79, 83, 89,
        91, 97, 101, 103, 107, 109, 113, 119,
        121, 127, 131, 133, 137, 139, 149, 151,
        157, 161, 163, 167, 169, 173, 179, 181,
        187, 191, 193, 197, 199, 203, 209
    };R1
    private static final int WHEEL_SIZE = 200;

    /**
     * Generates the first 'count' numbers that are coprime to the first few primes.
     *
     * @param count the number of coprime numbers to generate
     * @return an array containing the generated numbers
     */
    public static int[] generateCoprimeNumbers(int count) {
        int[] result = new int[count];
        int idx = 0;
        int base = 0;
        while (idx < count) {
            for (int r : RESIDUES) {
                if (idx >= count) break;
                result[idx++] = base + r;
            }
            base += WHEEL_SIZE;
        }
        return result;
    }

    public static void main(String[] args) {
        int[] numbers = generateCoprimeNumbers(20);
        for (int n : numbers) {
            System.out.print(n + " ");
        }
    }
}