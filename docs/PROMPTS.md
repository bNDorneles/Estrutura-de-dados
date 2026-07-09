# Registro de Prompts

Este arquivo resume as decisoes tomadas com apoio de IA durante o projeto. Ele
deve ser mantido junto da entrega para indicar objetivo, decisao e arquivos
afetados.

| Data | Objetivo | Decisao | Arquivos afetados |
| --- | --- | --- | --- |
| 2026-07-06 | Estruturar projeto Maven e contrato comum | Separar API, AVL, trace, benchmark e docs para permitir PRs pequenos | `pom.xml`, `README.md`, `docs/ARQUITETURA.md`, `src/main/java/edu/unipampa/ed/api/OrderedLongSet.java` |
| 2026-07-06 | Implementar AVL aumentada | Manter metadados de altura e tamanho de subarvore recalculados em rotacoes | `src/main/java/edu/unipampa/ed/avl/*`, `src/test/java/edu/unipampa/ed/avl/*` |
| 2026-07-06 | Adicionar trace, BST e benchmark | Usar contrato comum para alternar entre AVL e BST | `src/main/java/edu/unipampa/ed/trace/*`, `src/main/java/edu/unipampa/ed/bst/*`, `src/main/java/edu/unipampa/ed/benchmark/*` |
| 2026-07-09 | Integrar oraculo de corretude | Criar fluxo que gera trace, executa AVL/BST e falha se o oraculo falhar | `scripts/run_oracle_check.py`, `scripts/test_run_oracle_check.py`, `README.md`, `docs/EXPERIMENTOS.md` |
| 2026-07-09 | Definir matriz experimental | Gerar manifesto e comandos reprodutiveis antes de coletar CSVs | `scripts/experiment_matrix.py`, `scripts/test_experiment_matrix.py`, `docs/EXPERIMENTOS.md` |
| 2026-07-09 | Preparar relatorio e defesa | Escrever fonte do relatorio sem numeros nao medidos e organizar defesa oral | `docs/RELATORIO.md`, `docs/APRESENTACAO.md`, `docs/DEFESA.md`, `docs/ENTREGA_FINAL.md` |

## Observacao

Os prompts auxiliaram organizacao, implementacao e revisao. As decisoes finais
foram revisadas no repositorio por meio de issues, commits e pull requests.
