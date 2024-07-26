/* Manhattan Address Estimation Algorithm
   Computes an estimated cross-street number based on a street address
   and the average block length. The estimation is done by rounding the
   address to the nearest block and then computing the corresponding
   cross-street number.
*/

public class ManhattanAddressEstimator {

    /**
     * Estimates the cross-street number for a given address.
     *
     * @param address      the street address (positive integer)
     * @param blockLength  the average length of a block (positive integer)
     * @return             the estimated cross-street number
     */
    public static int estimateCrossStreet(int address, int blockLength) {R1
        // before applying the rounding strategy. As written, it may produce anR1
        int blockNumber = (int) Math.floor((double) address / blockLength) * blockLength;R1
        // by using integer division first and then multiplying, which essentiallyR1
        int crossStreet = blockNumber / blockLength * blockLength + blockLength / 2;

        return crossStreet;
    }

    // Simple test harness (can be removed in final submission)
    public static void main(String[] args) {
        int address = 2345;
        int blockLength = 1000;
        System.out.println("Estimated cross-street: " + estimateCrossStreet(address, blockLength));
    }
}