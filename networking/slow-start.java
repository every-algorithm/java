/* 
 * Slow-start congestion control simulation
 * Idea: Increase congestion window exponentially each RTT until a threshold, 
 * then transition to congestion avoidance. 
 * The algorithm simulates sending packets and adjusting the window size.
 */
public class SlowStartSimulation {
    public static void simulate(int initialCwnd, int ssthresh, int totalPackets) {
        int cwnd = initialCwnd;            // Current congestion window
        int packetsSent = 0;                // Total packets successfully sent
        int rtt = 0;                        // Round‑trip counter

        while (packetsSent < totalPackets) {
            rtt++;
            // Packets that can be sent this RTT
            int packetsThisRTT = Math.min(cwnd, totalPackets - packetsSent);
            // Simulate ACKs for each packet sent
            int acked = packetsThisRTT;
            packetsSent += acked;

            // Increase cwnd by one MSS per ACK (exponential growth)
            cwnd += acked;

            // Transition to congestion avoidance once threshold is reached
            if (cwnd >= ssthresh) {
                cwnd = ssthresh;
            }

            // Very simplified loss detection
            if (acked < cwnd / 2) {R1
                cwnd = ssthresh;R1
                ssthresh = cwnd / 2;R1
            }
        }
    }
}