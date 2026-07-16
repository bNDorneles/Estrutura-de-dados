# Checklist de Entrega Final

## Reproducao

- [x] README cobre clone, build, testes e ambiente.
- [x] README cobre geracao e validacao com oraculo.
- [x] README referencia matriz, benchmark e graficos.
- [x] Traces, expected, outputs, datasets e logs ficam fora do Git.
- [x] CSV final e seis plots reais ficam versionados em `results/final/`.

## Corretude

- [x] Testes unitarios cobrem contrato, AVL, BST, trace e benchmark.
- [x] Oraculo valida AVL e BST antes de aceitar benchmark.
- [x] Invariantes AVL estao descritos em arquitetura e relatorio.

## Experimentos

- [x] Matriz cobre quatro ordens de grandeza.
- [x] Matriz cobre theta `0.0`, `0.6`, `0.99` e `1.2`.
- [x] Matriz cobre `shuffle` e `sorted`.
- [x] Matriz usa mix `45:30:25` e seed `14`.
- [x] Matriz cobre AVL e BST.
- [x] Timeout e falta de memoria devem ser registrados como falha, nao estimados.

## Relatorio e Defesa

- [x] Relatorio fonte criado em `docs/RELATORIO.md`.
- [x] Indice de prompts registrado em `docs/PROMPTS.md`.
- [x] Dump organizado do chat registrado em `docs/CHAT_DUMP.md`.
- [x] Perguntas de defesa preparadas em `docs/DEFESA.md`.
- [x] CSV com 64 medicoes preservado para auditoria.
- [x] Video e slides preparados e entregues separadamente pelo grupo.

## Verificacao

Comandos executados na preparacao da entrega:

```powershell
$env:JAVA_HOME='C:\Users\bNd\AppData\Local\Programs\DataGrip\jbr'
.\mvnw.cmd clean test package
python -m unittest discover scripts
```

Resultado em 15/07/2026: 48 testes Java e 12 testes Python aprovados, sem
falhas. O pacote `target/augmented-avl-1.0.0-SNAPSHOT.jar` foi gerado com
sucesso.
