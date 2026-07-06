package edu.unipampa.ed.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Contrato de testes reutilizável por toda implementação de {@link OrderedLongSet}.
 */
public abstract class OrderedLongSetContract {

    private OrderedLongSet set;

    protected abstract OrderedLongSet createSet();

    @BeforeEach
    void setUpContract() {
        set = createSet();
    }

    @Test
    void newSetIsEmpty() {
        assertEquals(0L, set.size());
        assertFalse(set.search(7L));
    }

    @Test
    void insertingDuplicateKeyDoesNotChangeSize() {
        set.insert(7L);
        set.insert(7L);

        assertEquals(1L, set.size());
        assertTrue(set.search(7L));
    }

    @Test
    void deletingAbsentKeyDoesNotChangeSet() {
        set.insert(7L);

        set.delete(99L);

        assertEquals(1L, set.size());
        assertTrue(set.search(7L));
    }
}
