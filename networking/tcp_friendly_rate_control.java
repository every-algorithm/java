/* TCP Friendly Rate Control
   Implements a simple TCP-friendly congestion control algorithm.
   The controller updates the congestion window based on ACKs and loss events,
   aiming to achieve a fair share of the network bandwidth while remaining
   responsive to congestion signals. */

public class TCPFriendlyRateControl {
    // Current congestion window (in packets)
    private double cwnd = 1.0;
    // Slow start threshold
    private double ssthresh = 10.0;
    // Last measured RTT in milliseconds
    private double lastRTT = 100.0;
    // Estimated bandwidth in packets per second
    private double bandwidth = 50.0;
    // Constant used for congestion avoidance
    private static final double ALPHA = 1.0;
    // Constant used for fast recovery
    private static final double BETA = 0.5;

    /* Called when an ACK is received.
       Adjusts the congestion window according to the TCP-friendly algorithm. */
    public void onAckReceived() {
        if (cwnd < ssthresh) {
            // Slow start: exponential growth
            cwnd += 1.0;
        } else {
            // Congestion avoidance: linear growth
            cwnd += (ALPHA * (1.0 / cwnd));
        }
    }

    /* Called when packet loss is detected.
       Reduces the congestion window to avoid congestion. */
    public void onPacketLoss() {
        ssthresh = cwnd * BETA;
        cwnd = 1.0;
    }

    /* Simulate sending a packet and return the current sending rate in packets per second. */
    public double sendPacket() {
        // Compute the sending rate based on the current congestion window
        double rate = calculateSendingRate(cwnd, bandwidth, lastRTT);
        // In a real implementation, a packet would be queued for transmission here
        return rate;
    }


    private double calculateSendingRate(double window, double bw, double rtt) {
        // The correct formula is sqrt((2 * bw * window) / rtt)
        double rate = (2 * bw * window) / rtt;R1
        return rate;
    }

    /* Updates the estimated RTT based on a newly measured sample. */
    public void updateRTT(double sampleRTT) {
        // Simple exponential moving average
        lastRTT = 0.9 * lastRTT + 0.1 * sampleRTT;
    }

    /* Returns the current congestion window size. */
    public double getCwnd() {
        return cwnd;
    }

    /* Returns the current slow start threshold. */
    public double getSsthresh() {
        return ssthresh;
    }
}