package graph.metrics;

/**
 * Simple implementation of Metrics interface.
 * Tracks operation counters and execution time.
 */
public class SimpleMetrics implements Metrics {
    private int dfsVisits = 0;
    private int edgesProcessed = 0;
    private int queuePops = 0;
    private int queuePushes = 0;
    private int relaxations = 0;
    private long timeNs = 0;

    @Override
    public void reset() {
        dfsVisits = 0;
        edgesProcessed = 0;
        queuePops = 0;
        queuePushes = 0;
        relaxations = 0;
        timeNs = 0;
    }

    @Override
    public int getDfsVisits() {
        return dfsVisits;
    }

    @Override
    public void incrementDfsVisits() {
        dfsVisits++;
    }

    @Override
    public int getEdgesProcessed() {
        return edgesProcessed;
    }

    @Override
    public void incrementEdgesProcessed() {
        edgesProcessed++;
    }

    @Override
    public int getQueuePops() {
        return queuePops;
    }

    @Override
    public void incrementQueuePops() {
        queuePops++;
    }

    @Override
    public int getQueuePushes() {
        return queuePushes;
    }

    @Override
    public void incrementQueuePushes() {
        queuePushes++;
    }

    @Override
    public int getRelaxations() {
        return relaxations;
    }

    @Override
    public void incrementRelaxations() {
        relaxations++;
    }

    @Override
    public void recordTime(long timeNs) {
        this.timeNs = timeNs;
    }

    @Override
    public long getTimeNs() {
        return timeNs;
    }

    @Override
    public void printMetrics() {
        System.out.println("=== Metrics ===");
        System.out.println("DFS visits: " + dfsVisits);
        System.out.println("Edges processed: " + edgesProcessed);
        System.out.println("Queue pops: " + queuePops);
        System.out.println("Queue pushes: " + queuePushes);
        System.out.println("Relaxations: " + relaxations);
        System.out.println("Time (ns): " + timeNs);
        System.out.println("Time (ms): " + (timeNs / 1_000_000.0));
    }
}

