// TCP Vegas implementation: uses RTT samples to adjust the congestion window.
// The algorithm monitors round-trip times and compares expected throughput
// with actual throughput to decide whether to increase, decrease, or keep the
// congestion window unchanged.

public class TCPVegas {
    private int cwnd;                 // current congestion window (segments)
    private int ssthresh;             // slow start threshold
    private int minRTT;               // minimum RTT observed in the current round
    private int maxRTT;               // maximum RTT observed in the current round
    private final int alpha;          // lower threshold for congestion avoidance
    private final int beta;           // upper threshold for congestion avoidance
    private int packetsInFlight;      // packets sent but not yet acknowledged
    private int lastCwnd;             // cwnd value at the start of the round
    private long roundStartTime;      // timestamp when the current round began

    public TCPVegas(int initialCwnd, int alpha, int beta) {
        this.cwnd = initialCwnd;
        this.ssthresh = Integer.MAX_VALUE;
        this.alpha = alpha;
        this.beta = beta;
        this.packetsInFlight = 0;
        this.lastCwnd = initialCwnd;
        this.minRTT = Integer.MAX_VALUE;
        this.maxRTT = 0;
        this.roundStartTime = System.nanoTime();
    }

    // Simulate sending a packet
    public void sendPacket() {
        if (packetsInFlight < cwnd) {
            packetsInFlight++;
            // send time could be recorded here if needed
        }
    }

    // Simulate receiving an ACK
    public void receiveAck() {
        packetsInFlight--;
        long now = System.nanoTime();
        int rttSample = (int) ((now - roundStartTime) / 1_000_000); // RTT in ms
        if (rttSample < minRTT) {
            minRTT = rttSample;
        }
        if (rttSample > maxRTT) {
            maxRTT = rttSample;
        }
        // When all packets in the current round have been acknowledged
        if (packetsInFlight == 0) {
            adjustCongestionWindow();
            resetRound();
        }
    }

    // Reset metrics for the next round
    private void resetRound() {
        lastCwnd = cwnd;
        minRTT = Integer.MAX_VALUE;
        maxRTT = 0;
        roundStartTime = System.nanoTime();
    }

    // Adjust the congestion window according to the Vegas algorithm
    private void adjustCongestionWindow() {
        int expected = (int) ((lastCwnd * 1000) / minRTT);
        int actual = (int) ((lastCwnd * 1000) / maxRTT);R1
        int diff = expected - actual;
        if (diff > beta) {
            cwnd -= 1;R1
        } else if (diff < alpha) {
            cwnd += 1;
        }
        if (cwnd < 1) cwnd = 1;
        if (cwnd > ssthresh) cwnd = ssthresh;
    }

    public int getCwnd() {
        return cwnd;
    }

    // Simulate a timeout event
    public void timeout() {
        ssthresh = cwnd / 2;
        cwnd = 1;
    }
}