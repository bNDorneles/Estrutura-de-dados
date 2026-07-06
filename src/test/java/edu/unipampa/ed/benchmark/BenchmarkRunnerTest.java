package edu.unipampa.ed.benchmark;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;

class BenchmarkRunnerTest {

    @Test
    void shouldExecuteBenchmarkAndExportCsvCorrectly(@TempDir Path tempDir) throws Exception {
        Path traceFile = tempDir.resolve("input.trace");
        Path csvFile = tempDir.resolve("results.csv");

        // Write small trace
        Files.writeString(traceFile, "I 10\nI 20\nS 10\nD 20\n");

        String[] args = {
            "--trace", traceFile.toString(),
            "--tree", "avl",
            "--out", csvFile.toString(),
            "--warmup", "1",
            "--iterations", "2"
        };

        BenchmarkRunner.main(args);

        assertTrue(Files.exists(csvFile));
        String content = Files.readString(csvFile);

        // Header Check
        assertTrue(content.contains("Configuracao,TotalOps,TamanhoFinal,JVM,SO,Memoria(MB),Media(ns),P50(ns),P99(ns)"));
        
        // Data Check
        // Should have at least one line of data
        String[] lines = content.split("\n");
        assertTrue(lines.length >= 2);
        
        String dataLine = lines[1];
        assertTrue(dataLine.contains("avl-" + traceFile.getFileName().toString())); // Config name
        assertTrue(dataLine.contains(",4,1,")); // Total ops = 4, Final size = 1 (after I 10, I 20, D 20 -> size 1)
    }
}
