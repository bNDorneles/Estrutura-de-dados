package edu.unipampa.ed.avl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import edu.unipampa.ed.api.OrderedLongSet;
import edu.unipampa.ed.api.OrderedLongSetContract;
import org.junit.jupiter.api.Test;

class AugmentedAvlTreeTest extends OrderedLongSetContract {

    @Override
    protected OrderedLongSet createSet() {
        return new AugmentedAvlTree();
    }

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

    @Test
    void deletesLeafNode() {
        AugmentedAvlTree tree = treeWith(20L, 10L, 30L);

        tree.delete(10L);

        assertEquals(2L, tree.size());
        assertFalse(tree.search(10L));
        assertTrue(tree.search(20L));
        assertTrue(tree.search(30L));
    }

    @Test
    void deletesNodeWithOneChild() {
        AugmentedAvlTree tree = treeWith(20L, 10L, 5L);

        tree.delete(10L);

        assertEquals(2L, tree.size());
        assertFalse(tree.search(10L));
        assertTrue(tree.search(5L));
        assertEquals(5L, tree.root.left.key);
    }

    @Test
    void deletesNodeWithTwoChildrenUsingSuccessor() {
        AugmentedAvlTree tree = treeWith(20L, 10L, 30L, 25L, 40L);

        tree.delete(30L);

        assertEquals(4L, tree.size());
        assertFalse(tree.search(30L));
        assertTrue(tree.search(25L));
        assertTrue(tree.search(40L));
    }

    @Test
    void deletesRootNode() {
        AugmentedAvlTree tree = treeWith(20L, 10L, 30L);

        tree.delete(20L);

        assertEquals(2L, tree.size());
        assertFalse(tree.search(20L));
        assertTrue(tree.search(10L));
        assertTrue(tree.search(30L));
    }

    @Test
    void rebalancesLeftLeftCaseAfterDeletion() {
        AugmentedAvlTree tree = treeWith(3L, 2L, 4L, 1L);

        tree.delete(4L);

        assertEquals(2L, tree.root.key);
        assertEquals(3L, tree.size());
    }

    @Test
    void rebalancesRightRightCaseAfterDeletion() {
        AugmentedAvlTree tree = treeWith(2L, 1L, 3L, 4L);

        tree.delete(1L);

        assertEquals(3L, tree.root.key);
        assertEquals(3L, tree.size());
    }

    @Test
    void rebalancesLeftRightCaseAfterDeletion() {
        AugmentedAvlTree tree = treeWith(5L, 2L, 8L, 3L);

        tree.delete(8L);

        assertEquals(3L, tree.root.key);
        assertEquals(3L, tree.size());
    }

    @Test
    void rebalancesRightLeftCaseAfterDeletion() {
        AugmentedAvlTree tree = treeWith(2L, 1L, 5L, 4L);

        tree.delete(1L);

        assertEquals(4L, tree.root.key);
        assertEquals(3L, tree.size());
    }

    private static AugmentedAvlTree treeWith(long... keys) {
        AugmentedAvlTree tree = new AugmentedAvlTree();
        for (long key : keys) {
            tree.insert(key);
        }
        return tree;
    }
}
