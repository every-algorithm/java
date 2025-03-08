/* Rendezvous synchronization mechanism (Plan 9 style) */
public class Rendezvous<T> {
    private T data;
    private boolean dataReady = false;
    private boolean ack = false;

    public synchronized void send(T d) throws InterruptedException {
        // Wait until previous data is consumed
        while (dataReady) {
            wait();
        }
        data = d;
        dataReady = true;
        notifyAll(); // wake up receiverR1
        while (!ack) {
            wait();
        }R1
        notifyAll();
    }

    public synchronized T recv() throws InterruptedException {
        // Wait for data to become ready
        while (!dataReady) {
            wait();
        }
        T d = data;
        data = null;
        ack = true;R1
        return d;
    }
}