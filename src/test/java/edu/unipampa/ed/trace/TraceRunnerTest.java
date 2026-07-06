package edu.unipampa.ed.trace;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;

class TraceRunnerTest {

    @Test
    void shouldThrowExceptionWhenArgumentsAreMissing() {
        String[] args = {"--trace", "input.trace"};
        
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TraceRunner.main(args)
        );
        
        assertTrue(exception.getMessage().contains("Missing arguments"));
    }

    @Test
    void shouldProcessCommandsCorrectly(@TempDir Path tempDir) throws Exception {
        Path traceFile = tempDir.resolve("input.trace");
        Path outFile = tempDir.resolve("output.txt");

        Files.writeString(traceFile, "I 10\nI 20\nS 10\nS 15\nD 20\nS 20\n");

        String[] args = {
            "--trace", traceFile.toString(),
            "--out", outFile.toString(),
            "--tree", "avl"
        };

        TraceRunner.main(args);

        String result = Files.readString(outFile);
        String ls = System.lineSeparator();
        String expected = "10 FOUND" + ls + "15 NOT_FOUND" + ls + "20 NOT_FOUND" + ls;
        
        assertEquals(expected, result);
    }

    @Test
    void shouldIgnoreEmptyLinesAndComments(@TempDir Path tempDir) throws Exception {
        Path traceFile = tempDir.resolve("input.trace");
        Path outFile = tempDir.resolve("output.txt");

        Files.writeString(traceFile, "\n# comment\nI 5\n\nS 5\n");

        String[] args = {
            "--trace", traceFile.toString(),
            "--out", outFile.toString(),
            "--tree", "avl"
        };

        TraceRunner.main(args);

        String result = Files.readString(outFile);
        String expected = "5 FOUND" + System.lineSeparator();
        assertEquals(expected, result);
    }

    @Test
    void shouldThrowExceptionWithLineNumberForInvalidOperation(@TempDir Path tempDir) throws Exception {
        Path traceFile = tempDir.resolve("input.trace");
        Path outFile = tempDir.resolve("output.txt");

        Files.writeString(traceFile, "I 10\nINVALID_OP 20\n");

        String[] args = {
            "--trace", traceFile.toString(),
            "--out", outFile.toString(),
            "--tree", "avl"
        };

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> TraceRunner.main(args)
        );
        
        assertTrue(exception.getMessage().contains("Erro na linha 2"), "Should indicate error on line 2");
    }

    @Test
    void shouldThrowUnsupportedOperationExceptionForBst(@TempDir Path tempDir) throws Exception {
        Path traceFile = tempDir.resolve("input.trace");
        Path outFile = tempDir.resolve("output.txt");

        Files.writeString(traceFile, "I 10\n");

        String[] args = {
            "--trace", traceFile.toString(),
            "--out", outFile.toString(),
            "--tree", "bst"
        };

        UnsupportedOperationException exception = assertThrows(
            UnsupportedOperationException.class,
            () -> TraceRunner.main(args)
        );
        
        assertTrue(exception.getMessage().contains("bst"));
    }
}
