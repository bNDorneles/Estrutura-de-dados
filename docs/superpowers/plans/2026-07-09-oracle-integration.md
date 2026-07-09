# Oracle Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build a reproducible Issue 11 oracle gate for AVL and BST trace execution.

**Architecture:** Add a focused Python orchestration script that delegates generation to `gen_workload_1.py`, tree execution to Maven `TraceRunner`, and correctness checking back to the oracle. Keep benchmark measurement separate so Issue 12 can only consume traces that passed this gate.

**Tech Stack:** Java 17, Maven Wrapper, Python 3 standard library, existing `gen_workload_1.py`.

## Global Constraints

- Use Group 2 defaults: mix `50:20:30`, theta `0.9`, insert order `shuffle`, seed `2`.
- Do not commit generated `.trace`, `.expected`, `.out`, CSV, or plot artifacts.
- No benchmark result is valid unless both AVL and BST verification commands return success.
- Keep the script runnable on Windows PowerShell and Unix shells through Python path handling.

---

### Task 1: Oracle Check Script

**Files:**
- Create: `scripts/run_oracle_check.py`
- Test: `scripts/test_run_oracle_check.py`

**Interfaces:**
- Produces: `build_parser() -> argparse.ArgumentParser`
- Produces: `build_steps(args: argparse.Namespace, repo_root: pathlib.Path) -> list[list[str]]`
- Produces: `main(argv: list[str] | None = None) -> int`

- [ ] **Step 1: Write failing command-construction tests**

```python
def test_build_steps_uses_group_two_defaults(tmp_path):
    args = run_oracle_check.build_parser().parse_args(["--synthetic", "100", "--out", str(tmp_path / "g2")])
    steps = run_oracle_check.build_steps(args, Path("repo"))

    joined = [" ".join(step) for step in steps]
    assert "--mix 50:20:30" in joined[0]
    assert "--theta 0.9" in joined[0]
    assert "--insert-order shuffle" in joined[0]
    assert "--seed 2" in joined[0]
    assert any("--tree avl" in step for step in joined)
    assert any("--tree bst" in step for step in joined)
```

- [ ] **Step 2: Run test to verify it fails**

Run: `python -m unittest scripts.test_run_oracle_check`

Expected: fail because `scripts/run_oracle_check.py` does not exist.

- [ ] **Step 3: Implement parser and step builder**

Create `scripts/run_oracle_check.py` with parser defaults and command construction for generate, AVL run, AVL verify, BST run, and BST verify.

- [ ] **Step 4: Run test to verify it passes**

Run: `python -m unittest scripts.test_run_oracle_check`

Expected: pass.

### Task 2: Fail-Fast Execution

**Files:**
- Modify: `scripts/run_oracle_check.py`
- Modify: `scripts/test_run_oracle_check.py`

**Interfaces:**
- Produces: `run_steps(steps: list[list[str]], runner: Callable[[list[str]], subprocess.CompletedProcess]) -> int`

- [ ] **Step 1: Write failing fail-fast test**

```python
def test_run_steps_stops_on_first_failure():
    calls = []

    def runner(command):
        calls.append(command)
        return subprocess.CompletedProcess(command, 7)

    rc = run_oracle_check.run_steps([["first"], ["second"]], runner)

    assert rc == 7
    assert calls == [["first"]]
```

- [ ] **Step 2: Run test to verify it fails**

Run: `python -m unittest scripts.test_run_oracle_check`

Expected: fail because `run_steps` is not implemented.

- [ ] **Step 3: Implement fail-fast runner**

Add `run_steps` using `subprocess.run` by default and return immediately on any non-zero return code.

- [ ] **Step 4: Run test to verify it passes**

Run: `python -m unittest scripts.test_run_oracle_check`

Expected: pass.

### Task 3: Documentation

**Files:**
- Modify: `README.md`
- Modify: `docs/EXPERIMENTOS.md`

**Interfaces:**
- Consumes: `python scripts/run_oracle_check.py`

- [ ] **Step 1: Add README commands**

Document synthetic smoke-check commands and real Group 2 commands with `--keys`, `--key-bytes`, `--ops`, and `--out`.

- [ ] **Step 2: Add methodology note**

State that CSVs and plots for Issues 12 and 13 may only use traces where both AVL and BST produced `[OK]`.

- [ ] **Step 3: Run full verification**

Run: `python -m unittest scripts.test_run_oracle_check`

Run: `.\mvnw.cmd test`

Expected: both commands exit 0.
