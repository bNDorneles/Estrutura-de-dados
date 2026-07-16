import argparse
import re

import pandas as pd
import matplotlib.pyplot as plt
import os

REQUIRED_COLUMNS = ["Configuracao", "TotalOps", "Media(ns)", "P50(ns)", "P99(ns)"]
CONFIG_PATTERN = re.compile(
    r"^(?P<Tree>avl|bst)-ops\d+_theta(?P<Theta>\d+(?:p\d+)?)_"
    r"(?P<Order>shuffle|sorted)\.trace$"
)

def validate_columns(df):
    for col in REQUIRED_COLUMNS:
        if col not in df.columns:
            print(f"Error: Missing column '{col}'")
            return False
    return True


def extract_metadata(df):
    enriched = df.copy()
    metadata = enriched["Configuracao"].str.extract(CONFIG_PATTERN)
    metadata["Theta"] = metadata["Theta"].str.replace("p", ".", regex=False).astype(float)
    enriched[["Tree", "Theta", "Order"]] = metadata
    return enriched


def _save(fig, outdir, filename):
    fig.tight_layout()
    fig.savefig(os.path.join(outdir, filename), dpi=160)
    plt.close(fig)


def generate_plots(df, outdir):
    df = extract_metadata(df)
    
    # Baseline comparison (bar chart da Media(ns) e P99(ns))
    baseline_df = df.groupby('Tree')[['Media(ns)', 'P99(ns)']].mean()
    if not baseline_df.empty:
        fig, ax = plt.subplots(figsize=(8, 6))
        baseline_df.plot(kind='bar', ax=ax)
        ax.set_title("Comparacao de Baseline (Media vs P99)")
        ax.set_ylabel("Latencia (ns)")
        ax.set_xlabel("Estrutura")
        ax.set_yscale("log")
        _save(fig, outdir, "baseline_comparison.png")
    
    # Scale performance (line chart TotalOps vs Media(ns) por arvore)
    scale_df = df.groupby(['TotalOps', 'Tree'])['Media(ns)'].mean().unstack()
    if not scale_df.empty:
        fig, ax = plt.subplots(figsize=(8, 6))
        scale_df.plot(kind='line', marker='o', ax=ax)
        ax.set_title("Performance em Escala")
        ax.set_ylabel("Media(ns)")
        ax.set_xlabel("Total de Operacoes")
        ax.set_xscale("log")
        ax.set_yscale("log")
        ax.grid(True, which="both", alpha=0.25)
        _save(fig, outdir, "scale_performance.png")

    order_df = df.groupby(["TotalOps", "Tree", "Order"])["Media(ns)"].mean()
    if not order_df.empty:
        fig, axes = plt.subplots(1, 2, figsize=(12, 5), sharey=True)
        for ax, order in zip(axes, ["shuffle", "sorted"]):
            panel = order_df.xs(order, level="Order").unstack("Tree")
            panel.plot(marker="o", ax=ax)
            ax.set_title(f"Ordem: {order}")
            ax.set_xlabel("Total de operacoes")
            ax.set_xscale("log")
            ax.set_yscale("log")
            ax.grid(True, which="both", alpha=0.25)
        axes[0].set_ylabel("Latencia media (ns)")
        fig.suptitle("Impacto da Ordem de Insercao")
        _save(fig, outdir, "order_comparison.png")

    max_ops = df["TotalOps"].max()
    theta_df = (
        df[df["TotalOps"] == max_ops]
        .groupby(["Theta", "Tree", "Order"])["Media(ns)"]
        .mean()
    )
    if not theta_df.empty:
        fig, axes = plt.subplots(1, 2, figsize=(12, 5), sharey=True)
        available_orders = set(theta_df.index.get_level_values("Order"))
        for ax, order in zip(axes, ["shuffle", "sorted"]):
            if order not in available_orders:
                ax.set_visible(False)
                continue
            panel = theta_df.xs(order, level="Order").unstack("Tree")
            panel.plot(marker="o", ax=ax)
            ax.set_title(f"Ordem: {order}")
            ax.set_xlabel("Theta")
            ax.set_yscale("log")
            ax.grid(True, which="both", alpha=0.25)
        axes[0].set_ylabel("Latencia media (ns)")
        fig.suptitle(f"Sensibilidade ao Theta ({max_ops:,} operacoes)")
        _save(fig, outdir, "theta_sensitivity.png")

    percentile_df = df.groupby(["TotalOps", "Tree"])[
        ["Media(ns)", "P50(ns)", "P99(ns)"]
    ].mean()
    if not percentile_df.empty:
        fig, axes = plt.subplots(1, 2, figsize=(12, 5), sharey=True)
        for ax, tree in zip(axes, ["avl", "bst"]):
            panel = percentile_df.xs(tree, level="Tree")
            panel.plot(marker="o", ax=ax)
            ax.set_title(tree.upper())
            ax.set_xlabel("Total de operacoes")
            ax.set_xscale("log")
            ax.set_yscale("log")
            ax.grid(True, which="both", alpha=0.25)
        axes[0].set_ylabel("Latencia (ns)")
        fig.suptitle("Media, P50 e P99 por Escala")
        _save(fig, outdir, "percentiles_by_scale.png")

    group14 = df[(df["Theta"] == 0.99) & (df["Order"] == "sorted")]
    group14_df = group14.groupby(["TotalOps", "Tree"])["Media(ns)"].mean().unstack()
    if not group14_df.empty:
        fig, ax = plt.subplots(figsize=(8, 6))
        group14_df.plot(marker="o", ax=ax)
        ax.set_title("Grupo 14: Theta 0.99, Mix 45:30:25, Ordem Sorted")
        ax.set_xlabel("Total de operacoes")
        ax.set_ylabel("Latencia media (ns)")
        ax.set_xscale("log")
        ax.set_yscale("log")
        ax.grid(True, which="both", alpha=0.25)
        _save(fig, outdir, "group14_official.png")

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
