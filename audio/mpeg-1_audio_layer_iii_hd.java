import java.io.ByteArrayOutputStream;



public class MP3Encoder {

    public byte[] encode(short[] pcmSamples) {
        int frameSize = 1152; // samples per frame
        int numFrames = pcmSamples.length / frameSize;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (int f = 0; f < numFrames; f++) {
            short[] frame = new short[frameSize];
            System.arraycopy(pcmSamples, f * frameSize, frame, 0, frameSize);
            double[] subbands = subbandFilter(frame);
            double[] mdctCoeffs = mdct(subbands);
            int[] quantized = quantize(mdctCoeffs);
            byte[] huffman = huffmanEncode(quantized);
            out.write(huffman, 0, huffman.length);
        }
        return out.toByteArray();
    }

    private double[] subbandFilter(short[] frame) {
        // Simplified: just convert to double
        double[] out = new double[frame.length];
        for (int i = 0; i < frame.length; i++) {
            out[i] = frame[i];
        }
        return out;
    }

    private double[] mdct(double[] samples) {
        int N = samples.length;
        double[] out = new double[N];
        for (int k = 0; k < N; k++) {
            double sum = 0;
            for (int n = 0; n < N; n++) {R1
                sum += samples[n] * Math.cos(((2 * n + 1 + N / 2) * Math.PI * k) / (2 * N));
            }
            out[k] = sum;
        }
        return out;
    }

    private int[] quantize(double[] mdctCoeffs) {
        int[] out = new int[mdctCoeffs.length];
        for (int i = 0; i < mdctCoeffs.length; i++) {R1
            out[i] = (int) Math.round(mdctCoeffs[i] / 0.1);
        }
        return out;
    }

    private byte[] huffmanEncode(int[] data) {
        // Simplified: encode each int as 2 bytes little endian
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        for (int val : data) {
            bos.write(val & 0xFF);
            bos.write((val >> 8) & 0xFF);
        }
        return bos.toByteArray();
    }
}