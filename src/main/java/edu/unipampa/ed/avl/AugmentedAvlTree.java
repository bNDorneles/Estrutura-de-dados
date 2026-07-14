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

    /**
     * Conta quantas chaves são estritamente menores que a chave informada.
     *
     * @param key limite exclusivo
     * @return quantidade de chaves menores que {@code key}
     */
    public long rank(long key) {
        long result = 0L;
        AvlNode current = root;

        while (current != null) {
            if (key <= current.key) {
                current = current.left;
            } else {
                result += 1L + AvlRotations.size(current.left);
                current = current.right;
            }
        }

        return result;
    }

    /**
     * Retorna a chave que ocupa o índice informado na ordem crescente.
     *
     * @param index índice baseado em zero
     * @return chave no índice
     * @throws IndexOutOfBoundsException quando o índice não pertence à árvore
     */
    public long select(long index) {
        long treeSize = size();
        if (index < 0L || index >= treeSize) {
            throw new IndexOutOfBoundsException(
                    "index=" + index + ", size=" + treeSize);
        }

        long remaining = index;
        AvlNode current = root;

        while (current != null) {
            long leftSize = AvlRotations.size(current.left);
            if (remaining < leftSize) {
                current = current.left;
            } else if (remaining == leftSize) {
                return current.key;
            } else {
                remaining -= leftSize + 1L;
                current = current.right;
            }
        }

        throw new IllegalStateException("Metadados de tamanho inconsistentes");
    }

    /**
     * Conta as chaves no intervalo inclusivo.
     *
     * @param lowerInclusive limite inferior inclusivo
     * @param upperInclusive limite superior inclusivo
     * @return quantidade de chaves em {@code [lowerInclusive, upperInclusive]}
     */
    public long rangeCount(long lowerInclusive, long upperInclusive) {
        if (lowerInclusive > upperInclusive) {
            return 0L;
        }
        return countLessOrEqual(upperInclusive) - rank(lowerInclusive);
    }

    /**
     * Retorna a menor chave no intervalo inclusivo.
     *
     * @param lowerInclusive limite inferior inclusivo
     * @param upperInclusive limite superior inclusivo
     * @return menor chave presente em {@code [lowerInclusive, upperInclusive]}
     * @throws IndexOutOfBoundsException quando o intervalo nao contem chaves
     */
    public long rangeMin(long lowerInclusive, long upperInclusive) {
        if (lowerInclusive > upperInclusive) {
            throw new IndexOutOfBoundsException("Intervalo vazio");
        }

        AvlNode current = root;
        Long candidate = null;

        while (current != null) {
            if (current.key < lowerInclusive) {
                current = current.right;
            } else {
                candidate = current.key;
                current = current.left;
            }
        }

        if (candidate == null || candidate > upperInclusive) {
            throw new IndexOutOfBoundsException("Nenhuma chave no intervalo");
        }
        return candidate;
    }

    /**
     * Valida os invariantes internos da árvore.
     *
     * <p>Tem visibilidade de pacote para uso por testes e diagnósticos do
     * núcleo, sem expor nós na API pública.</p>
     *
     * @throws IllegalStateException quando encontra o primeiro nó inválido
     */
    void validateInvariants() {
        validate(root, null, null);
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

    private long countLessOrEqual(long key) {
        long result = 0L;
        AvlNode current = root;

        while (current != null) {
            if (key < current.key) {
                current = current.left;
            } else {
                result += 1L + AvlRotations.size(current.left);
                current = current.right;
            }
        }

        return result;
    }

    private static Validation validate(
            AvlNode node, Long lowerExclusive, Long upperExclusive) {
        if (node == null) {
            return new Validation(0, 0L);
        }

        if ((lowerExclusive != null && node.key <= lowerExclusive)
                || (upperExclusive != null && node.key >= upperExclusive)) {
            throw new IllegalStateException(
                    "Invariante de ordenação inválido na key=" + node.key);
        }

        Validation left = validate(node.left, lowerExclusive, node.key);
        Validation right = validate(node.right, node.key, upperExclusive);

        int expectedHeight = 1 + Math.max(left.height(), right.height());
        if (node.height != expectedHeight) {
            throw new IllegalStateException(
                    "Invariante de altura inválido na key="
                            + node.key
                            + ": esperado="
                            + expectedHeight
                            + ", atual="
                            + node.height);
        }

        long expectedSize = 1L + left.size() + right.size();
        if (node.subtreeSize != expectedSize) {
            throw new IllegalStateException(
                    "Invariante de tamanho inválido na key="
                            + node.key
                            + ": esperado="
                            + expectedSize
                            + ", atual="
                            + node.subtreeSize);
        }

        int balance = left.height() - right.height();
        if (balance < -1 || balance > 1) {
            throw new IllegalStateException(
                    "Invariante de balanceamento inválido na key="
                            + node.key
                            + ": fator="
                            + balance);
        }

        return new Validation(expectedHeight, expectedSize);
    }

    private record Validation(int height, long size) {
    }
}
