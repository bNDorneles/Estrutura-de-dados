import unittest
import pandas as pd
import os
import sys
import tempfile
sys.path.append(os.path.dirname(__file__))
from plot_results import extract_metadata, validate_columns, generate_plots

class TestPlotResults(unittest.TestCase):
    def setUp(self):
        self.temp_dir = tempfile.TemporaryDirectory()
        self.mock_csv = os.path.join(self.temp_dir.name, "mock_results.csv")
        self.out_dir = os.path.join(self.temp_dir.name, "plots")
        os.makedirs(self.out_dir, exist_ok=True)
        pd.DataFrame([
            {
                "Configuracao": "avl-ops1000_theta0p99_sorted.trace",
                "TotalOps": 1000,
                "TamanhoFinal": 500,
                "JVM": "test",
                "SO": "test",
                "Memoria(MB)": 256,
                "Media(ns)": 10.0,
                "P50(ns)": 9,
                "P99(ns)": 20,
            },
            {
                "Configuracao": "bst-ops1000_theta0p99_sorted.trace",
                "TotalOps": 1000,
                "TamanhoFinal": 500,
                "JVM": "test",
                "SO": "test",
                "Memoria(MB)": 256,
                "Media(ns)": 15.0,
                "P50(ns)": 14,
                "P99(ns)": 30,
            },
            {
                "Configuracao": "avl-ops10000_theta0p6_shuffle.trace",
                "TotalOps": 10000,
                "TamanhoFinal": 5000,
                "JVM": "test",
                "SO": "test",
                "Memoria(MB)": 256,
                "Media(ns)": 12.0,
                "P50(ns)": 10,
                "P99(ns)": 24,
            },
            {
                "Configuracao": "bst-ops10000_theta0p6_shuffle.trace",
                "TotalOps": 10000,
                "TamanhoFinal": 5000,
                "JVM": "test",
                "SO": "test",
                "Memoria(MB)": 256,
                "Media(ns)": 18.0,
                "P50(ns)": 16,
                "P99(ns)": 36,
            },
        ]).to_csv(self.mock_csv, index=False)

    def tearDown(self):
        self.temp_dir.cleanup()
    
    def test_validate_columns(self):
        df = pd.read_csv(self.mock_csv)
        self.assertTrue(validate_columns(df))
        
        # Test missing column
        bad_df = df.drop(columns=["Media(ns)"])
        self.assertFalse(validate_columns(bad_df))
        
    def test_generate_plots(self):
        df = pd.read_csv(self.mock_csv)
        generate_plots(df, self.out_dir)

        expected = {
            "baseline_comparison.png",
            "scale_performance.png",
            "order_comparison.png",
            "theta_sensitivity.png",
            "percentiles_by_scale.png",
            "group14_official.png",
        }
        self.assertTrue(expected.issubset(set(os.listdir(self.out_dir))))

    def test_extract_metadata_from_matrix_configuration(self):
        df = extract_metadata(pd.read_csv(self.mock_csv))

        self.assertEqual({"avl", "bst"}, set(df["Tree"]))
        self.assertEqual({0.6, 0.99}, set(df["Theta"]))
        self.assertEqual({"shuffle", "sorted"}, set(df["Order"]))

if __name__ == "__main__":
    unittest.main()
