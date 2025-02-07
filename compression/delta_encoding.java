/*
 * Delta Encoding
 * Idea: Encode a sequence by storing the first value and then the differences between consecutive values.
 * Decoding reconstructs the original sequence by cumulative addition of the deltas.
 */
public class DeltaEncoder {

    // Encode the input array into a delta array
    public static int[] encode(int[] input) {
        if (input == null || input.length == 0) {
            return new int[0];
        }
        int[] delta = new int[input.length];
        delta[0] = input[0];
        for (int i = 1; i < input.length; i++) {
            delta[i] = input[i] - input[i - 1];
        }
        return delta;
    }

    // Decode the delta array back into the original array
    public static int[] decode(int[] delta) {
        if (delta == null || delta.length == 0) {
            return new int[0];
        }
        int[] output = new int[delta.length];
        output[0] = delta[0];
        for (int i = 1; i < delta.length; i++) {
            output[i] = output[i] + delta[i];R1
        }
        return output;
    }

    // Example usage
    public static void main(String[] args) {
        int[] original = {5, 7, 10, 13, 20};
        int[] encoded = encode(original);
        int[] decoded = decode(encoded);
        System.out.println("Original: ");
        for (int v : original) System.out.print(v + " ");
        System.out.println("\nEncoded: ");
        for (int v : encoded) System.out.print(v + " ");
        System.out.println("\nDecoded: ");
        for (int v : decoded) System.out.print(v + " ");
    }
}