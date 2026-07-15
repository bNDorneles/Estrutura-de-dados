# Roteiro da Apresentação Oral

Tempo alvo: 10 minutos. O texto é uma base para ensaio; deve ser falado de
forma natural, sem leitura literal.

## Slide 1 - 0:00 a 0:40

“Nosso projeto avalia uma árvore AVL aumentada sob uma carga real do benchmark
SOSD. A pergunta central foi: quanto custa manter o balanceamento e o que
acontece quando abrimos mão dele? No caso oficial do Grupo 14, com inserção
ordenada e um milhão de operações, a AVL foi aproximadamente 1.207 vezes mais
rápida que a BST sem balanceamento. Vamos mostrar como chegamos a esse
resultado e por que ele faz sentido.”

**Transição:** “Primeiro, qual era exatamente o problema que precisávamos
resolver?”

## Slide 2 - 0:40 a 1:35

“Recebemos chaves reais de 64 bits do conjunto `face`. A carga mistura 45% de
inserções, 30% de remoções e 25% de buscas, com theta 0,99 e seed 14. Além das
operações básicas, a estrutura oferece rank, select e o agregado do nosso grupo,
que é o mínimo em intervalo. A ordem principal é sorted. Isso é importante
porque inserir chaves crescentes em uma BST comum produz o pior formato
possível: uma cadeia.”

**Transição:** “A solução precisava preservar ordem e, ao mesmo tempo, manter
informações adicionais.”

## Slide 3 - 1:35 a 2:30

“Cada nó da AVL armazena a chave, a altura e o tamanho da subárvore. A altura é
usada para detectar desequilíbrio e escolher rotações. O tamanho permite calcular
rank e select sem percorrer toda a árvore. O mínimo no intervalo aproveita a
ordenação para localizar a primeira chave válida em caminho logarítmico. Assim,
o aumento do nó não é decorativo: ele transforma a árvore em um índice ordenado
com consultas adicionais.”

**Transição:** “Esses metadados só são úteis se continuarem corretos depois de
cada atualização.”

## Slide 4 - 2:30 a 3:30

“Mantemos quatro invariantes. Primeiro, toda chave à esquerda é menor e toda
chave à direita é maior. Segundo, a altura armazenada corresponde aos filhos.
Terceiro, o fator de balanceamento fica entre menos um e um. Quarto, o tamanho
da subárvore corresponde ao número real de nós. Nas rotações, atualizamos
primeiro o nó que desce e depois o nó que sobe. Essa ordem evita propagar
metadados antigos. Os testes verificam esses invariantes após sequências de
inserção e remoção.”

**Transição:** “Depois de validar a estrutura isoladamente, validamos também
cada carga usada na medição.”

## Slide 5 - 3:30 a 4:35

“O script fornecido pelo professor gera o trace e um gabarito. Executamos o
mesmo trace na AVL e na BST e comparamos todas as buscas com esse gabarito. Se
houvesse qualquer divergência, aquela carga não poderia entrar no benchmark.
Somente depois do oráculo executamos três ciclos de aquecimento e dez repetições.
A leitura do arquivo fica fora da região medida. Registramos média, P50 e P99 em
execução nativa no Windows, evitando a interferência do Docker.”

**Transição:** “Essa metodologia foi aplicada a uma matriz ampla, não a um
único caso conveniente.”

## Slide 6 - 4:35 a 5:15

“A matriz final tem 64 linhas: quatro escalas, de mil a um milhão de operações;
quatro valores de theta; duas ordens de inserção; e duas estruturas. O CSV
versionado preserva todas as medições e os gráficos podem ser regenerados a
partir dele. Isso permite auditar tanto o resultado oficial quanto os testes de
sensibilidade.”

**Transição:** “O resultado oficial mostra com clareza o efeito do
balanceamento.”

## Slide 7 - 5:15 a 6:25

“Com theta 0,99 e ordem sorted, a AVL ficou em 137,7 nanossegundos por operação
na maior escala. A BST chegou a 166.278,6 nanossegundos. Isso representa uma
diferença de 1.207,5 vezes. O P99 foi 140 nanossegundos na AVL e 166.477 na BST.
Como P50 e P99 da BST ficam próximos, não estamos vendo um pico ocasional: o
caminho longo afeta a execução de forma persistente.”

**Transição:** “A comparação entre as ordens explica de onde vem essa
diferença.”

## Slide 8 - 6:25 a 7:35

“No painel shuffle, AVL e BST permanecem na mesma ordem de grandeza. A BST pode
até ser ligeiramente mais rápida em alguns pontos porque não paga rotações. No
painel sorted, porém, sua latência cresce quase proporcionalmente ao tamanho da
carga. A árvore vira uma lista encadeada: buscar ou inserir pode atravessar uma
grande parte dos nós. A AVL paga um custo local de rotação para impedir essa
degradação global.”

**Transição:** “Também variamos o theta para separar o efeito da distribuição
de acesso.”

## Slide 9 - 7:35 a 8:35

“Theta maior concentra operações em poucas chaves quentes. Isso altera
localidade de cache e repetição de caminhos. Em shuffle, observamos pequenas
mudanças de latência. Em sorted, a diferença estrutural continua dominante:
nenhum valor de theta recupera a BST degenerada. Portanto, o viés de acesso
importa, mas a forma da árvore importa muito mais nesse cenário.”

**Transição:** “Com isso, podemos voltar à pergunta que abriu a apresentação.”

## Slide 10 - 8:35 a 9:40

“O balanceamento não é gratuito: há atualização de altura, tamanho e rotações.
Mas esse custo compra previsibilidade. A implementação foi validada por 48
testes Java, 12 testes Python, invariantes estruturais e o oráculo do professor.
As 64 medições confirmam a teoria: a AVL mantém comportamento logarítmico,
enquanto a BST pode chegar ao comportamento linear por operação. No caso do
Grupo 14, isso significou ser 1.207 vezes mais rápida na maior escala.”

**Fechamento:** “A principal conclusão é que o custo local das rotações evita
uma degradação que cresce com a carga. Agora podemos responder perguntas sobre
remoção, rotações, P99, theta ou metodologia.”

## Perguntas prováveis da defesa

- **O que acontece se removermos uma rotação?** O fator de balanceamento deixa
  de ser restaurado; a altura pode crescer e invalidar a garantia `O(log n)`.
- **Por que atualizar primeiro o nó que desce?** O nó superior depende da altura
  e do tamanho já corrigidos do nó inferior.
- **Por que o P99 da BST cresce?** Os caminhos ficam longos para quase todas as
  operações, e não apenas para eventos raros.
- **Por que a BST às vezes vence no shuffle?** Ela evita o custo constante das
  rotações quando a ordem aleatória já produz uma altura razoável.
- **O theta corrige a BST sorted?** Não. Ele muda a frequência de acesso, mas
  não reconstrói a topologia degenerada.

