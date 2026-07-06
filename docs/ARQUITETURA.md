# Arquitetura da solução

## Visão geral

O projeto separa a estrutura de dados do pipeline experimental. A AVL e a BST
implementam o mesmo contrato, permitindo que o executor de trace e o benchmark
troquem de implementação sem conhecer detalhes internos.

```text
Trace (.trace) -> TraceRunner -> OrderedLongSet
                                  |-- AugmentedAvlTree
                                  `-- UnbalancedBst

BenchmarkRunner -> MetricsCollector -> CSV -> plot_results.py -> gráficos
```

## Contrato da árvore

`OrderedLongSet` define:

```java
void insert(long key);
void delete(long key);
boolean search(long key);
long size();
```

`AugmentedAvlTree` acrescenta:

```java
long rank(long key);
long select(long index);
long rangeCount(long lowerInclusive, long upperInclusive);
```

As chaves são únicas. Inserir uma duplicata ou remover uma chave ausente não
altera tamanho, altura ou conteúdo.

## Nó AVL

Cada nó contém:

```text
key: long
height: int
subtreeSize: long
left: Node
right: Node
```

`subtreeSize` é o aumento exigido pelo Grupo 2. Ele permite:

- `rank(k)`: acumular tamanhos de subárvores ignoradas durante a busca;
- `select(i)`: decidir se o índice está à esquerda, no nó ou à direita;
- `rangeCount(a,b)`: `countLessOrEqual(b) - rank(a)`.

## Invariantes

Após cada atualização:

1. esquerda contém apenas chaves menores;
2. direita contém apenas chaves maiores;
3. `height = 1 + max(height(left), height(right))`;
4. o fator de balanceamento pertence a `[-1, 1]`;
5. `subtreeSize = 1 + size(left) + size(right)`.

Nas rotações, o nó que desce deve ser recomputado antes do nó que sobe.

## Operações e erros

- `select` usa índice baseado em zero e lança
  `IndexOutOfBoundsException` para índice negativo ou maior ou igual ao
  tamanho;
- `rangeCount(a,b)` devolve zero quando `a > b`;
- o leitor ignora linhas vazias e comentários iniciados por `#`;
- operação desconhecida, chave inválida ou quantidade incorreta de campos
  produz erro com o número da linha;
- somente operações `S` geram saída para o oráculo.

## Corretude

Os testes validam operações isoladas, quatro tipos de rotação, formas de
remoção, consultas aumentadas e os cinco invariantes. Sequências aleatórias
serão comparadas com `TreeSet<Long>`. Um trace pequeno também será executado e
comparado pelo `gen_workload_1.py verify`.

## Benchmark

O benchmark separa o tempo das operações do custo de leitura e escrita sempre
que possível. Após aquecimento, registra total, média, p50 e p99, além da
configuração e do ambiente. Resultados brutos ficam em CSV e os gráficos são
derivados desses arquivos.

O estudo obrigatório compara:

- pelo menos quatro ordens de grandeza;
- `theta` em `0.0`, `0.6`, `0.99` e `1.2`;
- `shuffle` e `sorted`;
- AVL e BST não balanceada.
