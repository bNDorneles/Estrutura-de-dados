# Árvore AVL Aumentada - Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implementar e avaliar uma árvore AVL aumentada em Java para a configuração do Grupo 2.

**Architecture:** A AVL e a BST de referência implementam um contrato comum. O processamento de trace, o benchmark e a geração de gráficos ficam em módulos separados, permitindo validar corretude antes de medir desempenho.

**Tech Stack:** Java 17, Maven, JUnit 5, Python 3 com NumPy e Matplotlib, gerador/oráculo fornecido pelo professor.

---

## Estrutura de arquivos

```text
pom.xml
src/main/java/edu/unipampa/ed/api/OrderedLongSet.java
src/main/java/edu/unipampa/ed/avl/AugmentedAvlTree.java
src/main/java/edu/unipampa/ed/baseline/UnbalancedBst.java
src/main/java/edu/unipampa/ed/trace/TraceRunner.java
src/main/java/edu/unipampa/ed/benchmark/LatencyStats.java
src/main/java/edu/unipampa/ed/benchmark/BenchmarkRunner.java
src/test/java/edu/unipampa/ed/avl/AugmentedAvlTreeTest.java
src/test/java/edu/unipampa/ed/avl/AvlInvariantTest.java
src/test/java/edu/unipampa/ed/baseline/UnbalancedBstTest.java
src/test/java/edu/unipampa/ed/trace/TraceRunnerTest.java
src/test/java/edu/unipampa/ed/benchmark/LatencyStatsTest.java
scripts/plot_results.py
docs/EXPERIMENTOS.md
docs/RELATORIO.md
docs/APRESENTACAO.md
docs/PROMPTS.md
```

### Task 1: Estrutura Maven e contrato comum

**Responsável:** `bNDorneles`

**Files:**
- Create: `pom.xml`
- Create: `Dockerfile`
- Create: `compose.yaml`
- Create: `.dockerignore`
- Create: `requirements-dev.txt`
- Create: `.mvn/wrapper/maven-wrapper.properties`
- Create: `mvnw`
- Create: `mvnw.cmd`
- Create: `src/main/java/edu/unipampa/ed/api/OrderedLongSet.java`
- Create: `src/test/java/edu/unipampa/ed/api/OrderedLongSetContractTest.java`

- [ ] **Step 1: Configurar build e ambiente reproduzível**

Configurar Java 17, JUnit Jupiter 5.10.2, Surefire 3.2.5 e
Exec Maven Plugin 3.2.0 no `pom.xml`. Definir
`edu.unipampa.ed.trace.TraceRunner` como classe principal do plugin de
execução. Gerar o Maven Wrapper fixado no Maven 3.9.16.

Criar uma imagem Docker com Java 17, Maven 3.9.16, Python, NumPy e Matplotlib.
O Compose deve montar o projeto em `/workspace` e manter o cache Maven em
volume nomeado. Excluir datasets, traces e saídas grandes do contexto.

- [ ] **Step 2: Criar um teste de contrato compilável**

Definir uma implementação mínima privada no teste e verificar duplicata e
remoção ausente:

```java
assertEquals(0, set.size());
set.insert(7);
set.insert(7);
assertEquals(1, set.size());
set.delete(99);
assertEquals(1, set.size());
assertTrue(set.search(7));
```

- [ ] **Step 3: Executar o teste antes do contrato**

Run: `mvn -q -Dtest=OrderedLongSetContractTest test`

Expected: `FAIL` porque `OrderedLongSet` ainda não existe.

- [ ] **Step 4: Criar o contrato**

```java
package edu.unipampa.ed.api;

public interface OrderedLongSet {
    void insert(long key);
    void delete(long key);
    boolean search(long key);
    long size();
}
```

- [ ] **Step 5: Verificar**

Run: `.\mvnw.cmd -q test`

Expected: `BUILD SUCCESS`.

- [ ] **Step 6: Verificar no Docker**

Run: `docker compose run --rm dev ./mvnw -q test`

Expected: `BUILD SUCCESS`.

- [ ] **Step 7: Commit**

```text
git add pom.xml .mvn mvnw mvnw.cmd Dockerfile compose.yaml .dockerignore requirements-dev.txt src README.md docs
git commit -m "build: configure reproducible Java environment"
```

