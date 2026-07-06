# Projeto Final de Estruturas de Dados

Implementação e avaliação empírica de uma **árvore AVL aumentada** para o
Grupo 2 da disciplina AL0334 - Estrutura de Dados.

## O que será construído

A estrutura armazenará chaves inteiras de 64 bits e oferecerá:

| Operação | Resultado | Complexidade esperada |
| --- | --- | --- |
| `insert(k)` | insere uma chave sem duplicá-la | `O(log n)` |
| `delete(k)` | remove uma chave, se existir | `O(log n)` |
| `search(k)` | informa se a chave existe | `O(log n)` |
| `rank(k)` | conta as chaves menores que `k` | `O(log n)` |
| `select(i)` | encontra a chave de índice ordenado `i` | `O(log n)` |
| `rangeCount(a, b)` | conta chaves no intervalo inclusivo `[a, b]` | `O(log n)` |

Cada nó guarda sua altura e o tamanho da subárvore. Esses metadados são
recalculados durante as rotações, permitindo balanceamento e consultas de
ordem eficientes.

## Configuração do Grupo 2

- dataset SOSD: `face`;
- `theta`: `0.9`;
- mistura `I:D:S`: `50:20:30`;
- agregado: contagem;
- ordem principal: `shuffle`;
- seed: `2`.

O programa principal será escrito em **Java 17**. O script Python fornecido
pelo professor será usado somente para gerar cargas e verificar respostas.

## Entregas

O trabalho inclui código, testes, estudo empírico, justificativa das decisões,
relatório, apresentação oral e registro organizado dos prompts utilizados.
Os números e gráficos do relatório deverão vir de execuções reais na máquina
do grupo.

## Organização prevista

```text
src/main/java/edu/unipampa/ed/
  api/          contrato comum das árvores
  avl/          árvore AVL aumentada
  baseline/     BST não balanceada para comparação
  trace/        leitura e execução dos traces
  benchmark/    medições e exportação CSV
src/test/java/edu/unipampa/ed/
scripts/        geração dos gráficos
docs/           arquitetura, plano, experimentos e relatório
```

## Como começar

O projeto ainda está na fase de planejamento. A implementação deverá seguir:

1. [Arquitetura](docs/ARQUITETURA.md);
2. [Plano do projeto](docs/PLANO_PROJETO.md);
3. [Plano detalhado de implementação](docs/superpowers/plans/2026-07-06-arvore-avl-aumentada.md).

## Fluxo de colaboração

- `main`: versão estável;
- `develop`: integração antes da `main`;
- branches de trabalho: `feature/<numero>-<descricao>`;
- cada issue deve resultar em um pull request pequeno para `develop`;
- o outro integrante revisa o pull request antes do merge.

Responsáveis:

- `bNDorneles`: núcleo AVL e invariantes;
- `gustavodanjos`: trace, baseline e benchmark;
- ambos: integração, experimentos, relatório e defesa.
