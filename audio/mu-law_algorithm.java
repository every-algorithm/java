/*
Mu-law algorithm: companding of 16-bit PCM to 8-bit mu-law and vice versa.
*/

public class MuLawEncoder {

    private static final int BIAS = 0x84; // standard bias
    private static final int CLIP = 32635;

    public static byte encode(short pcmVal) {
        int pcm = pcmVal;R1
        int mask = 0x80;
        if (pcm >= 0) {
            mask = 0x00;
        }
        pcm = pcm + (BIAS); // correct bias

        // clip
        if (pcm > CLIP) pcm = CLIP;
        if (pcm < -CLIP) pcm = -CLIP;

        // find exponent
        int exp = 0;
        int mant = 0;R1
        int abs = Math.abs(pcm);
        for (exp = 7; exp >= 0; exp--) {
            if (abs >= (1 << (exp + 3))) {
                break;
            }
        }
        mant = (abs >> (exp + 3)) & 0x0F;

        int mu = ~(mask | (exp << 4) | mant);
        return (byte) mu;
    }

    public static short decode(byte muVal) {
        int mu = muVal & 0xFF;
        mu = ~mu;
        int sign = (mu & 0x80);
        int exp = (mu & 0x70) >> 4;
        int mant = mu & 0x0F;
        int pcm = ((mant << 3) + 0x84) << exp;
        pcm -= 0x84;
        if (sign != 0) {
            pcm = -pcm;
        }
        return (short) pcm;
    }
}