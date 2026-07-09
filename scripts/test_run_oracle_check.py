import subprocess
import sys
import tempfile
import unittest
from pathlib import Path

sys.path.append(str(Path(__file__).resolve().parent))

import run_oracle_check


class RunOracleCheckTest(unittest.TestCase):
    def test_build_steps_uses_group_two_defaults_for_both_trees(self):
        args = run_oracle_check.build_parser().parse_args(
            ["--synthetic", "100", "--out", "scratch/g2"]
        )

        steps = run_oracle_check.build_steps(args, Path("repo"))
        joined = [" ".join(step) for step in steps]

        self.assertIn("--mix 50:20:30", joined[0])
        self.assertIn("--theta 0.9", joined[0])
        self.assertIn("--insert-order shuffle", joined[0])
        self.assertIn("--seed 2", joined[0])
        self.assertTrue(any("--tree avl" in step for step in joined))
        self.assertTrue(any("--tree bst" in step for step in joined))

    def test_build_steps_verifies_avl_and_bst_outputs(self):
        args = run_oracle_check.build_parser().parse_args(
            ["--synthetic", "100", "--out", "scratch/g2"]
        )

        steps = run_oracle_check.build_steps(args, Path("repo"))

        self.assertEqual(5, len(steps))
        self.assertEqual(["--expected", str(Path("scratch/g2.expected"))], steps[2][3:5])
        self.assertEqual(["--candidate", str(Path("scratch/g2.avl.out"))], steps[2][5:7])
        self.assertEqual(["--expected", str(Path("scratch/g2.expected"))], steps[4][3:5])
        self.assertEqual(["--candidate", str(Path("scratch/g2.bst.out"))], steps[4][5:7])

    def test_run_steps_stops_on_first_failure(self):
        calls = []

        def runner(command):
            calls.append(command)
            return subprocess.CompletedProcess(command, 7)

        rc = run_oracle_check.run_steps([["first"], ["second"]], runner)

        self.assertEqual(7, rc)
        self.assertEqual([["first"]], calls)

    def test_prepare_output_dir_creates_missing_parent(self):
        with tempfile.TemporaryDirectory() as temp:
            output_dir = Path(temp) / "new-dir"
            args = run_oracle_check.build_parser().parse_args(
                ["--synthetic", "100", "--out", str(output_dir / "g2")]
            )

            run_oracle_check.prepare_output_dir(args)

            self.assertTrue(output_dir.exists())

    def test_main_returns_first_runner_failure(self):
        with tempfile.TemporaryDirectory() as temp:
            out_prefix = Path(temp) / "g2"

            def runner(command):
                return subprocess.CompletedProcess(command, 11)

            rc = run_oracle_check.main(
                ["--synthetic", "100", "--out", str(out_prefix)],
                runner=runner,
            )

            self.assertEqual(11, rc)


if __name__ == "__main__":
    unittest.main()
