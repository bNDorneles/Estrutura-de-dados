# Final Result Plots Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Generate presentation-ready plots that separate scale, insertion order, theta sensitivity, percentiles, and the official Group 14 configuration from the existing benchmark CSV.

**Architecture:** Extend the existing plotting module with filename metadata parsing and focused plotting helpers. Keep `generate_plots` as the public entry point so the current command and documentation remain valid.

**Tech Stack:** Python 3, pandas, matplotlib, unittest.

## Global Constraints

- Do not rerun benchmarks or modify `results.csv`.
- Preserve `baseline_comparison.png` and `scale_performance.png`.
- Generate `order_comparison.png`, `theta_sensitivity.png`, `percentiles_by_scale.png`, and `group14_official.png`.

---

### Task 1: Parse Matrix Metadata and Generate Final Plots

**Files:**
- Modify: `scripts/plot_results.py`
- Test: `scripts/test_plot_results.py`

**Interfaces:**
- Consumes: benchmark columns in `REQUIRED_COLUMNS` and matrix names such as `avl-ops1000_theta0p99_sorted.trace`.
- Produces: `extract_metadata(df: pandas.DataFrame) -> pandas.DataFrame` and six PNG files through `generate_plots(df, outdir)`.

- [x] **Step 1: Write the failing test**

Extend the fixture with all theta/order combinations, assert parsed `Tree`, `Theta`, and `Order` values, and assert all six output filenames exist.

- [x] **Step 2: Run test to verify it fails**

Run: `python -m unittest scripts.test_plot_results`

Expected: failure because `extract_metadata` and the four new PNGs do not exist.

- [x] **Step 3: Implement metadata parsing and plots**

Parse metadata with a regular expression over `Configuracao`. Add separate plotting helpers for order, theta, percentiles, and the official `theta=0.99`/`sorted` configuration. Use logarithmic axes for comparisons spanning multiple orders of magnitude.

- [x] **Step 4: Run automated tests**

Run: `python -m unittest scripts.test_plot_results`

Expected: all tests pass and six PNG files are created in the temporary directory.

- [x] **Step 5: Generate and inspect final artifacts**

Run: `python scripts/plot_results.py --input scratch/entrega-final-grupo14-20260714-003141/results.csv --outdir scratch/entrega-final-grupo14-20260714-003141/plots`

Expected: six non-empty PNG files and no changes to `results.csv`.

- [x] **Step 6: Commit**

```bash
git add scripts/plot_results.py scripts/test_plot_results.py docs/superpowers/plans/2026-07-14-final-result-plots.md
git commit -m "feat: add final experiment plots"
```