### Task 2: Nós, metadados e rotações AVL

**Responsável:** `bNDorneles`

**Files:**
- Create: `src/main/java/edu/unipampa/ed/avl/AvlNode.java`
- Create: `src/main/java/edu/unipampa/ed/avl/AvlRotations.java`
- Create: `src/test/java/edu/unipampa/ed/avl/AvlRotationsTest.java`

- [ ] **Step 1: Testar as quatro rotações**

Montar, em árvores internas separadas, `30,20,10`, `10,20,30`, `30,10,20` e
`10,30,20`. Rebalancear a raiz e verificar chave `20`, filhos `10` e `30`,
altura `2` e tamanho `3`.

- [ ] **Step 2: Confirmar falha**

Run: `.\mvnw.cmd -q -Dtest=AvlRotationsTest test`

Expected: `FAIL` porque a classe não existe.

- [ ] **Step 3: Implementar metadados e rotações**

Usar um nó com visibilidade de pacote contendo `long key`, `int height`,
`long subtreeSize`, `left` e `right`. Centralizar a atualização:

```java
private static void recompute(Node node) {
    node.height = 1 + Math.max(height(node.left), height(node.right));
    node.subtreeSize = 1L + size(node.left) + size(node.right);
}
```

Em cada rotação, recomputar primeiro o nó que desce e depois o que sobe.
Implementar `rebalance(Node)` para os casos LL, RR, LR e RL.

- [ ] **Step 4: Verificar**

Run: `.\mvnw.cmd -q -Dtest=AvlRotationsTest test`

Expected: quatro testes aprovados.

- [ ] **Step 5: Commit**

```text
git add src/main/java/edu/unipampa/ed/avl src/test/java/edu/unipampa/ed/avl docs
git commit -m "feat: add AVL metadata and rotations"
```

### Task 3: Inserção e busca

**Responsável:** `bNDorneles`

**Files:**
- Modify: `src/main/java/edu/unipampa/ed/avl/AugmentedAvlTree.java`
- Modify: `src/test/java/edu/unipampa/ed/avl/AugmentedAvlTreeTest.java`

- [ ] **Step 1: Testar inserção, busca e duplicatas**

Verificar árvore vazia, chaves negativas, `Long.MIN_VALUE`, `Long.MAX_VALUE` e
que inserir `42` duas vezes mantém `size() == 1`.

- [ ] **Step 2: Confirmar falha**

Run: `mvn -q -Dtest=AugmentedAvlTreeTest test`

Expected: novos testes falham.

- [ ] **Step 3: Implementar**

`insert(Node,long)` retorna o mesmo nó ao encontrar chave igual; nos demais
casos, recursa, chama `recompute` e `rebalance`. `search` percorre
iterativamente até encontrar a chave ou `null`.

- [ ] **Step 4: Verificar**

Run: `mvn -q test`

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```text
git add src/main src/test
git commit -m "feat: implement AVL insertion and search"
```

### Task 4: Remoção AVL

**Responsável:** `bNDorneles`

**Files:**
- Modify: `src/main/java/edu/unipampa/ed/avl/AugmentedAvlTree.java`
- Modify: `src/test/java/edu/unipampa/ed/avl/AugmentedAvlTreeTest.java`

- [ ] **Step 1: Testar todos os formatos**

Cobrir remoção de folha, nó com filho esquerdo, nó com filho direito, nó com
dois filhos, raiz, chave ausente e árvore que exige rebalanceamento após
remoção.

- [ ] **Step 2: Confirmar falha**

Run: `mvn -q -Dtest=AugmentedAvlTreeTest test`

Expected: testes de remoção falham.

- [ ] **Step 3: Implementar**

Para dois filhos, copiar a chave do menor nó da subárvore direita e remover
esse sucessor. Ao retornar da recursão, se o nó não for nulo, executar
`recompute(node)` e `rebalance(node)`.

- [ ] **Step 4: Verificar**

Run: `mvn -q test`

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```text
git add src/main src/test
git commit -m "feat: implement AVL deletion"
```

### Task 5: Rank, select e contagem de intervalo

