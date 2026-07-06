# Projeto Final - Arvore AVL Aumentada

## 1. Objetivo

Construir, em Java, uma arvore binaria de busca balanceada e aumentada capaz de
executar insercao, remocao, busca, rank, select e contagem de chaves em um
intervalo. A solucao sera validada com o oraculo fornecido pelo professor e
avaliada empiricamente em cargas derivadas do conjunto SOSD `face`.

O projeto deve produzir quatro entregas principais:

1. codigo-fonte compilavel e instrucoes de reproducao;
2. relatorio empirico com graficos e interpretacao;
3. justificativa das decisoes e dos invariantes;
4. apresentacao oral de aproximadamente dez minutos.

O grupo tambem deve manter um registro organizado dos prompts utilizados.

## 2. Parametros do grupo 2

| Parametro | Valor |
| --- | --- |
| Conjunto SOSD | `face` |
| Distribuicao Zipfiana principal | `theta = 0.9` |
| Mistura de operacoes | `50:20:30` para insercao, remocao e busca |
| Agregado de intervalo | contagem |
| Ordem principal de insercao | `shuffle` |
| Seed reproduzivel | `2` |

O estudo de sensibilidade tambem executara `theta` em `0.0`, `0.6`, `0.99` e
`1.2`. O caso patologico comparara `shuffle` e `sorted`.

## 3. Escolhas tecnicas

### Linguagem e ferramentas

- Java 17;
- Maven para compilacao, execucao e testes;
- Maven Wrapper para padronizar a versao sem exigir Maven global;
- Docker Compose com Java, Maven e Python para desenvolvimento reproduzivel;
- JUnit 5 para testes unitarios e de integracao;
- script Python separado somente para transformar CSVs em graficos;
- `gen_workload_1.py` como gerador de traces e oraculo de corretude.

Python nao fara parte da implementacao da estrutura de dados. Ele sera usado
apenas no pipeline auxiliar ja iniciado pelo professor e na visualizacao das
medicoes. O Docker sera usado em desenvolvimento e testes; as medicoes finais
serao executadas nativamente para evitar interferencia da virtualizacao.

### Estrutura escolhida

A implementacao usara uma arvore AVL. Cada no armazenara:

- chave `long`;
- referencias para os filhos esquerdo e direito;
- altura;
- quantidade de nos da subarvore.

O tamanho da subarvore e o campo aumentado do grupo 2. Ele atende tanto
`rank` e `select` quanto o agregado de contagem, sem introduzir um segundo
campo redundante.

### Semantica das operacoes

- `insert(k)`: insere `k`; uma chave duplicada nao altera a arvore;
- `delete(k)`: remove `k`; uma chave ausente nao altera a arvore;
- `search(k)`: informa se `k` esta presente;
- `rank(k)`: devolve o numero de chaves estritamente menores que `k`;
- `select(i)`: devolve a chave de indice `i` na ordem crescente, usando indice
  baseado em zero;
- `rangeCount(a, b)`: devolve a quantidade de chaves no intervalo inclusivo
  `[a, b]`; se `a > b`, devolve zero.

`rangeCount(a, b)` sera calculado pela diferenca entre a quantidade de chaves
menores ou iguais a `b` e a quantidade de chaves menores que `a`. Assim, a
consulta permanece em `O(log n)`.

## 4. Invariantes

Apos cada operacao de atualizacao:

1. todas as chaves da subarvore esquerda sao menores que a chave do no;
2. todas as chaves da subarvore direita sao maiores que a chave do no;
3. a altura armazenada e `1 + max(altura(esquerda), altura(direita))`;
4. o fator de balanceamento de cada no esta entre `-1` e `1`;
5. `subtreeSize` e `1 + size(esquerda) + size(direita)`.

As rotacoes simples e duplas devem recomputar altura e tamanho primeiro no no
que desce e depois no no que sobe. Essa ordem evita metadados calculados a
partir de valores desatualizados.

## 5. Componentes

### Nucleo AVL

Responsavel por nos, busca, insercao, remocao, rotacoes, rebalanceamento e
manutencao dos metadados. Sua API publica nao expoe nos internos.

### Consultas aumentadas

Implementa `rank`, `select` e `rangeCount` usando `subtreeSize`. Indices
invalidos em `select` geram uma excecao documentada.

### Leitor e executor de trace

Le operacoes `I`, `D` e `S` em fluxo, sem carregar o trace inteiro na memoria.
Para cada `S`, escreve exatamente:

```text
<chave> <FOUND|NOT_FOUND>
```

Comentarios iniciados por `#` e linhas vazias sao ignorados. Entradas
malformadas encerram a execucao com numero de linha e mensagem de erro.

### Linha de base

