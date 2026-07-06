import unittest
import pandas as pd
import os
import sys
sys.path.append(os.path.dirname(__file__))
from plot_results import validate_columns, generate_plots

class TestPlotResults(unittest.TestCase):
    def setUp(self):
        self.mock_csv = "scratch/mock_results.csv"
        self.out_dir = "scratch/plots"
        os.makedirs(self.out_dir, exist_ok=True)
    
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