**Responsável:** `bNDorneles`

**Files:**
- Modify: `src/main/java/edu/unipampa/ed/avl/AugmentedAvlTree.java`
- Modify: `src/test/java/edu/unipampa/ed/avl/AugmentedAvlTreeTest.java`

- [ ] **Step 1: Testar consultas aumentadas**

Com `{10,20,30,40}`, verificar `rank(10)=0`, `rank(25)=2`,
`select(0)=10`, `select(3)=40`, `rangeCount(15,35)=2`,
`rangeCount(40,10)=0` e exceção para índices `-1` e `4`.

- [ ] **Step 2: Confirmar falha**

Run: `mvn -q -Dtest=AugmentedAvlTreeTest test`

Expected: métodos ausentes.

- [ ] **Step 3: Implementar**

`rank` e `select` devem caminhar pela árvore usando `subtreeSize`.
`rangeCount(a,b)` retorna zero para `a>b`; nos demais casos retorna
`countLessOrEqual(b) - rank(a)`. Evitar `b + 1`, pois `b` pode ser
`Long.MAX_VALUE`.

- [ ] **Step 4: Verificar**

Run: `mvn -q test`

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```text
git add src/main src/test
git commit -m "feat: add order statistics and range count"
```

### Task 6: Verificador de invariantes e teste aleatório

**Responsável:** `bNDorneles`

**Files:**
- Create: `src/test/java/edu/unipampa/ed/avl/AvlInvariantTest.java`
- Modify: `src/main/java/edu/unipampa/ed/avl/AugmentedAvlTree.java`

- [ ] **Step 1: Criar teste aleatório reproduzível**

Usar `new Random(2)` em 10.000 inserções, remoções e buscas sobre um universo
de 2.000 chaves. Comparar conteúdo, tamanho, rank e select com
`TreeSet<Long>` após cada bloco de 100 operações.

- [ ] **Step 2: Criar validação estrutural**

Um método de acesso de pacote `validateInvariantsForTesting()` deve verificar
ordenação, altura, fator AVL e `subtreeSize`, lançando
`IllegalStateException` com a chave do primeiro nó inválido.

- [ ] **Step 3: Verificar**

Run: `mvn -q -Dtest=AvlInvariantTest test`

Expected: teste aprovado com seed 2.

- [ ] **Step 4: Executar a suíte**

Run: `mvn -q test`

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```text
git add src/main src/test
git commit -m "test: validate AVL invariants against TreeSet"
```

### Task 7: Leitor e executor de trace em streaming

**Responsável:** `gustavodanjos`

**Files:**
- Create: `src/main/java/edu/unipampa/ed/trace/TraceRunner.java`
- Create: `src/test/java/edu/unipampa/ed/trace/TraceRunnerTest.java`

- [ ] **Step 1: Testar um trace em memória**

Entrada:

```text
# seed=2
I 10
I 20
S 10
D 10
S 10
```

Saída esperada:

```text
10 FOUND
10 NOT_FOUND
```

Também testar linha malformada e operação desconhecida com número da linha na
mensagem.

- [ ] **Step 2: Confirmar falha**

Run: `mvn -q -Dtest=TraceRunnerTest test`

Expected: classe ausente.

- [ ] **Step 3: Implementar**

Criar `run(Reader,Writer,OrderedLongSet)` usando `BufferedReader` e
`BufferedWriter`. Processar uma linha por vez e emitir saída apenas para `S`.
O `main` recebe `--trace`, `--out` e `--tree avl|bst`.

- [ ] **Step 4: Verificar**

Run: `mvn -q -Dtest=TraceRunnerTest test`

Expected: todos os casos aprovados.

- [ ] **Step 5: Commit**

```text
git add src/main/java/edu/unipampa/ed/trace src/test/java/edu/unipampa/ed/trace
git commit -m "feat: stream workload traces"
```

### Task 8: BST não balanceada de referência

**Responsável:** `gustavodanjos`

**Files:**
- Create: `src/main/java/edu/unipampa/ed/baseline/UnbalancedBst.java`
- Create: `src/test/java/edu/unipampa/ed/baseline/UnbalancedBstTest.java`

- [ ] **Step 1: Testar o contrato**

