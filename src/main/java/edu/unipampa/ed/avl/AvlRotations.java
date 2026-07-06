package edu.unipampa.ed.avl;

/**
 * Mantém os metadados e executa o rebalanceamento dos nós AVL.
 */
final class AvlRotations {

    private AvlRotations() {
    }

    static int height(AvlNode node) {
        return node == null ? 0 : node.height;
    }

    static long size(AvlNode node) {
        return node == null ? 0L : node.subtreeSize;
    }

    static void recompute(AvlNode node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
        node.subtreeSize = 1L + size(node.left) + size(node.right);
    }

    static int balanceFactor(AvlNode node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    static AvlNode rebalance(AvlNode node) {
        if (node == null) {
            return null;
        }

        recompute(node);
        int balance = balanceFactor(node);

        if (balance > 1) {
            if (balanceFactor(node.left) < 0) {
                node.left = rotateLeft(node.left);
            }
            return rotateRight(node);
        }

        if (balance < -1) {
            if (balanceFactor(node.right) > 0) {
                node.right = rotateRight(node.right);
            }
            return rotateLeft(node);
        }

        return node;
    }

    private static AvlNode rotateRight(AvlNode node) {
        AvlNode newRoot = node.left;
        AvlNode transferredSubtree = newRoot.right;

        newRoot.right = node;
        node.left = transferredSubtree;

        recompute(node);
        recompute(newRoot);
        return newRoot;
    }

    private static AvlNode rotateLeft(AvlNode node) {
        AvlNode newRoot = node.right;
        AvlNode transferredSubtree = newRoot.left;

        newRoot.left = node;
        node.right = transferredSubtree;

        recompute(node);
        recompute(newRoot);
        return newRoot;
    }
}
