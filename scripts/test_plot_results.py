import unittest
import pandas as pd
import os
import sys
import tempfile
sys.path.append(os.path.dirname(__file__))
from plot_results import validate_columns, generate_plots

class TestPlotResults(unittest.TestCase):
    def setUp(self):
        self.temp_dir = tempfile.TemporaryDirectory()
        self.mock_csv = os.path.join(self.temp_dir.name, "mock_results.csv")
        self.out_dir = os.path.join(self.temp_dir.name, "plots")
        os.makedirs(self.out_dir, exist_ok=True)
        pd.DataFrame([
            {
                "Configuracao": "avl-ops1000.trace",
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
                "Configuracao": "bst-ops1000.trace",
                "TotalOps": 1000,
                "TamanhoFinal": 500,
                "JVM": "test",
                "SO": "test",
                "Memoria(MB)": 256,
                "Media(ns)": 15.0,
                "P50(ns)": 14,
                "P99(ns)": 30,
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
        
        self.assertTrue(os.path.exists(os.path.join(self.out_dir, "baseline_comparison.png")))
        self.assertTrue(os.path.exists(os.path.join(self.out_dir, "scale_performance.png")))

if __name__ == "__main__":
    unittest.main()
