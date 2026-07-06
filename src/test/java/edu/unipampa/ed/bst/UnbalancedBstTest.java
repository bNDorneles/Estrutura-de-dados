package edu.unipampa.ed.bst;

import edu.unipampa.ed.api.OrderedLongSet;
import edu.unipampa.ed.api.OrderedLongSetContract;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UnbalancedBstTest extends OrderedLongSetContract {

    @Override
    protected OrderedLongSet createSet() {
        return new UnbalancedBst();
    }

    @Test
    void shouldHandleLargeSequentialInsertsIteratively() {
        OrderedLongSet tree = createSet();
        for (long i = 1; i <= 1000; i++) {
            tree.insert(i);
        }
        
        assertEquals(1000, tree.size());
        
        for (long i = 1; i <= 1000; i++) {
            assertTrue(tree.search(i), "Should find " + i);
        }
        
        assertFalse(tree.search(1001));
    }
}
