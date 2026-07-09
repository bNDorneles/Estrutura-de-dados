import json
import sys
import unittest
from pathlib import Path

sys.path.append(str(Path(__file__).resolve().parent))

import experiment_matrix


class ExperimentMatrixTest(unittest.TestCase):
    def test_build_cases_covers_required_dimensions(self):
        args = experiment_matrix.build_parser().parse_args(
            ["--synthetic", "1000", "--outdir", "scratch/matrix"]
        )

        cases = experiment_matrix.build_cases(args)

        self.assertEqual({0.0, 0.6, 0.99, 1.2}, {case["theta"] for case in cases})
        self.assertEqual({"shuffle", "sorted"}, {case["insert_order"] for case in cases})
        self.assertEqual({"avl", "bst"}, {case["tree"] for case in cases})
        self.assertGreaterEqual(len({case["ops"] for case in cases}), 4)

    def test_case_names_are_deterministic_and_trace_scoped(self):
        args = experiment_matrix.build_parser().parse_args(
            ["--synthetic", "1000", "--outdir", "scratch/matrix"]
        )

        cases = experiment_matrix.build_cases(args)
        first = cases[0]

        self.assertEqual("ops1000_theta0p0_shuffle_avl", first["name"])
        self.assertEqual("scratch/matrix/traces/ops1000_theta0p0_shuffle", first["trace_prefix"])
        self.assertEqual(3, first["warmup"])
        self.assertEqual(10, first["iterations"])

    def test_write_outputs_manifest_and_commands(self):
        temp_dir = Path("scratch/test-matrix")
        args = experiment_matrix.build_parser().parse_args(
            ["--synthetic", "1000", "--ops", "1000", "--outdir", str(temp_dir)]
        )
        cases = experiment_matrix.build_cases(args)

        manifest = experiment_matrix.write_manifest(cases, temp_dir)
        commands = experiment_matrix.write_commands(cases, temp_dir)

        self.assertTrue(manifest.exists())
        self.assertTrue(commands.exists())
        self.assertIn("run_oracle_check.py", commands.read_text(encoding="utf-8"))
        self.assertIn("BenchmarkRunner", commands.read_text(encoding="utf-8"))
        manifest_data = json.loads(manifest.read_text(encoding="utf-8"))
        self.assertEqual(len(cases), len(manifest_data["cases"]))

    def test_write_commands_quotes_maven_properties_for_powershell(self):
        temp_dir = Path("scratch/test-matrix")
        args = experiment_matrix.build_parser().parse_args(
            ["--synthetic", "1000", "--ops", "1000", "--outdir", str(temp_dir)]
        )
        cases = experiment_matrix.build_cases(args)

        commands = experiment_matrix.write_commands(cases, temp_dir)
        content = commands.read_text(encoding="utf-8")

        self.assertIn('& ".\\mvnw.cmd" -q test-compile', content)
        self.assertIn('$java = Join-Path $env:JAVA_HOME "bin/java.exe"', content)
        self.assertIn('& $java -cp "target/classes" edu.unipampa.ed.benchmark.BenchmarkRunner', content)


if __name__ == "__main__":
    unittest.main()
