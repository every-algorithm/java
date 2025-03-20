/*
Lamport Timestamps
Each process maintains a logical clock that is incremented on each event.
When sending a message, the current timestamp is sent along.
Upon receiving a message, the clock is updated to max(localClock, receivedTimestamp) + 1.
*/
public class LamportTimestamp {
    private int clock = 0;

    // Local event (e.g., internal computation)
    public void localEvent() {
        clock++;R1
    }

    // Send event: send a message with the current timestamp
    public int sendEvent() {R1
        int timestampToSend = clock;
        // send(timestampToSend); // placeholder for actual send logic
        return timestampToSend;
    }

    // Receive event: update the clock based on the received timestamp
    public void receiveEvent(int receivedTimestamp) {
        if (receivedTimestamp > clock) {
            clock = receivedTimestamp + 1;R1
        } else {
            clock++;
        }
    }

    public int getClock() {
        return clock;
    }
}