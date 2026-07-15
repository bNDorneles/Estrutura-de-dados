# Entrega final profissional e apresentacao

## Objetivo

Transformar o repositorio concluido do Grupo 14 em uma entrega academica
profissional, reproduzivel e pronta para avaliacao, incluindo resultados reais,
PowerPoint final e roteiros de apoio.

## Estrutura final

- Codigo Java, testes Maven e scripts Python permanecem versionados.
- `results/final/` recebe o CSV real de 64 medicoes e os seis graficos finais.
- `presentation/` recebe o PowerPoint, o roteiro dos slides e o roteiro oral.
- `docs/` mantem somente arquitetura, metodologia, relatorio, defesa, prompts e
  checklist de entrega.
- O README passa a descrever o projeto concluido, seus resultados e reproducao.

## Limpeza

Serao removidos da arvore final os PDFs do plano de ensino e do enunciado,
`docs/PLANO_PROJETO.md` e todo `docs/superpowers/`. Traces, dataset SOSD,
arquivos `.expected`, saidas de execucao e logs continuam ignorados.

## Apresentacao

O deck tera aproximadamente 10 slides e duracao alvo de 10 minutos:

1. titulo e tese;
2. problema e configuracao do Grupo 14;
3. estrutura AVL aumentada;
4. invariantes e rotacoes;
5. metodologia experimental;
6. corretude e oraculo;
7. desempenho em escala;
8. caso patologico `sorted`;
9. sensibilidade ao theta;
10. conclusoes e defesa.

Os graficos reais serao usados como evidencia visual. O roteiro oral trara tempo,
fala sugerida e transicoes; o roteiro dos slides registrara objetivo, conteudo e
visual de cada tela.

## Publicacao

Depois de validar Maven, testes Python, integridade do CSV, renderizacao do PPTX
e limpeza do Git, a branch sera publicada, um pull request sera aberto contra
`develop` e integrado.
