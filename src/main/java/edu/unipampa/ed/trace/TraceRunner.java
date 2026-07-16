package edu.unipampa.ed.trace;

public class TraceRunner {
    public static void main(String[] args) {
        String traceFile = null;
        String outFile = null;
        String treeType = null;

        for (int i = 0; i < args.length; i++) {
            if ("--trace".equals(args[i]) && i + 1 < args.length) {
                traceFile = args[++i];
            } else if ("--out".equals(args[i]) && i + 1 < args.length) {
                outFile = args[++i];
            } else if ("--tree".equals(args[i]) && i + 1 < args.length) {
                treeType = args[++i];
            }
        }

        if (traceFile == null || outFile == null || treeType == null) {
            throw new IllegalArgumentException("Missing arguments. Required: --trace <file> --out <file> --tree <avl|bst>");
        }

        edu.unipampa.ed.api.OrderedLongSet tree;
        if ("avl".equals(treeType)) {
            tree = new edu.unipampa.ed.avl.AugmentedAvlTree();
        } else if ("bst".equals(treeType)) {
            tree = new edu.unipampa.ed.bst.UnbalancedBst();
        } else {
            throw new UnsupportedOperationException("Tree type not supported or implemented yet: " + treeType);
        }

        try (
            java.io.BufferedReader reader = java.nio.file.Files.newBufferedReader(java.nio.file.Paths.get(traceFile));
            java.io.PrintWriter writer = new java.io.PrintWriter(java.nio.file.Files.newBufferedWriter(java.nio.file.Paths.get(outFile)))
        ) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();
                
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("\\s+");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Erro na linha " + lineNumber + ": Formato invalido.");
                }

                String op = parts[0];
                long key;
                try {
                    key = Long.parseLong(parts[1]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Erro na linha " + lineNumber + ": Chave invalida.");
                }

                switch (op) {
                    case "I":
                        tree.insert(key);
                        break;
                    case "D":
                        tree.delete(key);
                        break;
                    case "S":
                        boolean found = tree.search(key);
                        writer.println(key + (found ? " FOUND" : " NOT_FOUND"));
                        break;
                    default:
                        throw new IllegalArgumentException("Erro na linha " + lineNumber + ": Operacao desconhecida.");
                }
            }
        } catch (java.io.IOException e) {
            throw new RuntimeException("Error processing trace files", e);
        }
    }
}
