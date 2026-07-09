# Relatorio Tecnico: Arvore AVL Aumentada

## 1. Objetivo

Este trabalho implementa e avalia uma arvore AVL aumentada para chaves inteiras
de 64 bits. A estrutura suporta insercao, remocao, busca e consultas baseadas
em ordem por meio do tamanho de subarvore armazenado em cada no.

O estudo compara a AVL com uma BST nao balanceada, usando traces gerados pelo
oraculo fornecido pela disciplina. Nenhum resultado numerico deve ser
transcrito para este relatorio sem que o trace correspondente tenha passado na
validacao do oraculo para AVL e BST.

## 2. Implementacao

O contrato comum esta em `OrderedLongSet`, permitindo que `TraceRunner` e
`BenchmarkRunner` alternem entre as implementacoes sem depender dos detalhes
internos de cada arvore.

A AVL foi dividida em tres responsabilidades:

- `AvlNode`: armazena chave, altura, tamanho da subarvore e referencias;
- `AvlRotations`: recalcula metadados, calcula fator de balanceamento e aplica
  rotacoes;
- `AugmentedAvlTree`: expoe insercao, remocao, busca, `rank`, `select` e
  `rangeCount`.

A BST nao balanceada serve como linha de base. Ela permite observar o custo de
uma estrutura sem rotacoes, principalmente em traces ordenados.

## 3. Invariantes

A corretude da AVL depende de cinco invariantes apos cada atualizacao:

1. toda chave da subarvore esquerda e menor que a chave do no;
2. toda chave da subarvore direita e maior que a chave do no;
3. `height = 1 + max(height(left), height(right))`;
4. o fator de balanceamento pertence ao intervalo `[-1, 1]`;
5. `subtreeSize = 1 + size(left) + size(right)`.

Nas rotacoes, o no que desce deve ser recalculado antes do no que sobe. Essa
ordem preserva tanto a altura quanto o tamanho da subarvore, que sao usados em
consultas de ordem. Se `subtreeSize` ficar incorreto, `rank`, `select` e
`rangeCount` podem retornar respostas erradas mesmo quando a busca simples
continua funcionando.

## 4. Metodologia Experimental

A matriz experimental e gerada por `scripts/experiment_matrix.py`. Ela cobre:

- quatro ordens de grandeza de operacoes;
- theta `0.0`, `0.6`, `0.99` e `1.2`;
- ordem de insercao `shuffle` e `sorted`;
- comparacao entre `avl` e `bst`.

Cada trace deve ser validado com `scripts/run_oracle_check.py` antes de qualquer
linha de benchmark ser aceita. O benchmark carrega o trace na memoria antes de
medir, executa aquecimento da JVM e repete a coleta para calcular media, p50 e
p99.

Os resultados oficiais devem ser coletados nativamente na maquina do grupo, nao
no Docker, para evitar interferencia da virtualizacao. O ambiente registrado
deve incluir sistema operacional, versao da JVM, memoria disponivel, aquecimento
e numero de repeticoes.

## 5. Resultados Medidos

Os graficos e tabelas desta secao devem ser derivados de `results.csv`, gerado
apos a validacao pelo oraculo. Os arquivos brutos ficam fora do Git por tamanho
e reprodutibilidade.

### Escala

Comparar a latencia media conforme o numero de operacoes cresce. A expectativa
teorica e que a AVL mantenha comportamento logaritmico, enquanto a BST pode
degradar fortemente em entradas ordenadas.

### P50 e P99

Comparar mediana e cauda. O p99 evidencia picos causados por caminhos longos,
rotacoes, alocacao, cache e interferencias do sistema operacional.

### Theta

Comparar distribuicoes uniformes e concentradas. Thetas maiores concentram
acessos em poucas chaves, mudando a localidade de cache e a pressao sobre
remocoes e buscas repetidas.

### Ordem de Insercao

Comparar `shuffle` e `sorted`. A ordem ordenada e patologica para BST nao
balanceada, mas a AVL deve preservar altura limitada por meio das rotacoes.

### Baseline AVL vs BST

A BST e usada como baseline para evidenciar o custo-beneficio do balanceamento:
rotacoes adicionam trabalho local, mas evitam crescimento linear da altura.

## 6. Teoria versus Pratica

Na teoria, as operacoes principais da AVL custam `O(log n)`, enquanto a BST nao
balanceada pode chegar a `O(n)`. Na pratica, os resultados tambem dependem de
cache, padrao de acesso, alocacao de objetos, custo das rotacoes e estabilidade
da JVM durante a medicao.

Quando a matriz oficial for executada, a interpretacao deve confrontar cada
grafico com essas expectativas. Divergencias devem ser explicadas a partir do
ambiente, do trace, do aquecimento ou de caracteristicas da implementacao, nunca
por extrapolacao sem medicao.

## 7. Alternativas Descartadas

- Usar apenas testes unitarios: insuficiente para comparar desempenho empirico.
- Medir dentro do Docker: adequado para desenvolvimento, mas inadequado para os
  numeros finais por causa da camada de virtualizacao.
- Medir sem oraculo: arriscaria comparar estruturas incorretas.
- Registrar somente media: esconderia caudas longas observadas pelo p99.
- Usar apenas AVL: removeria a linha de base necessaria para interpretar o
  custo do balanceamento.

## 8. Autoria e Ferramentas

Responsaveis:

- `bNDorneles`: nucleo AVL, invariantes, integracao e justificativa tecnica;
- `gustavodanjos`: trace, baseline BST, benchmark, CSV e graficos;
- ambos: matriz experimental, interpretacao, relatorio e defesa.

Ferramentas:

- Java 17 e Maven Wrapper;
- Python 3, NumPy, pandas e Matplotlib;
- `gen_workload_1.py` para gerar traces e validar o oraculo;
- GitHub issues e pull requests para rastrear as entregas;
- Codex para apoio na organizacao, implementacao e revisao.
