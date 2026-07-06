package edu.unipampa.ed.benchmark;

import edu.unipampa.ed.api.OrderedLongSet;
import edu.unipampa.ed.avl.AugmentedAvlTree;
import edu.unipampa.ed.bst.UnbalancedBst;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

public class BenchmarkRunner {

    public static void main(String[] args) {
        String traceFile = null;
        String outFile = null;
        String treeType = null;
        int warmup = 3;
        int iterations = 10;

        for (int i = 0; i < args.length; i++) {
            if ("--trace".equals(args[i]) && i + 1 < args.length) traceFile = args[++i];
            else if ("--out".equals(args[i]) && i + 1 < args.length) outFile = args[++i];
            else if ("--tree".equals(args[i]) && i + 1 < args.length) treeType = args[++i];
            else if ("--warmup".equals(args[i]) && i + 1 < args.length) warmup = Integer.parseInt(args[++i]);
            else if ("--iterations".equals(args[i]) && i + 1 < args.length) iterations = Integer.parseInt(args[++i]);
        }

        if (traceFile == null || outFile == null || treeType == null) {
            throw new IllegalArgumentException("Missing arguments. Required: --trace <file> --out <file> --tree <avl|bst>");
        }

        // 1. Load entire trace into memory to separate I/O cost
        List<Command> commands = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(traceFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    commands.add(new Command(parts[0].charAt(0), Long.parseLong(parts[1])));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading trace file", e);
        }

        // 2. Warmup rounds
        for (int i = 0; i < warmup; i++) {
            OrderedLongSet tree = createTree(treeType);
            runCommands(tree, commands);
        }

        // 3. Benchmark rounds
        LatencyStats stats = new LatencyStats(iterations);
        long finalSize = 0;
        
        for (int i = 0; i < iterations; i++) {
            OrderedLongSet tree = createTree(treeType);
            long start = System.nanoTime();
            runCommands(tree, commands);
            long end = System.nanoTime();
            
            // Total time in nanoseconds
            long totalNanos = end - start;
            // Average time per operation in this run
            long nanosPerOp = totalNanos / Math.max(1, commands.size());
            
            stats.add(nanosPerOp);
            finalSize = tree.size();
        }

        // 4. Export CSV
        exportCsv(outFile, traceFile, treeType, commands.size(), finalSize, stats);
    }

    private static OrderedLongSet createTree(String treeType) {
        if ("avl".equals(treeType)) return new AugmentedAvlTree();
        if ("bst".equals(treeType)) return new UnbalancedBst();
        throw new IllegalArgumentException("Unknown tree type: " + treeType);
    }

    private static void runCommands(OrderedLongSet tree, List<Command> commands) {
        for (Command cmd : commands) {
            switch (cmd.type) {
                case 'I': tree.insert(cmd.key); break;
                case 'D': tree.delete(cmd.key); break;
                case 'S': tree.search(cmd.key); break;
            }
        }
    }

    private static void exportCsv(String outFile, String traceFile, String treeType, 
                                  int totalOps, long finalSize, LatencyStats stats) {
        Path path = Paths.get(outFile);
        boolean exists = Files.exists(path);
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            
            if (!exists) {
                writer.write("Configuracao,TotalOps,TamanhoFinal,JVM,SO,Memoria(MB),Media(ns),P50(ns),P99(ns)\n");
            }

            Path p = Paths.get(traceFile);
            String configName = treeType + "-" + p.getFileName().toString();
            String jvm = System.getProperty("java.version");
            String so = System.getProperty("os.name") + " " + System.getProperty("os.arch");
            long memoryMb = Runtime.getRuntime().maxMemory() / (1024 * 1024);

            writer.write(String.format("%s,%d,%d,%s,%s,%d,%.2f,%d,%d\n",
                    configName, totalOps, finalSize, jvm, so, memoryMb,
                    stats.mean(), stats.p50(), stats.p99()));
            
        } catch (IOException e) {
            throw new RuntimeException("Error writing CSV", e);
        }
    }
}
