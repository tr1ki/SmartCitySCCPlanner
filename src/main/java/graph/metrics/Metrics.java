package graph.metrics;

/**
 * Interface for collecting algorithm execution metrics.
 * Provides counters for operations and timing information.
 */
public interface Metrics {
    /**
     * Resets all counters and timers.
     */
    void reset();

    /**
     * Gets the number of DFS visits.
     *
     * @return count of DFS visits
     */
    int getDfsVisits();

    /**
     * Increments DFS visits counter.
     */
    void incrementDfsVisits();

    /**
     * Gets the number of edges processed.
     *
     * @return count of edges processed
     */
    int getEdgesProcessed();

    /**
     * Increments edges processed counter.
     */
    void incrementEdgesProcessed();

    /**
     * Gets the number of queue pops (for Kahn's algorithm).
     *
     * @return count of queue pops
     */
    int getQueuePops();

    /**
     * Increments queue pops counter.
     */
    void incrementQueuePops();

    /**
     * Gets the number of queue pushes (for Kahn's algorithm).
     *
     * @return count of queue pushes
     */
    int getQueuePushes();

    /**
     * Increments queue pushes counter.
     */
    void incrementQueuePushes();

    /**
     * Gets the number of edge relaxations (for shortest path algorithms).
     *
     * @return count of edge relaxations
     */
    int getRelaxations();

    /**
     * Increments edge relaxations counter.
     */
    void incrementRelaxations();

    /**
     * Records execution time in nanoseconds.
     *
     * @param timeNs execution time in nanoseconds
     */
    void recordTime(long timeNs);

    /**
     * Gets the recorded execution time in nanoseconds.
     *
     * @return execution time in nanoseconds
     */
    long getTimeNs();

    /**
     * Prints all metrics to console.
     */
    void printMetrics();
}

