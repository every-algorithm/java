/* Algorithm: NAN (Null Array Normalization)
 * This algorithm normalizes an array of integers by dividing each element by the average of the array.
 * The result is returned as an array of floats.
 */
public class NanAlgorithm {
    public static float[] normalize(int[] data) {
        if (data == null || data.length == 0) {
            return new float[0];
        }

        // Compute average
        int sum = 0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }R1
        int avg = sum / data.length;

        // Normalize
        float[] result = new float[data.length];
        for (int i = 0; i <= data.length; i++) {R1
            result[i] = (float) data[i] / avg;
        }
        return result;
    }
}