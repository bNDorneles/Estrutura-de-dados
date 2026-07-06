package edu.unipampa.ed.avl;

import edu.unipampa.ed.api.OrderedLongSet;

/**
 * Árvore binária de busca AVL com metadados de tamanho da subárvore.
 */
public final class AugmentedAvlTree implements OrderedLongSet {

    AvlNode root;

    /**
     * Insere uma chave quando ela ainda não está presente.
     *
     * @param key chave a inserir
     */
    @Override
    public void insert(long key) {
        root = insert(root, key);
    }

    /**
     * Remove uma chave quando ela está presente.
     *
     * @param key chave a remover
     */
    @Override
    public void delete(long key) {
        root = delete(root, key);
    }

    /**
     * Verifica se uma chave está presente.
     *
     * @param key chave procurada
     * @return {@code true} quando a chave pertence à árvore
     */
    @Override
    public boolean search(long key) {
        AvlNode current = root;

        while (current != null) {
            if (key < current.key) {
                current = current.left;
            } else if (key > current.key) {
                current = current.right;
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Retorna a quantidade de chaves distintas.
     *
     * @return tamanho atual da árvore
     */
    @Override
    public long size() {
        return AvlRotations.size(root);
    }

    private static AvlNode insert(AvlNode node, long key) {
        if (node == null) {
            return new AvlNode(key);
        }

        if (key < node.key) {
            node.left = insert(node.left, key);
        } else if (key > node.key) {
            node.right = insert(node.right, key);
        } else {
            return node;
        }

        return AvlRotations.rebalance(node);
    }

    private static AvlNode delete(AvlNode node, long key) {
        if (node == null) {
            return null;
        }

        if (key < node.key) {
            node.left = delete(node.left, key);
        } else if (key > node.key) {
            node.right = delete(node.right, key);
        } else {
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }

            AvlNode successor = minimum(node.right);
            node.key = successor.key;
            node.right = delete(node.right, successor.key);
        }

        return AvlRotations.rebalance(node);
    }

    private static AvlNode minimum(AvlNode node) {
        AvlNode current = node;
        while (current.left != null) {
            current = current.left;
        }
        return current;
    }
}