Uma BST deliberadamente nao balanceada implementara insercao, remocao e busca.
Ela sera usada somente nos experimentos comparativos. A execucao devera
registrar quando a BST nao suportar uma escala por limite de tempo ou memoria,
sem fabricar resultados.

### Benchmark

O benchmark executara as cargas apos aquecimento e registrara:

- total de operacoes;
- tempo total;
- tempo medio por operacao;
- percentis p50 e p99;
- quantidade final de chaves;
- configuracao da carga;
- Java, sistema operacional, processador e memoria da maquina.

Os resultados brutos serao salvos em CSV. Graficos serao gerados a partir
desses arquivos, preservando os dados originais para auditoria.

## 6. Validacao

### Testes automatizados

- casos unitarios de cada operacao;
- quatro casos de rotacao AVL;
- remocao de folha, no com um filho e no com dois filhos;
- duplicatas e remocao ausente;
- limites de `rank`, `select` e `rangeCount`;
- validacao recursiva dos cinco invariantes;
- comparacao aleatoria contra `TreeSet<Long>`;
- execucao de um trace pequeno e verificacao com o oraculo.

### Corretude em larga escala

Antes de medir desempenho, cada configuracao deve gerar uma saida candidata e
passar em:

```text
python gen_workload_1.py verify --expected <arquivo.expected> --candidate <arquivo.out>
```

Uma carga que falhar no oraculo nao pode gerar dados aceitos no relatorio.

## 7. Estudo empirico

O estudo cobrirá:

1. pelo menos quatro ordens de grandeza de `n`, ate o limite real da maquina;
2. `theta` igual a `0.0`, `0.6`, `0.99` e `1.2`;
3. insercao `shuffle` contra `sorted`;
4. AVL contra BST nao balanceada;
5. confronto das curvas com os limites teoricos;
6. interpretacao de cache, alocacao, rotacoes e caudas de latencia.

Cada experimento tera comando, seed, ambiente e arquivos de saida registrados.
Os numeros do relatorio serao exclusivamente resultados medidos pelo grupo.

## 8. Divisao entre os desenvolvedores

### `bNDorneles`

- estrutura do projeto e API;
- nucleo AVL;
- insercao, remocao, rotacoes e rebalanceamento;
- consultas aumentadas;
- testes dos invariantes;
- lideranca da justificativa tecnica.

### `gustavodanjos`

- leitor e executor de trace;
- BST de linha de base;
- infraestrutura de benchmark;
- exportacao de CSV e geracao dos graficos;
- testes de integracao com o oraculo;
- lideranca da metodologia experimental.

### Responsabilidade compartilhada

- revisao cruzada de pull requests;
- execucao da matriz de experimentos;
- interpretacao dos resultados;
- relatorio final;
- apresentacao e preparacao para perguntas;
- organizacao do dump de prompts.

A divisao evita edicoes simultaneas frequentes nos mesmos arquivos. Ambos
precisam revisar a parte do colega porque a defesa oral pode perguntar sobre
qualquer componente.

## 9. Documentacao final

- `README.md`: visao geral, requisitos, compilacao, testes e execucao;
- `docs/ARQUITETURA.md`: invariantes, operacoes e decisoes;
- `docs/PLANO_PROJETO.md`: fases, responsaveis, dependencias, riscos e prazos;
- `docs/EXPERIMENTOS.md`: metodologia e comandos reproduziveis;
- `docs/RELATORIO.md`: graficos e interpretacao;
- `docs/APRESENTACAO.md`: roteiro de dez minutos e perguntas provaveis;
- `docs/PROMPTS.md`: indice organizado dos chats e ferramentas utilizados.

## 10. Riscos e mitigacoes

| Risco | Mitigacao |
| --- | --- |
| Metadado incorreto apos rotacao | funcao unica de recomputacao e testes de invariantes |
| Remocao AVL defeituosa | casos unitarios por formato de no e testes aleatorios |
| Trace grande demais para memoria | leitura e escrita em streaming |
| Benchmark contaminado por I/O | separar tempo da estrutura e tempo total do pipeline |
| Dataset maior que a maquina | usar `--max-load` e documentar o limite real |
| Resultado irreproduzivel | seed 2, comandos, ambiente e CSVs versionados |
| Conflitos entre desenvolvedores | modulos separados e pull requests pequenos |
| Defesa oral desigual | revisao cruzada e ensaio com perguntas sobre ambos os modulos |

## 11. Criterios de conclusao

O projeto esta pronto quando:

- todas as operacoes publicas estao implementadas e testadas;
- os invariantes permanecem validos apos testes deterministas e aleatorios;
- a saida dos traces confere com o oraculo;
- os experimentos obrigatorios foram executados na maquina do grupo;
- graficos podem ser regenerados a partir dos CSVs;
- README e documentos permitem reproducao;
- os dois integrantes conseguem explicar a estrutura, as medicoes e as
  decisoes de projeto.
