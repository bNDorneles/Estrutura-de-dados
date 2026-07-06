# Plano do projeto

## Meta

Entregar até 16/07/2026 uma AVL aumentada correta, um estudo empírico
reproduzível e material suficiente para que os dois integrantes defendam as
decisões técnicas e os resultados.

## Cronograma

| Data | Resultado | Responsável |
| --- | --- | --- |
| 06/07 | documentação, backlog e estrutura Maven | `bNDorneles` |
| 07-08/07 | núcleo AVL e consultas aumentadas | `bNDorneles` |
| 07-08/07 | trace runner e BST de referência | `gustavodanjos` |
| 09/07 | integração, testes aleatórios e oráculo | ambos |
| 10/07 | benchmark, métricas e exportação CSV | `gustavodanjos` |
| 11/07 | validação das cargas e ensaio em pequena escala | ambos |
| 12-13/07 | matriz completa de experimentos | ambos |
| 14/07 | gráficos, interpretação e justificativa | ambos |
| 15/07 | revisão, apresentação e simulação da defesa | ambos |
| 16/07 | margem final e entrega | ambos |

## Divisão por módulos

### `bNDorneles`

- contrato e estrutura do projeto;
- AVL, rotações, inserção, remoção e busca;
- `rank`, `select` e `rangeCount`;
- validação dos invariantes;
- justificativa técnica.

### `gustavodanjos`

- parser e executor de trace;
- BST não balanceada;
- benchmark e cálculo de percentis;
- CSV e geração dos gráficos;
- metodologia experimental.

### Ambos

- revisão cruzada;
- integração com o oráculo;
- execução dos experimentos;
- interpretação, relatório e apresentação;
- organização dos prompts.

## Dependências

```text
Estrutura Maven
  |-- AVL -> consultas aumentadas -> testes de invariantes
  |-- TraceRunner ----------------------|
  |-- BST baseline -> benchmark --------|-> integração/oráculo
                                         -> experimentos
                                         -> relatório
                                         -> apresentação
```

## Fluxo Git

1. criar branch `feature/<issue>-<nome>` a partir de `develop`;
2. implementar apenas o escopo da issue;
3. executar os testes e registrar o comando no pull request;
4. abrir pull request para `develop`;
5. solicitar revisão do outro integrante;
6. integrar `develop` em `main` somente quando a versão estiver validada.

## Critérios de qualidade

- nenhuma medição entra no relatório antes de passar no oráculo;
- todo pull request de lógica possui testes;
- CSVs brutos são preservados;
- cada gráfico informa configuração e unidade;
- comandos e ambiente são documentados;
- nenhum número experimental é inventado ou extrapolado;
- ambos conseguem explicar o módulo do colega.

## Riscos

| Risco | Resposta |
| --- | --- |
| erro silencioso nos metadados | recomputação centralizada e verificador de invariantes |
| conflito no mesmo arquivo | divisão por módulos e pull requests pequenos |
| falta de memória | leitura em streaming e uso de `--max-load` |
| benchmark instável | aquecimento, repetições e registro do ambiente |
| atraso nos dados SOSD | começar com cargas sintéticas e baixar `face` em paralelo |
| colega ainda sem acesso | aceitar convite e receber permissão de escrita antes do primeiro push |
| defesa desigual | revisão cruzada e rodada de perguntas no dia 15/07 |

## Definição de pronto

- build e testes passam em ambiente limpo;
- trace do professor é processado no formato correto;
- oráculo não encontra divergências;
- matriz experimental obrigatória foi executada;
- gráficos são regeneráveis;
- documentação contém comandos completos;
- relatório, prompts e apresentação foram revisados pelos dois integrantes.
