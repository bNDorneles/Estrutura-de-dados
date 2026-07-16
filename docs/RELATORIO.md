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
  `rangeMin`, agregado de intervalo exigido para o Grupo 14.

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
`rangeMin` podem retornar respostas erradas mesmo quando a busca simples
continua funcionando.

## 4. Metodologia Experimental

A matriz experimental e gerada por `scripts/experiment_matrix.py`. Ela cobre:

- quatro escalas: `1.000`, `10.000`, `100.000` e `1.000.000` operacoes;
- theta `0.0`, `0.6`, `0.99` e `1.2`;
- mistura `45:30:25`;
- ordens de insercao `shuffle` e `sorted`;
- seed `14`;
- comparacao entre `avl` e `bst`.

O produto cartesiano dessas configuracoes resulta em 64 medicoes. O recorte
oficial do Grupo 14 usa theta `0.99` e ordem `sorted`; as demais configuracoes
foram mantidas para analisar sensibilidade e fornecer contexto ao resultado.

Cada trace deve ser validado com `scripts/run_oracle_check.py` antes de qualquer
linha de benchmark ser aceita. O benchmark carrega o trace na memoria antes de
medir, executa aquecimento da JVM e repete a coleta. Em cada repeticao, divide o
tempo total pelo numero de comandos; media, P50 e P99 sao calculados sobre essas
medias por operacao, nao sobre operacoes individuais.

Os resultados oficiais devem ser coletados nativamente na maquina do grupo, nao
no Docker, para evitar interferencia da virtualizacao. O ambiente registrado
deve incluir sistema operacional, versao da JVM, memoria disponivel, aquecimento
e numero de repeticoes.

As medicoes finais foram executadas no Windows 11, com um AMD Ryzen 5 3600 de
6 nucleos e 12 processadores logicos, 16 GB de RAM e JVM 21.0.9. Cada caso usou
3 ciclos de aquecimento e 10 repeticoes medidas.

## 5. Resultados Medidos

Os graficos e tabelas desta secao foram derivados das 64 linhas de
`results/final/results.csv`, coletadas apos validacao pelo oraculo. Foram
avaliadas quatro escalas, quatro valores de theta, duas ordens de insercao e as
duas estruturas.

### Escala

A AVL permaneceu entre 106,2 ns e 298,2 ns na configuracao oficial. Em um
milhao de operacoes, registrou 137,7 ns de media. A BST passou de 301,2 ns em
mil operacoes para 166.278,6 ns em um milhao, confirmando a degradacao causada
pela altura linear.

### Ponto de Cruzamento

O recorte oficial evidencia quando o custo do desbalanceamento passa a dominar
o custo adicional das rotacoes:

| Operacoes | AVL (ns) | BST (ns) | Razao BST/AVL |
| ---: | ---: | ---: | ---: |
| 1.000 | 298,2 | 301,2 | 1,01 vezes |
| 10.000 | 135,5 | 1.313,0 | 9,69 vezes |
| 100.000 | 106,2 | 14.971,4 | 140,97 vezes |
| 1.000.000 | 137,7 | 166.278,6 | 1.207,54 vezes |

Com mil operacoes, as estruturas ficaram praticamente empatadas. A partir de
10 mil operacoes, a vantagem da AVL tornou-se clara e cresceu rapidamente com
a escala. Em `shuffle`, por outro lado, a BST foi aproximadamente 1,5 a 1,8
vezes mais rapida, mostrando o custo constante do balanceamento quando a
entrada nao produz uma arvore patologica.

### P50 e P99

Na configuracao oficial com um milhao de operacoes, a AVL obteve P50 de 138 ns
e P99 de 140 ns. A BST obteve P50 de 166.238 ns e P99 de 166.477 ns. Esses
percentis resumem as medias por operacao das 10 repeticoes; com o estimador por
posto mais proximo, o P99 equivale ao maior valor observado. A proximidade com
a mediana indica estabilidade entre repeticoes e reforca que a diferenca nao
foi causada por um unico pico.

### Theta

Os ensaios repetidos com theta em `0.0`, `0.6`, `0.99` e `1.2` mostraram efeito
secundario diante da ordem de insercao. O enviesamento altera a localidade de
cache, mas nao corrige a degeneracao da BST ordenada.

### Ordem de Insercao

Com `shuffle`, AVL e BST permaneceram na mesma ordem de grandeza. Com `sorted`,
a BST cresceu ate 166.278,6 ns, enquanto a AVL permaneceu em 137,7 ns. As
rotacoes preservaram altura logaritmica mesmo sob a entrada patologica.

### Baseline AVL vs BST

A BST evidencia o custo-beneficio do balanceamento: em cargas pequenas ou
embaralhadas ela pode ser competitiva por nao executar rotacoes. No caso
oficial de um milhao de operacoes, porem, a AVL foi aproximadamente 1.207,5 vezes
mais rapida.

## 6. Teoria versus Pratica

Na teoria, as operacoes principais da AVL custam `O(log n)`, enquanto a BST nao
balanceada pode chegar a `O(n)`. Na pratica, os resultados tambem dependem de
cache, padrao de acesso, alocacao de objetos, custo das rotacoes e estabilidade
da JVM durante a medicao.

Os resultados confirmam a teoria. O custo local de atualizar metadados e
rotacionar aparece nas menores escalas, mas permanece limitado. Na BST
ordenada, cada nova chave percorre uma cadeia crescente; por isso a latencia por
operacao aumenta junto com o numero de elementos.

Na implementacao avaliada, insercao e remocao da AVL percorrem a arvore
recursivamente e, no retorno, recalculam `height` e `subtreeSize`, verificam o
fator de balanceamento e executam rotacoes quando necessario. A BST usa
percursos iterativos e nao mantem esses metadados, o que explica sua vantagem
constante nos traces embaralhados. Esse custo adicional da AVL compra a
garantia de altura logaritmica: construir uma BST degenerada com uma sequencia
de insercoes ordenadas pode acumular custo `O(n^2)`, enquanto a AVL mantem custo
acumulado `O(n log n)` para as insercoes.

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
