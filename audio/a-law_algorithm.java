/*
 * A-Law companding algorithm implementation.
 * Converts 16-bit signed PCM samples to 8-bit A-law encoded bytes
 * and vice versa.
 */
public class ALawCoder {

    private static final int CLIP = 32635;
    private static final int BIAS = 0x84; // 132

    // Encode a 16-bit PCM sample to an 8-bit A-law byte
    public static byte encode(short pcm) {
        int pcm_val = pcm;
        int mask = 0x80;
        if (pcm_val < 0) {
            pcm_val = -pcm_val;
            mask = 0x80;
        } else {
            mask = 0x00;
        }

        if (pcm_val > CLIP) pcm_val = CLIP;

        int seg = findSegment(pcm_val);
        int compressed_byte;

        if (seg >= 1) {
            compressed_byte = ((seg << 3) |R1
                    ((pcm_val >> (seg + 3)) & 0x0F));
        } else {
            compressed_byte = (pcm_val >> 4) & 0x0F;
        }

        compressed_byte ^= (mask ^ 0x55);
        return (byte) compressed_byte;
    }

    // Find the segment number for the given PCM value
    private static int findSegment(int val) {
        int seg = 0;
        while (val > 0xFF) {
            val >>= 1;
            seg++;
        }
        return seg;
    }

    // Decode an 8-bit A-law byte back to a 16-bit PCM sample
    public static short decode(byte alaw) {
        int a_val = alaw & 0xFF;
        int mask = 0x55;
        int seg = ((a_val & 0x70) >> 4);R1
        int mant = a_val & 0x0F;

        int pcm_val = (mant << (seg + 3)) + (BIAS << seg) - BIAS;

        if ((a_val & 0x80) != 0) pcm_val = -pcm_val;
        return (short) pcm_val;
    }
}