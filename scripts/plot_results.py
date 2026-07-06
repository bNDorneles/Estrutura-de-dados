import argparse
import pandas as pd
import matplotlib.pyplot as plt
import os

REQUIRED_COLUMNS = ["Configuracao", "TotalOps", "Media(ns)", "P50(ns)", "P99(ns)"]

def validate_columns(df):
    for col in REQUIRED_COLUMNS:
        if col not in df.columns:
            print(f"Error: Missing column '{col}'")
            return False
    return True

def generate_plots(df, outdir):
    # Separa por arvore, ex: avl-100k -> tipo avl
    df['Tree'] = df['Configuracao'].apply(lambda x: x.split('-')[0])
    
    # Baseline comparison (bar chart da Media(ns) e P99(ns))
    baseline_df = df.groupby('Tree')[['Media(ns)', 'P99(ns)']].mean()
    if not baseline_df.empty:
        fig, ax = plt.subplots(figsize=(8, 6))
        baseline_df.plot(kind='bar', ax=ax)
        ax.set_title("Comparacao de Baseline (Media vs P99)")
        ax.set_ylabel("Latencia (ns)")
        ax.set_xlabel("Estrutura")
        plt.tight_layout()
        plt.savefig(os.path.join(outdir, "baseline_comparison.png"))
        plt.close()
    
    # Scale performance (line chart TotalOps vs Media(ns) por arvore)
    scale_df = df.groupby(['TotalOps', 'Tree'])['Media(ns)'].mean().unstack()
    if not scale_df.empty:
        fig, ax = plt.subplots(figsize=(8, 6))
        scale_df.plot(kind='line', marker='o', ax=ax)
        ax.set_title("Performance em Escala")
        ax.set_ylabel("Media(ns)")
        ax.set_xlabel("Total de Operacoes")
        plt.tight_layout()
        plt.savefig(os.path.join(outdir, "scale_performance.png"))
        plt.close()

def main():
    parser = argparse.ArgumentParser(description="Generate plots from benchmark CSV")
    parser.add_argument("--input", required=True, help="Input CSV file")
    parser.add_argument("--outdir", required=True, help="Output directory for plots")
    args = parser.parse_args()

    if not os.path.exists(args.input):
        print(f"Error: Input file {args.input} not found")
        return

    os.makedirs(args.outdir, exist_ok=True)

    df = pd.read_csv(args.input)
    if not validate_columns(df):
        return

    generate_plots(df, args.outdir)

if __name__ == "__main__":
    main()
