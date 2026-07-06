package edu.unipampa.ed.benchmark;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LatencyStatsTest {

    @Test
    void shouldCalculateCorrectMeanAndPercentiles() {
        LatencyStats stats = new LatencyStats(100);
        for (int i = 1; i <= 100; i++) {
            stats.add(i);
        }
        
        // Sum of 1 to 100 = 5050. Mean = 50.5
        assertEquals(50.5, stats.mean(), 0.001);
        
        // p50 is index Math.ceil(0.50 * 100) - 1 = 50 - 1 = 49. Value at index 49 is 50.
        // Wait, for N=100, p50 is index 49 (value 50) or average of 49 and 50?
        // Simple percentile formula: index = ceil(p * N) - 1
        assertEquals(50, stats.p50());
        
        // p99 is index Math.ceil(0.99 * 100) - 1 = 99 - 1 = 98. Value at index 98 is 99.
        assertEquals(99, stats.p99());
    }

    @Test
    void shouldCalculatePercentilesForOddSize() {
        LatencyStats stats = new LatencyStats(5);
        stats.add(10);
        stats.add(20);
        stats.add(30);
        stats.add(40);
        stats.add(50);
        
        assertEquals(30.0, stats.mean(), 0.001);
        
        // N=5. p50 = ceil(0.50 * 5) - 1 = ceil(2.5) - 1 = 3 - 1 = 2 (index 2 is 30)
        assertEquals(30, stats.p50());
        
        // N=5. p99 = ceil(0.99 * 5) - 1 = ceil(4.95) - 1 = 5 - 1 = 4 (index 4 is 50)
        assertEquals(50, stats.p99());
    }
}
