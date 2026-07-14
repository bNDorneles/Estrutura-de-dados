# Checklist de Entrega Final

## Reproducao

- [x] README cobre clone, build, testes e ambiente.
- [x] README cobre geracao e validacao com oraculo.
- [x] README referencia matriz, benchmark e graficos.
- [x] Traces, expected, outputs, CSVs e plots ficam fora do Git.

## Corretude

- [x] Testes unitarios cobrem contrato, AVL, BST, trace e benchmark.
- [x] Oraculo valida AVL e BST antes de aceitar benchmark.
- [x] Invariantes AVL estao descritos em arquitetura e relatorio.

## Experimentos

- [x] Matriz cobre quatro ordens de grandeza.
- [x] Matriz usa theta `0.99`.
- [x] Matriz usa ordem `sorted`.
- [x] Matriz usa mix `45:30:25` e seed `14`.
- [x] Matriz cobre AVL e BST.
- [x] Timeout e falta de memoria devem ser registrados como falha, nao estimados.

## Relatorio e Defesa

- [x] Relatorio fonte criado em `docs/RELATORIO.md`.
- [x] Prompts registrados em `docs/PROMPTS.md`.
- [x] Roteiro de apresentacao criado em `docs/APRESENTACAO.md`.
- [x] Perguntas de defesa preparadas em `docs/DEFESA.md`.
- [ ] Revisao final pelos dois integrantes.
- [ ] Insercao dos graficos oficiais apos execucao nativa da matriz.

## Verificacao

Registrar antes da entrega:

```powershell
$env:JAVA_HOME='C:\Users\bNd\AppData\Local\Programs\DataGrip\jbr'
.\mvnw.cmd clean test package
python -m unittest discover scripts
```
