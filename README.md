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

## Materiais fornecidos pelo professor

- [Enunciado do projeto](projeto_final_estruturas_de_dados.pdf);
- [Plano de ensino da disciplina](AL0334.pdf);
- [Gerador de carga e oráculo](gen_workload_1.py).

Os PDFs são a fonte dos requisitos. O gerador cria arquivos `.trace` e
`.expected`; esses arquivos podem ser muito grandes e, por isso, não devem ser
enviados ao Git.

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

### Opção recomendada: Docker

Para desenvolver e executar testes, instale apenas:

- Git;
- Docker Desktop com Docker Compose.

Depois:

```bash
git clone https://github.com/bNDorneles/Estrutura-de-dados.git
cd Estrutura-de-dados
git switch develop
docker compose build
docker compose run --rm dev ./mvnw test
```

O container contém Java 17, Maven 3.9.16, Python, NumPy e Matplotlib. O código
fica montado em `/workspace` e as dependências Maven usam um volume persistente.

### Opção nativa

Para trabalhar sem Docker, instale JDK 17 e Python 3. O Maven global é
opcional porque o repositório possui Maven Wrapper:

```powershell
.\mvnw.cmd clean test
python -m pip install -r requirements-dev.txt
```

No Linux ou macOS:

```bash
./mvnw clean test
python3 -m pip install -r requirements-dev.txt
```

### Importante sobre os benchmarks

Docker serve para desenvolvimento e testes reproduzíveis. As medições usadas
no relatório final devem rodar nativamente, fora do container, porque a camada
de virtualização no Windows pode alterar disco, memória, cache e latência. A
máquina, a JVM e a metodologia devem ser registradas no relatório.

### Oráculo de corretude

Antes de usar qualquer medição nos experimentos, gere a carga, execute AVL e
BST e valide as duas saídas com o oráculo do professor. Para uma checagem
rápida sem o dataset SOSD:

```powershell
python scripts/run_oracle_check.py --synthetic 1000 --ops 1000 --out scratch/oracle-smoke
```

Para a carga do Grupo 2 com o dataset `face`, use os parâmetros padrão do
script: mistura `50:20:30`, theta `0.9`, ordem `shuffle` e seed `2`.

```powershell
python scripts/run_oracle_check.py --keys datasets/face --format sosd --key-bytes 8 --ops 1000000 --out scratch/group2-face
```

O script gera `.trace` e `.expected`, executa `TraceRunner` com `--tree avl` e
`--tree bst`, e chama `gen_workload_1.py verify` para cada saída. Se qualquer
etapa falhar ou o oráculo retornar `[FALHA]`, a medição não deve ser usada.

O desenvolvimento deverá seguir:

1. [Arquitetura](docs/ARQUITETURA.md);
2. [Plano do projeto](docs/PLANO_PROJETO.md);
3. [Plano detalhado de implementação](docs/superpowers/plans/2026-07-06-arvore-avl-aumentada.md).

## Fluxo de colaboração

- `main`: versão estável;
- `develop`: integração antes da `main`;
- branches de trabalho: `feature/<numero>-<descricao>`;
- cada issue deve resultar em um pull request pequeno para `develop`;
- o outro integrante revisa o pull request antes do merge.

Para começar uma issue, atualize `develop` e crie uma branch:

```bash
git switch develop
git pull
git switch -c feature/NUMERO-descricao
```

Responsáveis:

- `bNDorneles`: núcleo AVL e invariantes;
- `gustavodanjos`: trace, baseline e benchmark;
- ambos: integração, experimentos, relatório e defesa.
