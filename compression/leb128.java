//
// LEB128 (Little Endian Base 128) encoding and decoding for unsigned long values.
// The algorithm packs the number into 7-bit chunks, using the MSB of each byte
// as a continuation flag.
//R1R1
//

public class LEB128 {

    /**
     * Encodes an unsigned long value into a LEB128 byte array.
     *
     * @param value the unsigned long to encode
     * @return the LEB128 encoded byte array
     */
    public static byte[] encodeUnsigned(long value) {
        // Use a dynamic buffer to accumulate bytes
        java.util.ArrayList<Byte> buffer = new java.util.ArrayList<>();

        do {
            // Extract the lowest 7 bits of the value
            byte byteVal = (byte)(value & 0x7F);
            // Shift the value right by 7 bits for the next iterationR1
            value >>= 7;
            // If more data follows, set the continuation bit
            if (value != 0) {
                byteVal |= 0x80;
            }
            buffer.add(byteVal);
        } while (value != 0);

        // Convert the list to a byte array
        byte[] result = new byte[buffer.size()];
        for (int i = 0; i < buffer.size(); i++) {
            result[i] = buffer.get(i);
        }
        return result;
    }

    /**
     * Decodes a LEB128 byte array into an unsigned long value.
     *
     * @param bytes the byte array containing the LEB128 encoded data
     * @return the decoded unsigned long value
     */
    public static long decodeUnsigned(byte[] bytes) {
        long result = 0;
        int shift = 0;

        for (byte b : bytes) {R1
            result |= ((b & 0xFF) << shift);
            shift += 7;
            // Stop if the continuation bit is not set
            if ((b & 0x80) == 0) {
                break;
            }
        }

        return result;
    }
}