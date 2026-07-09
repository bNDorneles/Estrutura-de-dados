# Metodologia de Experimentos

Este documento descreve a metodologia empregada na avaliação de desempenho das estruturas de dados (AVL vs Unbalanced BST) contidas neste repositório. O isolamento rigoroso dos testes garante que as medições representem de forma fidedigna os gargalos estruturais e computacionais da árvore.

## 1. Isolamento de I/O
Uma das métricas mais distorcidas em benchmarks empíricos é o overhead de leitura do disco (I/O) durante operações maciças. Para obtermos a **latência pura** das operações nas árvores:
1. O arquivo `.trace` inteiro é carregado prévia e integralmente para a **Memória RAM** na forma de uma estrutura de objetos de comandos (`char type, long key`).
2. O cronômetro (`System.nanoTime()`) só é iniciado **após** a finalização completa do carregamento do disco e do parser textual.

## 2. Fase de Aquecimento (Warmup)
Devido ao mecanismo do compilador *Just-In-Time* (JIT) do Java, as primeiras execuções de um bloco de código são mais lentas por operarem de forma interpretada ou pouco otimizada.
- O `BenchmarkRunner` processa todo o conjunto de comandos do dataset **repetidas vezes** (default: 3 ciclos de warmup) **antes** de começar as coletas formais.
- Os tempos coletados nesta fase são descartados, servindo exclusivamente para garantir que a JVM atinja sua temperatura operacional e otimize os caminhos das rotas da árvore para código de máquina (HotSpot).

## 3. Repetições e Agregação de Percentis
Para suavizar interferências do Sistema Operacional (Escalonador, Coleta de Lixo ou I/O concorrente no background):
- O ciclo medido é executado $N$ vezes (default: 10 repetições independentes).
- As medições em nanossegundos são agrupadas pelo coletor `LatencyStats`.
- Extraímos **três** medidas críticas para cada caso de teste (dataset x estrutura):
  - **Média (Mean)**: a latência geral de pico ao longo das baterias.
  - **P50 (Mediana)**: o valor central, excluindo picos de outliers causados pela CPU / OS.
  - **P99**: o pior caso probabilístico; indica a latência onde 99% das requisições foram mais rápidas. Crucial para mensurar o limite do sistema sob estresse severo ou longas coletas de Lixo (Garbage Collection).

## 4. Como Executar os Ensaios
### Passo 0: Validação com o Oráculo
Antes de coletar ou preservar qualquer CSV, valide o mesmo trace contra AVL e
BST usando o oráculo de corretude:

```bash
python scripts/run_oracle_check.py --synthetic 1000 --ops 1000 --out scratch/oracle-smoke
python scripts/run_oracle_check.py --keys datasets/face --format sosd --key-bytes 8 --ops 1000000 --out scratch/group2-face
```

Somente resultados cujas duas verificações terminem com `[OK]` podem entrar na
matriz experimental, nos gráficos e no relatório. Timeouts, falta de memória
ou divergências devem ser registrados como falhas de execução, nunca como
valores estimados.

### Passo 1: Matriz Obrigatória
Gere o manifesto e os comandos da matriz completa:

```bash
python scripts/experiment_matrix.py --keys datasets/face --format sosd --key-bytes 8 --outdir scratch/matrix-face
```

A matriz padrão cobre quatro ordens de grandeza (`1000`, `10000`, `100000`,
`1000000` operações), theta em `0.0`, `0.6`, `0.99` e `1.2`, ordens
`shuffle` e `sorted`, e as duas árvores (`avl` e `bst`). O arquivo
`scratch/matrix-face/manifest.json` preserva a configuração dos casos e
`scratch/matrix-face/commands.ps1` contém os comandos reproduzíveis.

Para ensaio rápido sem o dataset SOSD:

```bash
python scripts/experiment_matrix.py --synthetic 1000 --ops 1000,10000 --outdir scratch/matrix-smoke
```

### Passo 2: Execução do Coletor Java
Invoque o runner gerando as métricas para o CSV (`relatorio.csv`):
```bash
./mvnw clean install
./mvnw exec:java -Dexec.mainClass="edu.unipampa.ed.benchmark.BenchmarkRunner" -Dexec.args="--trace datasets/books_200M_uint32.trace --tree avl --out relatorio.csv"
```
Repita o procedimento alternando o trace e/ou a flag `--tree bst`.

### Passo 3: Geração dos Gráficos Python
Em um ambiente configurado com Python (ou usando o contêiner `dev` através do docker-compose):
```bash
python scripts/plot_results.py --input relatorio.csv --outdir plots/
```

Este script compilará automaticamente comparativos em imagens vetorizadas (`.png`) contendo Bar Charts para Baseline (P99 vs Média) e Line Charts para o comportamento assimptótico ($O(\log n)$) no eixo temporal.

## 5. Relatório

O texto-base do relatório esta em [`RELATORIO.md`](RELATORIO.md). Ele deve ser
atualizado somente com numeros e graficos derivados de CSVs validados pelo
oraculo.
