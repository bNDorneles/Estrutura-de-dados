# Graficos finais dos experimentos do Grupo 14

## Objetivo

Gerar visualizacoes adicionais a partir do `results.csv` ja coletado, sem
executar novamente os benchmarks. Os graficos devem separar os efeitos de
escala, ordem de insercao e enviesamento Zipfiano e destacar a configuracao
oficial do Grupo 14.

## Abordagem

O gerador existente `scripts/plot_results.py` sera ampliado. Ele extraira do
campo `Configuracao` a estrutura (`avl` ou `bst`), o `theta` e a ordem de
insercao (`shuffle` ou `sorted`). Os dois graficos atuais serao preservados.

## Novas saidas

- `order_comparison.png`: AVL e BST por escala, separadas por ordem.
- `theta_sensitivity.png`: latencia media por theta e estrutura.
- `percentiles_by_scale.png`: media, P50 e P99 por escala e estrutura.
- `group14_official.png`: configuracao oficial theta 0.99 e ordem sorted.

Os graficos de escala usarao eixo logaritmico para operacoes e latencia quando
necessario, evitando que o caso patologico da BST esconda as curvas da AVL.

## Validacao

Os testes criarao um CSV pequeno com nomes no formato real da matriz e
confirmarao a criacao das seis imagens. Depois, o gerador sera executado sobre
o CSV final de 64 linhas e os PNGs serao inspecionados.
