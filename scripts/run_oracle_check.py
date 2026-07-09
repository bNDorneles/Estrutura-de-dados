#!/usr/bin/env python3
import argparse
import subprocess
import sys
from pathlib import Path


def build_parser():
    parser = argparse.ArgumentParser(
        description="Generate a workload, run AVL and BST, and verify both outputs with the oracle."
    )
    source = parser.add_mutually_exclusive_group(required=True)
    source.add_argument("--keys", help="SOSD or text key file passed to gen_workload_1.py")
    source.add_argument("--synthetic", type=int, help="Generate N synthetic keys for a smoke check")
    parser.add_argument("--format", choices=["auto", "sosd", "text"], default="auto")
    parser.add_argument("--key-bytes", type=int, choices=[4, 8], default=8)
    parser.add_argument("--max-load", type=int, default=0)
    parser.add_argument("--out", required=True, help="Output prefix for trace, expected, and tree outputs")
    parser.add_argument("--ops", type=int, default=1_000_000)
    parser.add_argument("--universe", type=int, default=0)
    parser.add_argument("--mix", default="50:20:30")
    parser.add_argument("--theta", type=float, default=0.9)
    parser.add_argument("--insert-order", choices=["shuffle", "sorted", "popularity"], default="shuffle")
    parser.add_argument("--seed", type=int, default=2)
    return parser


def build_steps(args, repo_root):
    repo_root = Path(repo_root)
    out_prefix = Path(args.out)
    trace = out_prefix.with_suffix(".trace")
    expected = out_prefix.with_suffix(".expected")
    avl_out = out_prefix.with_suffix(".avl.out")
    bst_out = out_prefix.with_suffix(".bst.out")
    mvnw = repo_root / ("mvnw.cmd" if sys.platform.startswith("win") else "mvnw")

    generate = [
        sys.executable,
        str(repo_root / "gen_workload_1.py"),
        "generate",
    ]
    if args.keys:
        generate.extend(["--keys", args.keys, "--format", args.format, "--key-bytes", str(args.key_bytes)])
        if args.max_load:
            generate.extend(["--max-load", str(args.max_load)])
    else:
        generate.extend(["--synthetic", str(args.synthetic)])
    generate.extend(
        [
            "--out",
            str(out_prefix),
            "--ops",
            str(args.ops),
            "--mix",
            args.mix,
            "--theta",
            str(args.theta),
            "--insert-order",
            args.insert_order,
            "--seed",
            str(args.seed),
        ]
    )
    if args.universe:
        generate.extend(["--universe", str(args.universe)])

    def run_tree(tree, candidate):
        return [
            str(mvnw),
            "exec:java",
            "-Dexec.mainClass=edu.unipampa.ed.trace.TraceRunner",
            f"-Dexec.args=--trace {trace} --out {candidate} --tree {tree}",
        ]

    def verify(candidate):
        return [
            sys.executable,
            str(repo_root / "gen_workload_1.py"),
            "verify",
            "--expected",
            str(expected),
            "--candidate",
            str(candidate),
        ]

    return [
        generate,
        run_tree("avl", avl_out),
        verify(avl_out),
        run_tree("bst", bst_out),
        verify(bst_out),
    ]


def run_steps(steps, runner=subprocess.run):
    for step in steps:
        result = runner(step)
        if result.returncode != 0:
            return result.returncode
    return 0


def prepare_output_dir(args):
    out_parent = Path(args.out).parent
    if str(out_parent) != ".":
        out_parent.mkdir(parents=True, exist_ok=True)


def main(argv=None, runner=subprocess.run):
    parser = build_parser()
    args = parser.parse_args(argv)
    prepare_output_dir(args)
    repo_root = Path(__file__).resolve().parents[1]
    steps = build_steps(args, repo_root)
    return run_steps(steps, runner)


if __name__ == "__main__":
    raise SystemExit(main())
