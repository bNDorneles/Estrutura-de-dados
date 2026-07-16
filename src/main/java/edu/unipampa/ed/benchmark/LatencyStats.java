package edu.unipampa.ed.benchmark;

import java.util.Arrays;

public class LatencyStats {
    private final long[] latencies;
    private int count;

    public LatencyStats(int expectedSize) {
        this.latencies = new long[expectedSize];
        this.count = 0;
    }

    public void add(long latency) {
        if (count < latencies.length) {
            latencies[count++] = latency;
        }
    }

    public double mean() {
        if (count == 0) return 0.0;
        long sum = 0;
        for (int i = 0; i < count; i++) {
            sum += latencies[i];
        }
        return (double) sum / count;
    }
    
    private long getPercentile(double percentile) {
        if (count == 0) return 0;
        long[] sorted = Arrays.copyOf(latencies, count);
        Arrays.sort(sorted);
        int index = (int) Math.ceil(percentile * count) - 1;
        if (index < 0) index = 0;
        return sorted[index];
    }

    public long p50() {
        return getPercentile(0.50);
    }

    public long p99() {
        return getPercentile(0.99);
    }
}