Aplicar à BST os mesmos casos de inserção, busca, remoção e duplicata usados no
contrato comum. Adicionar sequência ordenada `1..1000`.

- [ ] **Step 2: Confirmar falha**

Run: `mvn -q -Dtest=UnbalancedBstTest test`

Expected: classe ausente.

- [ ] **Step 3: Implementar iterativamente**

Implementar busca e inserção iterativas. Na remoção, localizar pai e alvo;
para dois filhos, substituir pelo sucessor e remover o sucessor. Manter
`long size` apenas quando a operação alterar o conjunto.

- [ ] **Step 4: Verificar**

Run: `mvn -q -Dtest=UnbalancedBstTest test`

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```text
git add src/main/java/edu/unipampa/ed/baseline src/test/java/edu/unipampa/ed/baseline
git commit -m "feat: add unbalanced BST baseline"
```

### Task 9: Métricas e exportação CSV

**Responsável:** `gustavodanjos`

**Files:**
- Create: `src/main/java/edu/unipampa/ed/benchmark/LatencyStats.java`
- Create: `src/main/java/edu/unipampa/ed/benchmark/BenchmarkRunner.java`
- Create: `src/test/java/edu/unipampa/ed/benchmark/LatencyStatsTest.java`

- [ ] **Step 1: Testar percentis**

Para latências `{10,20,30,40,50}`, verificar média `30`, p50 `30` e p99 `50`.
Para coleção vazia, exigir `IllegalArgumentException`.

- [ ] **Step 2: Confirmar falha**

Run: `mvn -q -Dtest=LatencyStatsTest test`

Expected: classe ausente.

- [ ] **Step 3: Implementar**

Armazenar latências em nanossegundos, ordenar uma cópia e usar o índice
`ceil(percentile * length) - 1`. O runner recebe trace, tipo de árvore, warmup,
repetições e caminho CSV. Registrar configuração, total, média, p50, p99,
quantidade final, JVM, SO e memória.

- [ ] **Step 4: Verificar**

Run: `mvn -q test`

Expected: `BUILD SUCCESS`.

- [ ] **Step 5: Commit**

```text
git add src/main/java/edu/unipampa/ed/benchmark src/test/java/edu/unipampa/ed/benchmark
git commit -m "feat: collect latency metrics and export CSV"
```

### Task 10: Gráficos e metodologia

**Responsável:** `gustavodanjos`

**Files:**
- Create: `scripts/plot_results.py`
- Create: `docs/EXPERIMENTOS.md`

- [ ] **Step 1: Definir o formato**

Documentar colunas CSV, warmup, número de repetições, controle do ambiente e
os comandos para escala, theta, ordem e baseline.

- [ ] **Step 2: Criar um CSV de teste**

Usar duas estruturas, quatro tamanhos e colunas `mean_ns`, `p50_ns` e
`p99_ns`.

- [ ] **Step 3: Implementar os gráficos**

O script recebe `--input`, `--output-dir` e gera PNGs com eixos, unidades,
legenda, título e escala logarítmica quando apropriado. Falhar com mensagem
clara se faltar uma coluna obrigatória.

- [ ] **Step 4: Verificar**

Run: `python scripts/plot_results.py --input resultados/teste.csv --output-dir resultados/graficos`

Expected: PNGs de escala, theta, ordem e baseline.

- [ ] **Step 5: Commit**

```text
git add scripts docs/EXPERIMENTOS.md
git commit -m "feat: document experiments and plot benchmark results"
```

### Task 11: Integração com o oráculo

**Responsável:** ambos

**Files:**
- Modify: `README.md`
- Create: `src/test/resources/traces/smoke.trace`

- [ ] **Step 1: Gerar carga reproduzível**

Run:

```text
python gen_workload_1.py generate --synthetic 10000 --out resultados/smoke --ops 100000 --universe 8000 --mix 50:20:30 --theta 0.9 --insert-order shuffle --seed 2
```

Expected: arquivos `.trace` e `.expected`.

- [ ] **Step 2: Executar a AVL**

Run: `mvn -q exec:java -Dexec.args="--trace resultados/smoke.trace --out resultados/smoke.out --tree avl"`

