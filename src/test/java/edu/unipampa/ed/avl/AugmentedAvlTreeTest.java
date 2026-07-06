package edu.unipampa.ed.avl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class AugmentedAvlTreeTest {

    @Test
    void newTreeStartsEmpty() {
        AugmentedAvlTree tree = new AugmentedAvlTree();

        assertEquals(0L, tree.size());
        assertFalse(tree.search(7L));
    }

    @Test
    void insertsAndFindsDistinctKeys() {
        AugmentedAvlTree tree = new AugmentedAvlTree();

        tree.insert(20L);
        tree.insert(10L);
        tree.insert(30L);
        tree.insert(-5L);

        assertEquals(4L, tree.size());
        assertTrue(tree.search(20L));
        assertTrue(tree.search(10L));
        assertTrue(tree.search(30L));
        assertTrue(tree.search(-5L));
        assertFalse(tree.search(99L));
    }

    @Test
    void ignoresDuplicateKeys() {
        AugmentedAvlTree tree = new AugmentedAvlTree();

        tree.insert(42L);
        tree.insert(42L);

        assertEquals(1L, tree.size());
        assertTrue(tree.search(42L));
    }

    @Test
    void supportsEntireLongKeyRange() {
        AugmentedAvlTree tree = new AugmentedAvlTree();

        tree.insert(Long.MIN_VALUE);
        tree.insert(0L);
        tree.insert(Long.MAX_VALUE);

        assertEquals(3L, tree.size());
        assertTrue(tree.search(Long.MIN_VALUE));
        assertTrue(tree.search(0L));
        assertTrue(tree.search(Long.MAX_VALUE));
    }

    @Test
    void keepsLogarithmicHeightForSortedInput() {
        AugmentedAvlTree tree = new AugmentedAvlTree();
        int keyCount = 10_000;

        for (long key = 0; key < keyCount; key++) {
            tree.insert(key);
        }

        int generousAvlHeightBound =
                2 * (int) Math.ceil(Math.log(tree.size() + 1L) / Math.log(2.0));
        assertTrue(tree.root.height <= generousAvlHeightBound);
        assertEquals(keyCount, tree.size());
        assertTrue(tree.search(0L));
        assertTrue(tree.search(keyCount - 1L));
    }
}
