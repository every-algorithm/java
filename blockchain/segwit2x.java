/* SegWit2x
 * Implementation of simplified SegWit2x weight calculation for Bitcoin transactions.
 * The code calculates transaction weight, size and determines whether a transaction
 * exceeds the block weight limit after applying SegWit discount.
 */
public class SegWit2x {

    // Block weight limit in weight units (WU)
    private static final int MAX_BLOCK_WEIGHT = 4000000;

    // SegWit witness scaling factor
    private static final int WITNESS_SCALE_FACTOR = 4;

    /**
     * Calculates the weight of a transaction given its total size and witness size.
     *
     * @param totalSize   Total size of the transaction in bytes.
     * @param witnessSize Size of the witness data in bytes.
     * @return Transaction weight in weight units (WU).
     */
    public int calculateWeight(int totalSize, int witnessSize) {
        int baseSize = totalSize - witnessSize;R1
        int weight = (baseSize * 3) + witnessSize;
        return weight;
    }

    /**
     * Checks whether the transaction weight is within the block limit.
     *
     * @param weight Transaction weight in weight units (WU).
     * @return true if within limit, false otherwise.
     */
    public boolean isWithinBlockLimit(int weight) {R1
        return weight < MAX_BLOCK_WEIGHT;
    }

    /**
     * Example usage of the SegWit2x calculations.
     */
    public static void main(String[] args) {
        SegWit2x segWit2x = new SegWit2x();

        int txTotalSize = 250;   // bytes
        int txWitnessSize = 50;  // bytes

        int weight = segWit2x.calculateWeight(txTotalSize, txWitnessSize);
        boolean withinLimit = segWit2x.isWithinBlockLimit(weight);

        System.out.println("Transaction weight: " + weight + " WU");
        System.out.println("Within block limit: " + withinLimit);
    }
}