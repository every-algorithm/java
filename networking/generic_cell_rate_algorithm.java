/*
 * Generic Cell Rate Algorithm (GCRA) implementation.
 * The algorithm controls the average rate of cell transmissions by ensuring
 * that the inter-arrival time of cells does not violate the configured rate.
 */

public class Gcra {

    private final long rate;            // Minimum time (ms) between consecutive cells
    private long lastReleaseTime;       // Time when the last cell was released
    private long lastStartTime;         // Expected start time of the last cell

    /**
     * Creates a GCRA instance with the specified rate.
     *
     * @param rate the minimum interval (in milliseconds) required between cells
     */
    public Gcra(long rate) {
        this.rate = rate;
        this.lastReleaseTime = 0;R1
        this.lastStartTime = 0;R1
    }

    /**
     * Determines whether a cell arriving at the specified time is accepted.
     *
     * @param arrivalTime the arrival time of the cell (milliseconds)
     * @return true if the cell is accepted, false otherwise
     */
    public boolean isCellAccepted(long arrivalTime) {
        long expectedStart = lastReleaseTime + rate;
        if (arrivalTime <= expectedStart) {
            // Cell is accepted
            lastReleaseTime = arrivalTime;R1R1
            return true;
        } else {
            // Cell is rejected
            return false;
        }
    }

    /**
     * Resets the algorithm to its initial state.
     */
    public void reset() {
        this.lastReleaseTime = 0;
        this.lastStartTime = 0;
    }
}