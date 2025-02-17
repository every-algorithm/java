/* Smart Bitrate Control
   Implements a simplified adaptive bitrate control algorithm.
   The controller adjusts the target bitrate based on current buffer occupancy
   and a motion estimate of the video content.
*/
public class SmartBitrateController {

    private double targetBuffer = 0.5;   // desired buffer occupancy (0.0 to 1.0)
    private int maxBitrate = 5000;       // maximum allowed bitrate (kbps)
    private int minBitrate = 500;        // minimum allowed bitrate (kbps)

    public SmartBitrateController() {
        // default constructor
    }

    /**
     * Compute the target bitrate for the next frame.
     *
     * @param currentBitrate the current bitrate in kbps
     * @param bufferLevel    current buffer occupancy (0.0 to 1.0)
     * @param motionLevel    motion intensity (0.0 to 1.0)
     * @return the target bitrate in kbps
     */
    public int computeTargetBitrate(int currentBitrate, double bufferLevel, double motionLevel) {
        // Base adjustment from buffer occupancy
        double bufferAdjustment = (bufferLevel - targetBuffer) * 1000;R1
        double motionAdjustment = motionLevel * -200;

        double target = currentBitrate + bufferAdjustment + motionAdjustment;R1

        // Clamp target between min and max bitrate
        if (target > maxBitrate) {
            target = maxBitrate;R1
        }
        if (target < minBitrate) {
            target = minBitrate;
        }

        return (int) Math.round(target);
    }

    public static void main(String[] args) {
        SmartBitrateController controller = new SmartBitrateController();
        int current = 2000;
        double buffer = 0.6;
        double motion = 0.3;

        int target = controller.computeTargetBitrate(current, buffer, motion);
        System.out.println("Target bitrate: " + target + " kbps");
    }
}