Expected: arquivo `.out` com uma linha por busca.

- [ ] **Step 3: Verificar**

Run: `python gen_workload_1.py verify --expected resultados/smoke.expected --candidate resultados/smoke.out`

Expected: `[OK]` e nenhuma divergência.

- [ ] **Step 4: Repetir com BST**

Usar `--tree bst` e exigir o mesmo resultado do oráculo.

- [ ] **Step 5: Commit**

```text
git add README.md src/test/resources
git commit -m "test: verify tree output with workload oracle"
```

### Task 12: Matriz experimental

**Responsável:** ambos

**Files:**
- Create: `resultados/README.md`
- Modify: `docs/EXPERIMENTOS.md`

- [ ] **Step 1: Registrar ambiente**

Salvar CPU, RAM, SO, `java -version`, Maven, Python e parâmetros de JVM.

- [ ] **Step 2: Executar escala**

Usar ao menos quatro ordens de grandeza, por exemplo `10^3`, `10^4`, `10^5`
e `10^6`, aumentando enquanto a máquina suportar.

- [ ] **Step 3: Executar sensibilidade**

Repetir com `theta` em `0.0`, `0.6`, `0.99` e `1.2`.

- [ ] **Step 4: Executar casos comparativos**

Comparar `shuffle` com `sorted` e AVL com BST. Registrar timeout ou falta de
memória como limitação, nunca como medição inventada.

- [ ] **Step 5: Validar e versionar**

Executar o oráculo antes de aceitar cada CSV. Versionar comandos, CSVs
consolidados e checksums; não versionar o dataset SOSD ou traces gigantes.

- [ ] **Step 6: Commit**

```text
git add docs/EXPERIMENTOS.md resultados/README.md resultados/*.csv
git commit -m "data: record reproducible benchmark results"
```

### Task 13: Relatório e justificativa

**Responsável:** ambos

**Files:**
- Create: `docs/RELATORIO.md`
- Modify: `docs/ARQUITETURA.md`

- [ ] **Step 1: Descrever metodologia**

Incluir máquina, compilador, aquecimento, repetições, geração de carga e
limitações.

- [ ] **Step 2: Inserir gráficos medidos**

Cobrir escala, p50/p99, theta, ordem e baseline com legendas completas.

- [ ] **Step 3: Interpretar**

Confrontar `O(log n)` com as curvas e discutir cache, alocação, rotações,
caudas de latência e ponto de cruzamento da BST.

- [ ] **Step 4: Justificar a AVL**

Explicar alternativas descartadas, os cinco invariantes e por que as rotações
preservam ordem, altura e contagem.

- [ ] **Step 5: Revisar autoria**

Identificar ferramentas utilizadas e garantir que nenhuma afirmação dependa de
um número não medido.

- [ ] **Step 6: Commit**

```text
git add docs/RELATORIO.md docs/ARQUITETURA.md
git commit -m "docs: analyze benchmark results and design choices"
```

### Task 14: Entrega, prompts e defesa

**Responsável:** ambos

**Files:**
- Modify: `README.md`
- Create: `docs/APRESENTACAO.md`
- Create: `docs/PROMPTS.md`

- [ ] **Step 1: Finalizar reprodução**

Documentar download de `face`, geração do trace, build, testes, execução,
oráculo, benchmark e gráficos.

- [ ] **Step 2: Organizar prompts**

Registrar data, ferramenta, objetivo, prompt, decisão tomada e arquivos
afetados para cada conversa utilizada.

- [ ] **Step 3: Preparar dez minutos**

Dividir em problema, estrutura, invariantes, metodologia, resultados,
interpretação e conclusão.

- [ ] **Step 4: Simular defesa**

Cada integrante deve responder sobre remoção de uma rotação, metadados,
complexidade, p99, theta, cache, baseline e reprodutibilidade.

- [ ] **Step 5: Verificação final**

Run: `mvn -q clean test package`

Expected: `BUILD SUCCESS`, oráculo aprovado e documentação sem comandos
quebrados.

- [ ] **Step 6: Commit**

```text
git add README.md docs/APRESENTACAO.md docs/PROMPTS.md
git commit -m "docs: prepare reproducible delivery and oral defense"
```
