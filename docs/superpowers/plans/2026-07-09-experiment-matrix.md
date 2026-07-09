# Experiment Matrix Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a reproducible experiment-matrix generator for Issue 12.

**Architecture:** Add a Python script that expands the required dimensions into named cases, writes a JSON manifest, and writes a shell-neutral command list. The script delegates correctness to `scripts/run_oracle_check.py` and benchmark collection to the existing Java `BenchmarkRunner`.

**Tech Stack:** Python 3 standard library, Java 17 through Maven Wrapper, existing oracle and benchmark scripts.

## Global Constraints

- Matrix must include at least four operation counts.
- Matrix must include theta values `0.0`, `0.6`, `0.99`, and `1.2`.
- Matrix must compare `shuffle` and `sorted`.
- Matrix must compare AVL and BST.
- No measured number may be invented.
- Generated traces, outputs, CSVs, and plots must stay out of Git.

---

### Task 1: Matrix Expansion

**Files:**
- Create: `scripts/experiment_matrix.py`
- Create: `scripts/test_experiment_matrix.py`

**Interfaces:**
- Produces: `build_parser() -> argparse.ArgumentParser`
- Produces: `build_cases(args: argparse.Namespace) -> list[dict]`

- [ ] **Step 1: Write failing tests for matrix coverage**

```python
def test_build_cases_covers_required_dimensions():
    args = experiment_matrix.build_parser().parse_args(["--synthetic", "1000", "--outdir", "scratch/matrix"])
    cases = experiment_matrix.build_cases(args)
    assert {case["theta"] for case in cases} == {0.0, 0.6, 0.99, 1.2}
    assert {case["insert_order"] for case in cases} == {"shuffle", "sorted"}
    assert {case["tree"] for case in cases} == {"avl", "bst"}
    assert len({case["ops"] for case in cases}) >= 4
```

- [ ] **Step 2: Run test to verify it fails**

Run: `python -m unittest scripts.test_experiment_matrix`

Expected: fail because `experiment_matrix.py` does not exist.

- [ ] **Step 3: Implement parser and matrix expansion**

Create `scripts/experiment_matrix.py` with defaults for four operation counts, required theta/order/tree dimensions, and deterministic case names.

- [ ] **Step 4: Run test to verify it passes**

Run: `python -m unittest scripts.test_experiment_matrix`

Expected: pass.

### Task 2: Manifest and Commands

**Files:**
- Modify: `scripts/experiment_matrix.py`
- Modify: `scripts/test_experiment_matrix.py`
- Modify: `docs/EXPERIMENTOS.md`

**Interfaces:**
- Produces: `write_manifest(cases: list[dict], outdir: pathlib.Path) -> pathlib.Path`
- Produces: `write_commands(cases: list[dict], outdir: pathlib.Path) -> pathlib.Path`

- [ ] **Step 1: Write failing tests for manifest and command output**

```python
def test_write_outputs_manifest_and_commands(tmp_path):
    args = experiment_matrix.build_parser().parse_args(["--synthetic", "1000", "--outdir", str(tmp_path)])
    cases = experiment_matrix.build_cases(args)
    manifest = experiment_matrix.write_manifest(cases, tmp_path)
    commands = experiment_matrix.write_commands(cases, tmp_path)
    assert manifest.exists()
    assert commands.exists()
    assert "run_oracle_check.py" in commands.read_text()
    assert "BenchmarkRunner" in commands.read_text()
```

- [ ] **Step 2: Run test to verify it fails**

Run: `python -m unittest scripts.test_experiment_matrix`

Expected: fail because writers are missing.

- [ ] **Step 3: Implement writers and documentation**

Write `manifest.json` with environment metadata and cases. Write `commands.ps1` with oracle and benchmark commands for each case. Document the workflow in `docs/EXPERIMENTOS.md`.

- [ ] **Step 4: Run verification**

Run: `python -m unittest scripts.test_experiment_matrix`

Run: `python scripts/experiment_matrix.py --synthetic 1000 --outdir scratch/matrix-smoke`

Expected: tests pass and manifest/commands are generated.
