# Oracle Integration Design

## Goal

Provide a reproducible Issue 11 workflow that generates the Group 2 workload, runs both tree implementations, and rejects benchmark usage when the professor oracle reports divergences.

## Scope

This issue covers correctness integration only. It does not execute the full experiment matrix, produce final report numbers, or create presentation material. Those remain in Issues 12, 13, and 14 after oracle validation is reliable.

## Approach

Create a small script under `scripts/` that coordinates the existing pieces:

1. Generate a trace and `.expected` file with `gen_workload_1.py generate`.
2. Run `edu.unipampa.ed.trace.TraceRunner` for AVL and BST.
3. Verify both generated `.out` files with `gen_workload_1.py verify`.
4. Return a non-zero exit code if generation, execution, or either verification fails.

The script accepts defaults matching Group 2: mix `50:20:30`, theta `0.9`, insert order `shuffle`, and seed `2`. It also supports a small synthetic mode so tests and local smoke checks can run without the SOSD `face` dataset.

## Files

- `scripts/run_oracle_check.py`: orchestration script for workload generation, trace execution, and oracle verification.
- `scripts/test_run_oracle_check.py`: Python unit tests for command construction, fail-fast behavior, and both-tree verification.
- `README.md`: reproducible commands for Issue 11 and the rule that benchmark results must pass the oracle first.
- `docs/EXPERIMENTOS.md`: methodology note linking oracle acceptance to later measurements.

## Error Handling

Any subprocess failure stops the script and propagates the failure code. This includes Maven execution failures and `[FALHA]` from the professor oracle. The caller must treat the corresponding measurement as invalid.

## Testing

Python tests validate the orchestration without running heavy datasets. The final verification includes Maven tests and Python script tests.
