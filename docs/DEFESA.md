# Preparacao para Defesa Oral

## Remocao

**Pergunta:** Como a AVL remove um no com dois filhos?

**Resposta:** A arvore substitui a chave pelo sucessor em ordem, remove o
sucessor da subarvore direita e rebalanceia no caminho de volta. Apos cada
alteracao, altura e tamanho de subarvore sao recalculados.

## Rotacoes

**Pergunta:** Por que recalcular o no que desce antes do no que sobe?

**Resposta:** Porque o no que sobe depende dos metadados atualizados do filho.
Se a ordem for invertida, altura e `subtreeSize` podem ficar inconsistentes.

## P99

**Pergunta:** Por que medir p99 alem da media?

**Resposta:** A media esconde caudas longas. O p99 mostra a latencia abaixo da
qual 99% das execucoes ficaram, evidenciando picos de custo por caminhos mais
longos, cache, alocacao ou pausas do sistema.

## Theta

**Pergunta:** O que o theta muda no trace?

**Resposta:** Theta controla a concentracao da distribuicao Zipfiana. Valores
maiores concentram acessos em poucas chaves, alterando localidade de cache e
frequencia de hits/remocoes em elementos quentes.

## Cache

**Pergunta:** Por que cache importa em arvores?

**Resposta:** Nos de arvores ficam espalhados na memoria. Percursos por ponteiros
podem causar faltas de cache. A distribuicao de acesso e a altura da arvore
mudam quantos nos sao visitados e com que localidade.

## Baseline

**Pergunta:** Por que comparar com BST nao balanceada?

**Resposta:** A BST mostra o que acontece sem rotacoes. Ela ajuda a separar o
custo do balanceamento do ganho em altura limitada, especialmente em entradas
ordenadas.

## Oraculo

**Pergunta:** Por que validar antes de medir?

**Resposta:** Medir uma estrutura incorreta nao tem valor experimental. O
oraculo garante que as respostas de busca refletem insercoes e remocoes
anteriores.
