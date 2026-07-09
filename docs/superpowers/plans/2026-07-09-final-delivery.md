# Final Delivery Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Prepare final reproducibility, prompt, presentation, and defense artifacts for Issue 14.

**Architecture:** Keep final materials under `docs/` and link the main reproduction commands from `README.md`. Generated data remains ignored.

**Tech Stack:** Markdown, Maven Wrapper, Python scripts already in the repository.

## Global Constraints

- README must cover download, generation, build, tests, execution, oracle, and plots.
- Prompts must include date, objective, decision, and affected files.
- Presentation must cover structure, invariants, methodology, and results.
- Defense must cover removal, rotations, p99, theta, cache, and baseline.
- Final verification must include `mvn clean test package`.

---

### Task 1: Final Delivery Docs

**Files:**
- Modify: `README.md`
- Create: `docs/PROMPTS.md`
- Create: `docs/APRESENTACAO.md`
- Create: `docs/DEFESA.md`
- Create: `docs/ENTREGA_FINAL.md`

**Interfaces:**
- Consumes: `scripts/run_oracle_check.py`
- Consumes: `scripts/experiment_matrix.py`
- Consumes: `scripts/plot_results.py`

- [ ] **Step 1: Write final docs**

Add the five final delivery artifacts and README reproduction section.

- [ ] **Step 2: Verify**

Run: `mvnw.cmd clean test package`

Run: `python -m unittest discover scripts`

Expected: all checks pass.
