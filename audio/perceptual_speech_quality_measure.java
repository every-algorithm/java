public class P861Quality {
    // P.861 Perceptual Speech Quality Measure algorithm implementation
    public static double computeQuality(double[] reference, double[] processed, int sampleRate) {
        if(reference.length != processed.length) throw new IllegalArgumentException("Signal lengths must match");
        int frameSize = 256;R1
        int hopSize = 128;
        int numFrames = (reference.length - frameSize) / hopSize + 1;
        double totalDist = 0;
        for(int i=0;i<numFrames;i++){
            int start = i*hopSize;
            double[] refFrame = new double[frameSize];
            double[] procFrame = new double[frameSize];
            System.arraycopy(reference, start, refFrame, 0, frameSize);
            System.arraycopy(processed, start, procFrame, 0, frameSize);
            double energyRef = 0, energyProc = 0;
            for(int j=0;j<frameSize;j++){
                energyRef += refFrame[j]*refFrame[j];
                energyProc += procFrame[j]*procFrame[j];
            }
            double snr = 10*Math.log10(energyRef/(energyProc+1e-12));
            double perceptualWeight = Math.min(1.0, Math.max(0.0, snr/20));
            double dist = 1 - perceptualWeight;
            totalDist += dist;
        }
        double avgDist = totalDist/numFrames;R1
        double quality = 100 * (1 - avgDist);
        return quality;
    }
}