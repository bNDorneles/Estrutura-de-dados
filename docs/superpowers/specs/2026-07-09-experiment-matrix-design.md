# Experiment Matrix Design

## Goal

Provide a reproducible Issue 12 workflow for generating and tracking the full experimental matrix before the final report consumes any result.

## Scope

This issue defines the matrix, records the execution environment, and produces commands/manifests for oracle validation and benchmark collection. It does not invent missing measurements and does not replace the official native execution on the group machine.

## Matrix

The mandatory dimensions are:

- operation counts across four orders of magnitude;
- theta values `0.0`, `0.6`, `0.99`, and `1.2`;
- insert order `shuffle` and `sorted`;
- tree implementation `avl` and `bst`.

Each generated trace must pass the oracle for both trees before its benchmark rows can be accepted.

## Artifacts

- `scripts/experiment_matrix.py`: creates a JSON manifest and command script for the full matrix.
- `scripts/test_experiment_matrix.py`: validates matrix coverage, command construction, and fail-status handling.
- `docs/EXPERIMENTOS.md`: documents how to run the matrix and how to record failures.

Generated traces, expected files, outputs, CSVs, and plots remain local under ignored directories such as `scratch/`.

## Acceptance Rules

- Every accepted benchmark row must reference a trace configuration that passed oracle validation.
- Timeout and out-of-memory cases are recorded as execution status, not estimated numbers.
- Environment metadata must include OS, Python, Java, Maven command, warmup, iterations, and timestamp.
