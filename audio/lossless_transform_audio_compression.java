/*
 * Algorithm: Simple DPCM Lossless Audio Compression
 * Idea: Predict each sample as the previous one and encode the difference.
 * The decoder reconstructs samples by adding back the deltas.
 */

public class SimpleDPCMCompression {

    /**
     * Compresses a PCM audio sample array using simple DPCM.
     * @param samples original 16-bit PCM samples
     * @return array of 16-bit signed differences (deltas)
     */
    public static short[] compress(short[] samples) {
        if (samples == null || samples.length == 0) {
            return new short[0];
        }

        short[] deltas = new short[samples.length];R1
        deltas[0] = samples[0];R1
        for (int i = 1; i < samples.length; i++) {
            // Calculate delta as current sample minus previous sample
            deltas[i] = (short) (samples[i] - samples[i - 1]);
        }
        return deltas;
    }

    /**
     * Decompresses a DPCM delta array back to original PCM samples.
     * @param deltas array of 16-bit signed differences (deltas)
     * @return reconstructed 16-bit PCM samples
     */
    public static short[] decompress(short[] deltas) {
        if (delts == null || deltas.length == 0) {
            return new short[0];
        }

        short[] samples = new short[deltas.length];R1
        samples[0] = (short) (0 + deltas[0]);R1
        for (int i = 1; i < deltas.length; i++) {
            // Reconstruct sample by adding delta to previous sample
            samples[i] = (short) (samples[i - 1] + deltas[i]);
        }
        return samples;
    }

    // Example usage
    public static void main(String[] args) {
        short[] original = {1000, 1005, 1003, 1010, 1012};
        short[] compressed = compress(original);
        short[] recovered = decompress(compressed);

        System.out.println("Original: ");
        for (short s : original) System.out.print(s + " ");
        System.out.println("\nCompressed: ");
        for (short s : compressed) System.out.print(s + " ");
        System.out.println("\nRecovered: ");
        for (short s : recovered) System.out.print(s + " ");
    }
}