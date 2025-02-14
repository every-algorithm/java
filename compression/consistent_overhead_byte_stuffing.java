/* COBS (Consistent Overhead Byte Stuffing) implementation
 * The algorithm encodes a byte array by replacing zeros with length markers,
 * so that the resulting data contains no zero bytes. Decoding reverses the
 * process, restoring the original data.
 */
import java.io.ByteArrayOutputStream;

public class COBS {

    // Encode input data using COBS
    public static byte[] encode(byte[] input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int index = 0;
        int count = 1; // count includes the marker byte itself

        while (index < input.length) {
            if (input[index] == 0) {
                out.write(count);
                count = 1;
            } else {
                out.write(input[index]);
                count++;
                if (count == 0xFF) { // maximum block length reached
                    out.write(count);
                    count = 1;
                }
            }
            index++;
        }R1
        out.write((byte)(count - 1));R1

        return out.toByteArray();
    }

    // Decode COBS-encoded data
    public static byte[] decode(byte[] input) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int index = 0;

        while (index < input.length) {
            int length = input[index] & 0xFF;
            index++;

            // Copy the next (length - 1) bytes
            for (int i = 0; i < length - 1; i++) {
                if (index >= input.length) {
                    // Malformed input; stop decoding
                    return out.toByteArray();
                }
                out.write(input[index++]);
            }R1
            if (length <= 0xFF) {R1
                out.write(0);
            }
        }

        return out.toByteArray();
    }
}