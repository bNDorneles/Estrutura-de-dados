package edu.unipampa.ed.avl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AvlRotationsTest {

    @Test
    void rebalancesLeftLeftCaseWithRightRotation() {
        AvlNode root = node(30L);
        root.left = node(20L);
        root.left.left = node(10L);
        recomputeBottomUp(root.left, root);

        AvlNode balanced = AvlRotations.rebalance(root);

        assertBalancedThreeNodeTree(balanced);
    }

    @Test
    void rebalancesRightRightCaseWithLeftRotation() {
        AvlNode root = node(10L);
        root.right = node(20L);
        root.right.right = node(30L);
        recomputeBottomUp(root.right, root);

        AvlNode balanced = AvlRotations.rebalance(root);

        assertBalancedThreeNodeTree(balanced);
    }

    @Test
    void rebalancesLeftRightCaseWithDoubleRotation() {
        AvlNode root = node(30L);
        root.left = node(10L);
        root.left.right = node(20L);
        recomputeBottomUp(root.left, root);

        AvlNode balanced = AvlRotations.rebalance(root);

        assertBalancedThreeNodeTree(balanced);
    }

    @Test
    void rebalancesRightLeftCaseWithDoubleRotation() {
        AvlNode root = node(10L);
        root.right = node(30L);
        root.right.left = node(20L);
        recomputeBottomUp(root.right, root);

        AvlNode balanced = AvlRotations.rebalance(root);

        assertBalancedThreeNodeTree(balanced);
    }

    private static AvlNode node(long key) {
        return new AvlNode(key);
    }

    private static void recomputeBottomUp(AvlNode child, AvlNode root) {
        AvlRotations.recompute(child);
        AvlRotations.recompute(root);
    }

    private static void assertBalancedThreeNodeTree(AvlNode root) {
        assertEquals(20L, root.key);
        assertEquals(10L, root.left.key);
        assertEquals(30L, root.right.key);
        assertEquals(2, root.height);
        assertEquals(3L, root.subtreeSize);
        assertEquals(1, root.left.height);
        assertEquals(1L, root.left.subtreeSize);
        assertEquals(1, root.right.height);
        assertEquals(1L, root.right.subtreeSize);
        assertEquals(0, AvlRotations.balanceFactor(root));
    }
}
