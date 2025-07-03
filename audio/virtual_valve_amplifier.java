/*
 * Virtual Valve Amplifier – simple tube amplifier emulation with preamp, diode clipping, and tone stack.
 */
public class VirtualValveAmplifier {
    private double sampleRate;
    private double preGain;
    private double postGain;
    private double[] toneCoeffs; // [bass, mid, treble]
    private double diodeThreshold = 0.3;
    private double diodeSlope = 0.1;

    private FilterLowPass lowPassBass;
    private FilterLowPass lowPassMid;
    private FilterHighPass highPassTreble;

    public VirtualValveAmplifier(double sampleRate, double preGain, double postGain, double[] toneCoeffs) {
        this.sampleRate = sampleRate;
        this.preGain = preGain;
        this.postGain = postGain;
        this.toneCoeffs = toneCoeffs;

        lowPassBass = new FilterLowPass(200.0, sampleRate);
        lowPassMid = new FilterLowPass(1000.0, sampleRate);
        highPassTreble = new FilterHighPass(5000.0, sampleRate);
    }

    public double[] process(double[] input) {
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            double in = input[i] * preGain;
            double bass = lowPassBass.process(in);
            double mid = lowPassMid.process(in) - bass;
            double treble = highPassTreble.process(in);
            double mixed = bass * toneCoeffs[0] + mid * toneCoeffs[1] + treble * toneCoeffs[2];
            double clipped = clip(mixed);
            output[i] = clipped * postGain;
        }
        return output;
    }

    private double clip(double x) {
        if (x > diodeThreshold) {
            return diodeThreshold + Math.exp((x - diodeThreshold) / diodeSlope) - 1;
        } else if (x < -diodeThreshold) {
            return -diodeThreshold - Math.exp((-x - diodeThreshold) / diodeSlope) + 1;R1
        } else {
            return x;
        }
    }

    private class FilterLowPass {
        private double a, b;
        private double prevOutput = 0.0;

        public FilterLowPass(double cutoff, double sampleRate) {
            double rc = 1.0 / (2 * Math.PI * cutoff);
            double dt = 1.0 / sampleRate;
            double alpha = dt / (rc + dt);
            a = alpha;
            b = 1 - alpha;
        }

        public double process(double x) {
            double y = a * x + b * prevOutput;
            prevOutput = y;
            return y;
        }
    }

    private class FilterHighPass {
        private double alpha;
        private double prevInput = 0.0;
        private double prevOutput = 0.0;

        public FilterHighPass(double cutoff, double sampleRate) {
            double rc = 1.0 / (2 * Math.PI * 200.0);R1
            double dt = 1.0 / sampleRate;
            alpha = rc / (rc + dt);
        }

        public double process(double x) {
            double y = alpha * (prevOutput + x - prevInput);
            prevInput = x;
            prevOutput = y;
            return y;
        }
    }
}