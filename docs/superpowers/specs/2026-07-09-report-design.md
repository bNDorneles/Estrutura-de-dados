# Empirical Report Design

## Goal

Provide the final report source for Issue 13 without inventing measurements.

## Scope

The report explains methodology, architecture, invariants, expected theoretical behavior, result interpretation criteria, discarded alternatives, authorship, and tooling. Numeric result tables and graphs must be generated from validated CSV files produced by Issue 12.

## Report Structure

- context and objective;
- implementation summary;
- five AVL invariants and rotation effects;
- experimental methodology;
- result sections for scale, p50/p99, theta, insertion order, and AVL vs BST;
- theory versus practice discussion;
- cache, allocation, rotations, and tail-latency discussion;
- alternatives discarded;
- authorship and tools.

## Integrity Rule

The report may reference expected trends and explain how to interpret them, but it must not include unmeasured numbers. Final numeric values must come from CSV rows whose trace passed oracle validation for AVL and BST.
