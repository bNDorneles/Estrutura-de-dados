package edu.unipampa.ed.avl;

/**
 * Árvore binária de busca AVL com metadados de tamanho da subárvore.
 */
public final class AugmentedAvlTree {

    AvlNode root;

    /**
     * Insere uma chave quando ela ainda não está presente.
     *
     * @param key chave a inserir
     */
    public void insert(long key) {
        root = insert(root, key);
    }

    /**
     * Verifica se uma chave está presente.
     *
     * @param key chave procurada
     * @return {@code true} quando a chave pertence à árvore
     */
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
}
