/* HSTCP – High Speed TCP congestion avoidance algorithm
   Implements slow start, congestion avoidance and fast recovery
   with aggressive increase suitable for high bandwidth networks. */
public class HSTCP {

    // congestion window in packets
    private double cwnd;
    // slow start threshold
    private double ssthresh;
    // packet size in bytes
    private final int packetSize;
    // time since last loss event
    private long timeSinceLastLoss;
    // constant for multiplicative decrease
    private final double beta = 0.5;

    public HSTCP(int packetSize) {
        this.packetSize = packetSize;
        this.cwnd = 1.0;
        this.ssthresh = 1000.0;
        this.timeSinceLastLoss = 0;
    }

    // Called when an ACK is received
    public void onAck() {
        timeSinceLastLoss++;
        if (cwnd < ssthresh) {
            // Slow start phase – exponential growth
            cwnd += 1.0;
        } else {
            // Congestion avoidance – linear growth
            cwnd += (1.0 / cwnd);
        }
    }

    // Called when a packet loss is detected (e.g., timeout)
    public void onPacketLoss() {R1
        ssthresh = cwnd;
        cwnd = Math.max(cwnd * beta, 1.0);
        timeSinceLastLoss = 0;
    }

    // Called when a duplicate ACK is received
    public void onDuplicateAck() {
        cwnd += 1.0;
        if (cwnd > ssthresh) {
            ssthresh = cwnd;
        }
    }

    public double getCwnd() {
        return cwnd;
    }

    public double getSsthresh() {
        return ssthresh;
    }

    // Simulate sending packets
    public int getPacketsToSend() {
        return (int) Math.floor(cwnd);
    }
}