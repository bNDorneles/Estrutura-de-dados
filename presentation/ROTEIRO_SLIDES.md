# Roteiro dos Slides

Deck: `Grupo14-AVL-Aumentada.pptx`  
Duração alvo: 10 minutos  
Mensagem central: o balanceamento tem custo local pequeno, mas evita a
degradação estrutural que tornou a AVL 1.207 vezes mais rápida que a BST no
caso oficial.

## Slide 1 - AVL aumentada sob carga real

**Objetivo:** abrir com a tese, não apenas com o nome do projeto.  
**Conteúdo:** Grupo 14, dataset SOSD `face`, disciplina e comparação AVL x BST.  
**Visual:** título grande e resultado-chave em destaque.

## Slide 2 - O desafio combina ordem, atualização e consulta

**Objetivo:** explicar o problema recebido pelo grupo.  
**Conteúdo:** chaves de 64 bits; `insert`, `delete`, `search`, `rank`, `select`
e `rangeMin`; parâmetros `theta=0.99`, mix `45:30:25`, `sorted`, seed `14`.  
**Visual:** três blocos: carga, operações e risco da entrada ordenada.

## Slide 3 - Metadados transformam a AVL em índice ordenado

**Objetivo:** mostrar o desenho da solução.  
**Conteúdo:** cada nó guarda chave, altura e tamanho da subárvore; altura
sustenta o balanceamento e tamanho sustenta `rank`/`select`; `rangeMin` usa a
ordenação da árvore.  
**Visual:** diagrama simples de uma árvore com metadados em três nós.

## Slide 4 - Quatro invariantes sustentam a corretude

**Objetivo:** ligar implementação e argumento de corretude.  
**Conteúdo:** propriedade BST, altura correta, fator em `[-1,1]` e tamanho de
subárvore; rotações recalculam primeiro o nó que desce e depois o que sobe.  
**Visual:** sequência curta “alterar -> atualizar -> rotacionar -> validar”.

## Slide 5 - Só medimos traces aprovados pelo oráculo

**Objetivo:** estabelecer confiança nos números.  
**Conteúdo:** SOSD real, geração reproduzível, execução AVL/BST, comparação com
`.expected`, 3 warmups, 10 repetições, média/P50/P99, execução nativa.  
**Visual:** linha do tempo do pipeline experimental.

## Slide 6 - A matriz cobre escala, viés e caso patológico

**Objetivo:** resumir a cobertura empírica.  
**Conteúdo:** 64 medições; 4 escalas; 4 thetas; 2 ordens; 2 árvores; Windows 11,
JVM 21.0.9, 4.078 MB registrados pelo benchmark.  
**Visual:** três números grandes e uma linha de metodologia.

## Slide 7 - Em escala, a AVL permanece estável

**Objetivo:** apresentar o resultado principal do grupo.  
**Conteúdo:** gráfico oficial `theta=0.99`, `sorted`; AVL 137,7 ns e BST
166.278,6 ns em um milhão de operações; diferença de 1.207,5 vezes.  
**Visual:** `group14_official.png` ocupando a maior parte do slide.

## Slide 8 - A ordem sorted expõe a degeneração da BST

**Objetivo:** explicar a causa, não apenas a diferença.  
**Conteúdo:** com `shuffle`, ambas ficam na mesma ordem de grandeza; com
`sorted`, a BST vira uma cadeia e aproxima cada operação de `O(n)`; a AVL
mantém `O(log n)`.  
**Visual:** `order_comparison.png` e uma frase de interpretação.

## Slide 9 - Theta altera localidade; balanceamento decide o resultado

**Objetivo:** responder à análise de sensibilidade.  
**Conteúdo:** theta concentra acessos em chaves quentes e afeta cache; o efeito
é secundário diante da ordem; nenhum theta recupera a BST ordenada.  
**Visual:** `theta_sensitivity.png` com destaque em `0.99`.

## Slide 10 - O custo da rotação compra previsibilidade

**Objetivo:** fechar resolvendo a tese inicial.  
**Conteúdo:** 48 testes Java + 12 testes Python; 64 medições auditáveis;
corretude por invariantes e oráculo; resultado de 1.207 vezes no caso oficial.  
**Visual:** conclusão em três linhas e convite para perguntas técnicas.

