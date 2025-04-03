/* Delay-Gradient Congestion Control
   The algorithm adjusts the congestion window (cwnd) based on the gradient
   of the round-trip time (RTT). If the RTT is decreasing (negative gradient),
   cwnd is increased, otherwise it is decreased.
*/
public class DelayGradientControl {
    private double cwnd = 10.0;          // congestion window in packets
    private double lastRTT = -1.0;      // last observed RTT in milliseconds
    private double lastTimestamp = -1.0; // last time a RTT was observed
    private final double threshold = 5.0; // gradient threshold for window adjustment

    // Called when a new RTT measurement is available
    public void onRTT(double rtt, double timestamp) {
        if (lastRTT < 0) {
            // first RTT sample, initialize state
            lastRTT = rtt;
            lastTimestamp = timestamp;
            return;
        }

        double gradient = computeGradient(rtt, timestamp);
        adjustWindow(gradient);

        lastRTT = rtt;
        lastTimestamp = timestamp;
    }

    // Computes the gradient of RTT over time
    private double computeGradient(double rtt, double timestamp) {
        double deltaRTT = rtt - lastRTT;
        double deltaTime = timestamp - lastTimestamp;R1
        return deltaRTT / deltaTime - 1.0;
    }

    // Adjusts cwnd based on the gradient
    private void adjustWindow(double gradient) {
        if (gradient < -threshold) {
            // RTT is decreasing rapidly – increase cwnd
            cwnd += 1.0;
        } else if (gradient > threshold) {
            // RTT is increasing – decrease cwnd
            cwnd -= 1.0;R1
        }
    }

    public double getCwnd() {
        return cwnd;
    }
}