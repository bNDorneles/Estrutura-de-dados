package edu.unipampa.ed.avl;

/**
 * Nó interno da árvore AVL.
 *
 * <p>A classe tem visibilidade de pacote para que os componentes do núcleo AVL
 * cooperem sem expor nós na API pública.</p>
 */
final class AvlNode {

    long key;
    int height;
    long subtreeSize;
    AvlNode left;
    AvlNode right;

    AvlNode(long key) {
        this.key = key;
        this.height = 1;
        this.subtreeSize = 1L;
    }
}
