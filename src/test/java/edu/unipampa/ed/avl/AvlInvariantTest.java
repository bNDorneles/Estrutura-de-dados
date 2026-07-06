package edu.unipampa.ed.avl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;
import java.util.TreeSet;
import org.junit.jupiter.api.Test;

class AvlInvariantTest {

    @Test
    void matchesTreeSetAcrossReproducibleRandomOperations() {
        AugmentedAvlTree tree = new AugmentedAvlTree();
        TreeSet<Long> expected = new TreeSet<>();
        Random random = new Random(2L);

        for (int operation = 1; operation <= 10_000; operation++) {
            long key = random.nextInt(2_000) - 1_000L;
            switch (random.nextInt(3)) {
                case 0 -> {
                    tree.insert(key);
                    expected.add(key);
                }
                case 1 -> {
                    tree.delete(key);
                    expected.remove(key);
                }
                case 2 -> assertEquals(expected.contains(key), tree.search(key));
                default -> throw new AssertionError("Operação aleatória inválida");
            }

            if (operation % 100 == 0) {
                assertMatchesReference(tree, expected);
            }
        }
    }

    @Test
    void rejectsBrokenSearchTreeOrdering() {
        AugmentedAvlTree tree = treeWith(2L, 1L, 3L);
        tree.root.left.key = 4L;

        IllegalStateException error =
                assertThrows(IllegalStateException.class, tree::validateInvariants);

        assertTrue(error.getMessage().contains("ordenação"));
    }

    @Test
    void rejectsBrokenOrderingInRightSubtree() {
        AugmentedAvlTree tree = treeWith(2L, 1L, 3L);
        tree.root.right.key = 0L;

        IllegalStateException error =
                assertThrows(IllegalStateException.class, tree::validateInvariants);

        assertTrue(error.getMessage().contains("ordenação"));
    }

    @Test
    void rejectsIncorrectStoredHeight() {
        AugmentedAvlTree tree = treeWith(2L, 1L, 3L);
        tree.root.height = 99;

        IllegalStateException error =
                assertThrows(IllegalStateException.class, tree::validateInvariants);

        assertTrue(error.getMessage().contains("altura"));
    }

    @Test
    void rejectsIncorrectStoredSubtreeSize() {
        AugmentedAvlTree tree = treeWith(2L, 1L, 3L);
        tree.root.subtreeSize = 99L;

        IllegalStateException error =
                assertThrows(IllegalStateException.class, tree::validateInvariants);

        assertTrue(error.getMessage().contains("tamanho"));
    }

    @Test
    void rejectsBalanceFactorOutsideAvlRange() {
        AugmentedAvlTree tree = new AugmentedAvlTree();
        tree.root = new AvlNode(3L);
        tree.root.left = new AvlNode(2L);
        tree.root.left.left = new AvlNode(1L);
        AvlRotations.recompute(tree.root.left);
        AvlRotations.recompute(tree.root);

        IllegalStateException error =
                assertThrows(IllegalStateException.class, tree::validateInvariants);

        assertTrue(error.getMessage().contains("balanceamento"));
    }

    private static void assertMatchesReference(
            AugmentedAvlTree tree, TreeSet<Long> expected) {
        tree.validateInvariants();
        assertEquals(expected.size(), tree.size());

        long index = 0L;
        for (long key : expected) {
            assertTrue(tree.search(key));
            assertEquals(index, tree.rank(key));
            assertEquals(key, tree.select(index));
            index++;
        }

        long expectedRangeCount = expected.subSet(-500L, true, 500L, true).size();
        assertEquals(expectedRangeCount, tree.rangeCount(-500L, 500L));
    }

    private static AugmentedAvlTree treeWith(long... keys) {
        AugmentedAvlTree tree = new AugmentedAvlTree();
        for (long key : keys) {
            tree.insert(key);
        }
        return tree;
    }
}
