#!/usr/bin/env python3
import argparse
import json
import platform
import sys
from datetime import datetime, timezone
from pathlib import Path


DEFAULT_OPS = [1_000, 10_000, 100_000, 1_000_000]
DEFAULT_THETAS = [0.0, 0.6, 0.99, 1.2]
DEFAULT_ORDERS = ["shuffle", "sorted"]
DEFAULT_TREES = ["avl", "bst"]


def build_parser():
    parser = argparse.ArgumentParser(
        description="Generate the required experiment matrix manifest and runnable commands."
    )
    source = parser.add_mutually_exclusive_group(required=True)
    source.add_argument("--keys", help="SOSD or text key file used for the official matrix")
    source.add_argument("--synthetic", type=int, help="Generate synthetic keys for a smoke matrix")
    parser.add_argument("--outdir", required=True, help="Directory for manifest, commands, traces, and CSV")
    parser.add_argument("--ops", default=",".join(str(value) for value in DEFAULT_OPS))
    parser.add_argument("--thetas", default=",".join(str(value) for value in DEFAULT_THETAS))
    parser.add_argument("--orders", default=",".join(DEFAULT_ORDERS))
    parser.add_argument("--trees", default=",".join(DEFAULT_TREES))
    parser.add_argument("--format", choices=["auto", "sosd", "text"], default="auto")
    parser.add_argument("--key-bytes", type=int, choices=[4, 8], default=8)
    parser.add_argument("--max-load", type=int, default=0)
    parser.add_argument("--mix", default="50:20:30")
    parser.add_argument("--seed", type=int, default=2)
    parser.add_argument("--warmup", type=int, default=3)
    parser.add_argument("--iterations", type=int, default=10)
    return parser


def build_cases(args):
    outdir = Path(args.outdir)
    cases = []
    for ops in _parse_int_list(args.ops):
        for theta in _parse_float_list(args.thetas):
            for insert_order in _parse_str_list(args.orders):
                trace_name = f"ops{ops}_theta{_slug_float(theta)}_{insert_order}"
                trace_prefix = outdir / "traces" / trace_name
                for tree in _parse_str_list(args.trees):
                    cases.append(
                        {
                            "name": f"{trace_name}_{tree}",
                            "trace_name": trace_name,
                            "trace_prefix": _posix(trace_prefix),
                            "ops": ops,
                            "theta": theta,
                            "insert_order": insert_order,
                            "tree": tree,
                            "mix": args.mix,
                            "seed": args.seed,
                            "warmup": args.warmup,
                            "iterations": args.iterations,
                            "status": "pending",
                        }
                    )
    return cases


def write_manifest(cases, outdir):
    outdir = Path(outdir)
    outdir.mkdir(parents=True, exist_ok=True)
    path = outdir / "manifest.json"
    manifest = {
        "generated_at": datetime.now(timezone.utc).isoformat(),
        "environment": {
            "os": platform.platform(),
            "python": sys.version.split()[0],
            "java": "record from `java -version` on the benchmark machine",
            "maven": "record from `./mvnw -version` or `.\\mvnw.cmd -version`",
            "warmup": cases[0]["warmup"] if cases else None,
            "iterations": cases[0]["iterations"] if cases else None,
        },
        "acceptance_rule": "Only benchmark rows whose trace passed oracle validation for AVL and BST may be used.",
        "cases": cases,
    }
    path.write_text(json.dumps(manifest, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    return path


def write_commands(cases, outdir, warmup=3, iterations=10, source_args=None):
    outdir = Path(outdir)
    outdir.mkdir(parents=True, exist_ok=True)
    path = outdir / "commands.ps1"
    csv_path = _posix(outdir / "results.csv")
    grouped = _group_by_trace(cases)
    lines = [
        "$ErrorActionPreference = 'Stop'",
        "# Run from the repository root after configuring Java 17.",
        "# These commands validate each trace with the oracle before collecting benchmark CSV rows.",
        "",
    ]
    for trace_name, trace_cases in grouped.items():
        first = trace_cases[0]
        oracle = [
            "python",
            "scripts/run_oracle_check.py",
            _source_option(source_args),
            "--ops",
            str(first["ops"]),
            "--theta",
            str(first["theta"]),
            "--insert-order",
            first["insert_order"],
            "--mix",
            first["mix"],
            "--seed",
            str(first["seed"]),
            "--out",
            first["trace_prefix"],
        ]
        lines.append(" ".join(part for part in oracle if part))
        for case in trace_cases:
            trace_file = f"{case['trace_prefix']}.trace"
            lines.append(
                ".\\mvnw.cmd exec:java "
                "-Dexec.mainClass=edu.unipampa.ed.benchmark.BenchmarkRunner "
                f"'-Dexec.args=--trace {trace_file} --tree {case['tree']} --out {csv_path} "
                f"--warmup {warmup} --iterations {iterations}'"
            )
        lines.append("")
    path.write_text("\n".join(lines), encoding="utf-8")
    return path


def main(argv=None):
    args = build_parser().parse_args(argv)
    outdir = Path(args.outdir)
    cases = build_cases(args)
    manifest = write_manifest(cases, outdir)
    commands = write_commands(cases, outdir, args.warmup, args.iterations, args)
    print(f"[ok] manifest written to {manifest}")
    print(f"[ok] commands written to {commands}")
    print(f"[ok] cases: {len(cases)}")
    return 0


def _parse_int_list(value):
    return [int(item.strip()) for item in value.split(",") if item.strip()]


def _parse_float_list(value):
    return [float(item.strip()) for item in value.split(",") if item.strip()]


def _parse_str_list(value):
    return [item.strip() for item in value.split(",") if item.strip()]


def _slug_float(value):
    return str(value).replace(".", "p")


def _posix(path):
    return Path(path).as_posix()


def _group_by_trace(cases):
    grouped = {}
    for case in cases:
        grouped.setdefault(case["trace_name"], []).append(case)
    return grouped


def _source_option(args):
    if args is None:
        return "--synthetic 1000"
    if args.keys:
        parts = ["--keys", args.keys, "--format", args.format, "--key-bytes", str(args.key_bytes)]
        if args.max_load:
            parts.extend(["--max-load", str(args.max_load)])
        return " ".join(parts)
    return f"--synthetic {args.synthetic}"


if __name__ == "__main__":
    raise SystemExit(main())
