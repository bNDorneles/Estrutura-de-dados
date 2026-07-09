# Roteiro de Apresentacao

Tempo alvo: 10 minutos.

## 1. Problema e Objetivo (1 min)

- Apresentar a necessidade de manter um conjunto ordenado de chaves de 64 bits.
- Explicar as operacoes: inserir, remover, buscar, `rank`, `select` e
  `rangeCount`.
- Dizer que a avaliacao compara AVL aumentada contra BST nao balanceada.

## 2. Estrutura da Solucao (2 min)

- `OrderedLongSet` define o contrato comum.
- `AugmentedAvlTree` implementa AVL com altura e tamanho de subarvore.
- `UnbalancedBst` e a linha de base.
- `TraceRunner` executa traces e produz respostas para o oraculo.
- `BenchmarkRunner` mede latencia media, p50 e p99.

## 3. Invariantes e Rotacoes (2 min)

- Invariante de ordenacao: esquerda menor, direita maior.
- Invariante de altura.
- Fator de balanceamento em `[-1, 1]`.
- Tamanho de subarvore correto.
- Rotacoes recalculam primeiro o no que desce e depois o no que sobe.

## 4. Metodologia Experimental (2 min)

- Geracao de traces pelo script do professor.
- Validacao obrigatoria pelo oraculo antes de aceitar resultados.
- Matriz com quatro ordens de grandeza, quatro thetas, `shuffle`/`sorted` e
  AVL/BST.
- Execucao nativa na maquina do grupo para evitar distorcao do Docker.

## 5. Resultados e Interpretacao (2 min)

- Mostrar graficos gerados a partir do CSV validado.
- Comparar escala, p50/p99, theta, ordem e baseline.
- Explicar que AVL paga custo local de rotacoes para evitar altura linear.
- Explicar que BST sofre principalmente em ordem `sorted`.

## 6. Fechamento (1 min)

- Reforcar corretude por testes, invariantes e oraculo.
- Reforcar reprodutibilidade por manifesto, comandos e README.
- Indicar que nenhum numero foi usado sem medicao validada.
