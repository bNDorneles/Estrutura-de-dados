package edu.unipampa.ed.api;

/**
 * Contrato comum para conjuntos ordenados de chaves {@code long}.
 *
 * <p>As implementações não armazenam chaves duplicadas. Inserir uma chave já
 * presente ou remover uma chave ausente não altera o conjunto.</p>
 */
public interface OrderedLongSet {

    /**
     * Insere a chave quando ela ainda não está presente.
     *
     * @param key chave a inserir
     */
    void insert(long key);

    /**
     * Remove a chave quando ela está presente.
     *
     * @param key chave a remover
     */
    void delete(long key);

    /**
     * Verifica se uma chave pertence ao conjunto.
     *
     * @param key chave procurada
     * @return {@code true} quando a chave está presente
     */
    boolean search(long key);

    /**
     * Retorna a quantidade de chaves distintas armazenadas.
     *
     * @return tamanho atual do conjunto
     */
    long size();
}
