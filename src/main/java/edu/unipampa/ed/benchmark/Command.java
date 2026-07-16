package edu.unipampa.ed.benchmark;

public class Command {
    public final char type;
    public final long key;

    public Command(char type, long key) {
        this.type = type;
        this.key = key;
    }
}
