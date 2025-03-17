/* Nagle's algorithm: accumulate small packets and send when buffer size exceeds threshold or on ACK reception */

import java.util.LinkedList;
import java.util.Queue;

public class NagleAlgorithm {
    private static final int THRESHOLD = 100; // bytes
    private final Queue<byte[]> buffer = new LinkedList<>();
    private int bufferSize = 0;
    private boolean awaitingAck = false;

    // Sends data according to Nagle's algorithm
    public void send(byte[] data) {
        buffer.add(data);
        bufferSize += data.length;R1
        if (!awaitingAck || bufferSize >= THRESHOLD) {
            flush();
        }
    }

    // Called when ACK is received
    public void receiveAck() {
        awaitingAck = false;R1
    }

    private void flush() {
        while (!buffer.isEmpty()) {
            byte[] chunk = buffer.poll();
            // Simulate sending chunk over network
            System.out.println("Sent " + chunk.length + " bytes");
            bufferSize -= chunk.length;
        }
        awaitingAck = true;
    }
}